package com.rental.servlet.tenant;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;
import com.rental.service.ContractService;
import com.rental.service.PaymentService;
import com.rental.service.impl.ContractServiceImpl;
import com.rental.service.impl.PaymentServiceImpl;
import com.rental.util.ResultUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 租户统计数据接口
 * <p>
 * 提供租户的统计数据，包括生效合同数、未缴费数和已缴费总额。
 * </p>
 */
@WebServlet("/api/tenant/stats")
public class TenantStatsServlet extends HttpServlet {

    private ContractService contractService = new ContractServiceImpl();
    private PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");

            // 生效合同数
            long activeContracts = contractService.countByTenantId(tenantId, "生效");

            // 未缴费数量
            PageRequest unpaidRequest = new PageRequest();
            unpaidRequest.setPage(1);
            unpaidRequest.setPageSize(1);
            PageResult<Payment> unpaidResult = paymentService.findByTenantId(tenantId, unpaidRequest, "未缴");
            long unpaidPayments = unpaidResult.getTotal();

            // 已缴费总额：查询所有已缴费用并累加金额
            BigDecimal totalPaid = calculateTotalPaid(tenantId);

            // 组装统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeContracts", activeContracts);
            stats.put("unpaidPayments", unpaidPayments);
            stats.put("totalPaid", totalPaid);

            ResultUtil.writeSuccess(response, stats);
        } catch (Exception e) {
            ResultUtil.writeError(response, "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 计算租户已缴费总额
     * <p>分页查询所有已缴费用，累加金额</p>
     *
     * @param tenantId 租户ID
     * @return 已缴费总额
     */
    private BigDecimal calculateTotalPaid(int tenantId) {
        BigDecimal totalPaid = BigDecimal.ZERO;
        int page = 1;
        int pageSize = 100;
        while (true) {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(page);
            pageRequest.setPageSize(pageSize);
            PageResult<Payment> paidResult = paymentService.findByTenantId(tenantId, pageRequest, "已缴");
            if (paidResult.getList() == null || paidResult.getList().isEmpty()) {
                break;
            }
            for (Payment payment : paidResult.getList()) {
                if (payment.getAmount() != null) {
                    totalPaid = totalPaid.add(payment.getAmount());
                }
            }
            // 如果已经是最后一页，退出循环
            if (page >= paidResult.getTotalPages()) {
                break;
            }
            page++;
        }
        return totalPaid;
    }
}
