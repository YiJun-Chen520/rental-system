package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;
import com.rental.service.*;
import com.rental.service.impl.*;
import com.rental.util.ResultUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理端 - 系统统计接口
 * <p>
 * 提供系统整体统计数据，包括房东数、租户数、房源数、合同数、费用统计等。
 * </p>
 */
@WebServlet("/api/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    private OwnerService ownerService = new OwnerServiceImpl();
    private TenantService tenantService = new TenantServiceImpl();
    private HouseService houseService = new HouseServiceImpl();
    private ContractService contractService = new ContractServiceImpl();
    private PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleStats(request, response);
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 获取系统统计数据
     */
    private void handleStats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> stats = new HashMap<>();

        // 房东总数
        PageRequest countRequest = new PageRequest();
        countRequest.setPage(1);
        countRequest.setPageSize(1);
        PageResult<?> ownerPage = ownerService.findAll(countRequest, null);
        stats.put("totalOwners", ownerPage.getTotal());

        // 租户总数
        PageResult<?> tenantPage = tenantService.findAll(countRequest, null);
        stats.put("totalTenants", tenantPage.getTotal());

        // 房源总数
        long totalHouses = houseService.count(null, null);
        stats.put("totalHouses", totalHouses);

        // 空闲房源数
        long vacantHouses = houseService.count(null, "空闲");
        stats.put("vacantHouses", vacantHouses);

        // 已租房源数
        long rentedHouses = houseService.count(null, "已租");
        stats.put("rentedHouses", rentedHouses);

        // 生效合同数
        long activeContracts = contractService.count("生效");
        stats.put("activeContracts", activeContracts);

        // 未缴费用数
        long unpaidPayments = paymentService.count("未缴");
        stats.put("unpaidPayments", unpaidPayments);

        // 已缴费用总额（总收入）
        BigDecimal totalRevenue = calculateTotalRevenue();
        stats.put("totalRevenue", totalRevenue);

        ResultUtil.writeSuccess(response, stats);
    }

    /**
     * 计算已缴费用总额
     */
    private BigDecimal calculateTotalRevenue() {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        // 查询所有已缴费用记录
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setPageSize(10000);
        PageResult<Payment> paymentPage = paymentService.findAll(pageRequest, "已缴", null);
        if (paymentPage.getList() != null) {
            for (Payment payment : paymentPage.getList()) {
                if (payment.getAmount() != null) {
                    totalRevenue = totalRevenue.add(payment.getAmount());
                }
            }
        }
        return totalRevenue;
    }
}
