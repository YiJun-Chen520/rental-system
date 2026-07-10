package com.rental.service;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;

import java.util.List;

/**
 * 费用业务逻辑层接口
 */
public interface PaymentService {

    /**
     * 添加费用记录
     *
     * @param payment 费用信息
     * @return 添加后的费用对象
     */
    Payment add(Payment payment);

    /**
     * 根据ID查询费用记录
     *
     * @param id 费用ID
     * @return 费用对象
     */
    Payment findById(int id);

    /**
     * 根据合同ID查询费用列表
     *
     * @param contractId 合同ID
     * @return 费用列表
     */
    List<Payment> findByContractId(int contractId);

    /**
     * 根据租户ID分页查询费用记录
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @param payStatus   支付状态筛选（可为null）
     * @return 分页结果
     */
    PageResult<Payment> findByTenantId(int tenantId, PageRequest pageRequest, String payStatus);

    /**
     * 根据房东ID分页查询费用记录
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param payStatus   支付状态筛选（可为null）
     * @return 分页结果
     */
    PageResult<Payment> findByOwnerId(int ownerId, PageRequest pageRequest, String payStatus);

    /**
     * 分页查询所有费用记录
     *
     * @param pageRequest 分页参数
     * @param payStatus   支付状态筛选（可为null）
     * @param paymentType 费用类型筛选（可为null）
     * @return 分页结果
     */
    PageResult<Payment> findAll(PageRequest pageRequest, String payStatus, String paymentType);

    /**
     * 模拟支付
     * <p>将支付状态更新为"已缴"，并设置支付日期为当前日期</p>
     *
     * @param id 费用ID
     */
    void pay(int id);

    /**
     * 统计费用记录数量
     *
     * @param payStatus 支付状态筛选（可为null）
     * @return 费用记录数量
     */
    long count(String payStatus);
}
