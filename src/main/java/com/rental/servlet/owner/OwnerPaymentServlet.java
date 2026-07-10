package com.rental.servlet.owner;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.entity.House;
import com.rental.entity.Payment;
import com.rental.service.ContractService;
import com.rental.service.HouseService;
import com.rental.service.PaymentService;
import com.rental.service.impl.ContractServiceImpl;
import com.rental.service.impl.HouseServiceImpl;
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
 * 房东费用管理接口
 * <p>
 * 处理房东对费用记录的查询和添加。
 * </p>
 */
@WebServlet("/api/owner/payment")
public class OwnerPaymentServlet extends HttpServlet {

    private PaymentService paymentService = new PaymentServiceImpl();
    private ContractService contractService = new ContractServiceImpl();
    private HouseService houseService = new HouseServiceImpl();

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

    /**
     * 获取房东的费用列表（分页）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String payStatus = ServletUtil.getStringParam(request, "payStatus", null);

        PageResult<Payment> pageResult = paymentService.findByOwnerId(ownerId, pageRequest, payStatus);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 获取费用详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的费用ID");
            return;
        }

        Payment payment = paymentService.findById(id);
        if (payment == null) {
            ResultUtil.writeError(response, 404, "费用记录不存在");
            return;
        }

        // 验证费用关联的合同归属当前房东
        if (!verifyPaymentOwner(payment, ownerId)) {
            ResultUtil.writeError(response, 403, "无权访问该费用记录");
            return;
        }

        ResultUtil.writeSuccess(response, payment);
    }

    /**
     * 添加费用记录
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        String json = JsonUtil.readJson(request);
        Payment payment = JsonUtil.fromJson(json, Payment.class);

        if (payment.getContractID() <= 0) {
            ResultUtil.writeError(response, 400, "无效的合同ID");
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

        // 验证合同归属
        Contract contract = contractService.findById(payment.getContractID());
        if (contract == null) {
            ResultUtil.writeError(response, 404, "合同不存在");
            return;
        }
        House house = houseService.findById(contract.getHouseID());
        if (house == null || house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权为该合同添加费用");
            return;
        }

        try {
            Payment addedPayment = paymentService.add(payment);
            ResultUtil.writeSuccess(response, addedPayment);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }

    /**
     * 验证费用记录是否归属指定房东
     *
     * @param payment 费用记录
     * @param ownerId 房东ID
     * @return 是否归属
     */
    private boolean verifyPaymentOwner(Payment payment, int ownerId) {
        Contract contract = contractService.findById(payment.getContractID());
        if (contract == null) {
            return false;
        }
        House house = houseService.findById(contract.getHouseID());
        return house != null && house.getOwnerID() == ownerId;
    }
}
