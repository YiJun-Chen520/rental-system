package com.rental.service;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;

/**
 * 合同业务逻辑层接口
 */
public interface ContractService {

    /**
     * 创建合同
     * <p>创建合同的同时将对应房源状态改为"已租"，并创建押金费用记录</p>
     *
     * @param contract 合同信息
     * @return 创建后的合同对象
     */
    Contract create(Contract contract);

    /**
     * 更新合同信息
     *
     * @param contract 合同信息
     */
    void update(Contract contract);

    /**
     * 根据ID查询合同
     *
     * @param id 合同ID
     * @return 合同对象
     */
    Contract findById(int id);

    /**
     * 根据租户ID分页查询合同
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @param status      合同状态筛选（可为null）
     * @return 分页结果
     */
    PageResult<Contract> findByTenantId(int tenantId, PageRequest pageRequest, String status);

    /**
     * 根据房东ID分页查询合同（通过房源关联）
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param status      合同状态筛选（可为null）
     * @return 分页结果
     */
    PageResult<Contract> findByOwnerId(int ownerId, PageRequest pageRequest, String status);

    /**
     * 分页查询所有合同
     *
     * @param pageRequest 分页参数
     * @param status      合同状态筛选（可为null）
     * @return 分页结果
     */
    PageResult<Contract> findAll(PageRequest pageRequest, String status);

    /**
     * 终止合同
     * <p>将合同状态改为"已终止"，并将对应房源状态改回"空闲"</p>
     *
     * @param id 合同ID
     */
    void terminate(int id);

    /**
     * 合同到期处理
     * <p>将合同状态改为"到期"</p>
     *
     * @param id 合同ID
     */
    void expire(int id);

    /**
     * 统计合同数量
     *
     * @param status 合同状态筛选（可为null）
     * @return 合同数量
     */
    long count(String status);

    /**
     * 统计租户的合同数量
     *
     * @param tenantId 租户ID
     * @param status   合同状态筛选（可为null）
     * @return 合同数量
     */
    long countByTenantId(int tenantId, String status);

    /**
     * 统计房东的合同数量
     *
     * @param ownerId 房东ID
     * @param status  合同状态筛选（可为null）
     * @return 合同数量
     */
    long countByOwnerId(int ownerId, String status);
}
