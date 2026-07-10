package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;
import com.rental.service.PaymentService;
import com.rental.service.impl.PaymentServiceImpl;
import com.rental.util.JsonUtil;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理端 - 费用管理接口
 * <p>
 * 处理费用列表查询、详情查看、添加费用记录和标记已缴操作。
 * </p>
 */
@WebServlet("/api/admin/payment")
public class AdminPaymentServlet extends HttpServlet {

    private PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = ServletUtil.getStringParam(request, "action", "");
            switch (action) {
                case "detail":
                    handleDetail(request, response);
                    break;
                default:
                    handleList(request, response);
                    break;
            }
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleAdd(request, response);
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = ServletUtil.getStringParam(request, "action", "");
            switch (action) {
                case "pay":
                    handlePay(request, response);
                    break;
                default:
                    ResultUtil.writeError(response, 400, "未知操作");
                    break;
            }
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 费用列表（分页 + 支付状态筛选 + 费用类型筛选）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String payStatus = ServletUtil.getStringParam(request, "payStatus", null);
        String paymentType = ServletUtil.getStringParam(request, "paymentType", null);

        PageResult<Payment> pageResult = paymentService.findAll(pageRequest, payStatus, paymentType);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 费用详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
    }

    /**
     * 添加费用记录
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = JsonUtil.readJson(request);
        Payment payment = JsonUtil.fromJson(json, Payment.class);

        if (payment.getContractID() <= 0) {
            ResultUtil.writeError(response, 400, "合同ID不能为空");
            return;
        }
        if (payment.getPaymentType() == null || payment.getPaymentType().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "费用类型不能为空");
            return;
        }
        if (payment.getAmount() == null) {
            ResultUtil.writeError(response, 400, "金额不能为空");
            return;
        }

        Payment savedPayment = paymentService.add(payment);
        ResultUtil.writeSuccess(response, savedPayment);
    }

    /**
     * 标记已缴
     */
    private void handlePay(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少费用ID");
            return;
        }

        paymentService.pay(id);
        ResultUtil.writeSuccess(response);
    }
}
