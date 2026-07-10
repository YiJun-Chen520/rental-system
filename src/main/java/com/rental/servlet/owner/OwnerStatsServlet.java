package com.rental.servlet.owner;

import com.rental.service.ContractService;
import com.rental.service.HouseService;
import com.rental.service.PaymentService;
import com.rental.service.impl.ContractServiceImpl;
import com.rental.service.impl.HouseServiceImpl;
import com.rental.service.impl.PaymentServiceImpl;
import com.rental.util.ResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 房东统计数据接口
 * <p>
 * 提供房东的房源、合同和费用相关统计数据。
 * </p>
 */
@WebServlet("/api/owner/stats")
public class OwnerStatsServlet extends HttpServlet {

    private HouseService houseService = new HouseServiceImpl();
    private ContractService contractService = new ContractServiceImpl();
    private PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int ownerId = (int) request.getAttribute("userId");

            // 房源统计
            long totalHouses = houseService.countByOwnerId(ownerId, null);
            long vacantHouses = houseService.countByOwnerId(ownerId, "空闲");
            long rentedHouses = houseService.countByOwnerId(ownerId, "已租");

            // 合同统计（生效中的合同）
            long activeContracts = contractService.countByOwnerId(ownerId, "生效");

            // 未缴费用总额统计（通过PaymentService获取未缴费列表并计算总额）
            // 这里使用countByOwnerId方法近似获取未缴数量，实际未缴金额需要通过列表计算
            // 为简化实现，通过查询未缴记录来计算
            com.rental.dto.PageRequest pageRequest = new com.rental.dto.PageRequest();
            pageRequest.setPage(1);
            pageRequest.setPageSize(Integer.MAX_VALUE);
            com.rental.dto.PageResult<com.rental.entity.Payment> unpaidPage =
                    paymentService.findByOwnerId(ownerId, pageRequest, "未缴");

            java.math.BigDecimal unpaidAmount = java.math.BigDecimal.ZERO;
            for (com.rental.entity.Payment payment : unpaidPage.getList()) {
                if (payment.getAmount() != null) {
                    unpaidAmount = unpaidAmount.add(payment.getAmount());
                }
            }

            // 组装统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalHouses", totalHouses);
            stats.put("vacantHouses", vacantHouses);
            stats.put("rentedHouses", rentedHouses);
            stats.put("activeContracts", activeContracts);
            stats.put("unpaidAmount", unpaidAmount);

            ResultUtil.writeSuccess(response, stats);
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }
}
