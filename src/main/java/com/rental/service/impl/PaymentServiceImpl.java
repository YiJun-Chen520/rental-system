package com.rental.service.impl;

import com.rental.dao.PaymentDao;
import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;
import com.rental.service.PaymentService;

import java.time.LocalDate;
import java.util.List;

/**
 * 费用业务逻辑层实现
 */
public class PaymentServiceImpl implements PaymentService {

    private PaymentDao paymentDao = new PaymentDao();

    /**
     * 添加费用记录
     *
     * @param payment 费用信息
     * @return 添加后的费用对象
     */
    @Override
    public Payment add(Payment payment) {
        paymentDao.insert(payment);
        return payment;
    }

    /**
     * 根据ID查询费用记录
     *
     * @param id 费用ID
     * @return 费用对象
     */
    @Override
    public Payment findById(int id) {
        return paymentDao.findById(id);
    }

    /**
     * 根据合同ID查询费用列表
     *
     * @param contractId 合同ID
     * @return 费用列表
     */
    @Override
    public List<Payment> findByContractId(int contractId) {
        return paymentDao.findByContractId(contractId);
    }

    /**
     * 根据租户ID分页查询费用记录
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @param payStatus   支付状态筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<Payment> findByTenantId(int tenantId, PageRequest pageRequest, String payStatus) {
        return paymentDao.findByTenantId(tenantId, pageRequest, payStatus);
    }

    /**
     * 根据房东ID分页查询费用记录
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param payStatus   支付状态筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<Payment> findByOwnerId(int ownerId, PageRequest pageRequest, String payStatus) {
        return paymentDao.findByOwnerId(ownerId, pageRequest, payStatus);
    }

    /**
     * 分页查询所有费用记录
     *
     * @param pageRequest 分页参数
     * @param payStatus   支付状态筛选（可为null）
     * @param paymentType 费用类型筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<Payment> findAll(PageRequest pageRequest, String payStatus, String paymentType) {
        return paymentDao.findAll(pageRequest, payStatus, paymentType);
    }

    /**
     * 模拟支付
     * <p>将支付状态更新为"已缴"，并设置支付日期为当前日期</p>
     *
     * @param id 费用ID
     */
    @Override
    public void pay(int id) {
        paymentDao.updatePayStatus(id, "已缴", LocalDate.now());
    }

    /**
     * 统计费用记录数量
     *
     * @param payStatus 支付状态筛选（可为null）
     * @return 费用记录数量
     */
    @Override
    public long count(String payStatus) {
        return paymentDao.count(payStatus);
    }
}
