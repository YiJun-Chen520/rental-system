package com.rental.servlet.tenant;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;
import com.rental.service.PaymentService;
import com.rental.service.impl.PaymentServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

/**
 * 租户费用接口
 * <p>
 * 提供费用列表查询、费用详情查看和模拟缴费功能。
 * </p>
 */
@WebServlet("/api/tenant/payment")
public class TenantPaymentServlet extends HttpServlet {

    private PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            handleDetail(request, response);
        } else {
            handleList(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("pay".equals(action)) {
            handlePay(request, response);
        } else {
            ResultUtil.writeError(response, 400, "不支持的操作");
        }
    }

    /**
     * 我的费用列表
     * <p>根据租户ID分页查询费用记录，支持按支付状态筛选</p>
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");

            // 解析分页参数
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
            pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));

            // 解析支付状态筛选参数
            String payStatus = ServletUtil.getStringParam(request, "payStatus", null);

            PageResult<Payment> pageResult = paymentService.findByTenantId(tenantId, pageRequest, payStatus);
            ResultUtil.writePageResult(response, pageResult);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询费用列表失败: " + e.getMessage());
        }
    }

    /**
     * 费用详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.getIntParam(request, "id", -1);
            if (id == -1) {
                ResultUtil.writeError(response, 400, "缺少费用ID");
                return;
            }
            Payment payment = paymentService.findById(id);
            if (payment == null) {
                ResultUtil.writeError(response, 404, "费用记录不存在");
                return;
            }
            ResultUtil.writeSuccess(response, payment);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询费用详情失败: " + e.getMessage());
        }
    }

    /**
     * 模拟缴费
     * <p>将指定费用的支付状态更新为"已缴"</p>
     */
    private void handlePay(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = ServletUtil.getIntParam(request, "id", -1);
            if (id == -1) {
                ResultUtil.writeError(response, 400, "缺少费用ID");
                return;
            }
            paymentService.pay(id);
            ResultUtil.writeSuccess(response, "缴费成功");
        } catch (Exception e) {
            ResultUtil.writeError(response, "缴费失败: " + e.getMessage());
        }
    }
}
