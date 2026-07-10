package com.rental.service.impl;

import com.rental.dao.ContractDao;
import com.rental.dao.HouseDao;
import com.rental.dao.PaymentDao;
import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.entity.Payment;
import com.rental.service.ContractService;

import java.time.LocalDate;

/**
 * 合同业务逻辑层实现
 */
public class ContractServiceImpl implements ContractService {

    private ContractDao contractDao = new ContractDao();
    private HouseDao houseDao = new HouseDao();
    private PaymentDao paymentDao = new PaymentDao();

    /**
     * 创建合同
     * <p>执行以下操作：</p>
     * <ol>
     *   <li>插入合同记录</li>
     *   <li>将对应房源状态改为"已租"</li>
     *   <li>创建押金费用记录</li>
     * </ol>
     *
     * @param contract 合同信息
     * @return 创建后的合同对象
     */
    @Override
    public Contract create(Contract contract) {
        // 设置签订日期为当前日期
        contract.setSignDate(LocalDate.now());
        contractDao.insert(contract);

        // 将房源状态改为"已租"
        houseDao.updateStatus(contract.getHouseID(), "已租");

        // 创建押金费用记录
        Payment depositPayment = new Payment();
        depositPayment.setContractID(contract.getContractID());
        depositPayment.setPaymentType("押金");
        depositPayment.setAmount(contract.getDeposit());
        depositPayment.setPayStatus("未缴");
        paymentDao.insert(depositPayment);

        return contract;
    }

    /**
     * 更新合同信息
     *
     * @param contract 合同信息
     */
    @Override
    public void update(Contract contract) {
        contractDao.update(contract);
    }

    /**
     * 根据ID查询合同
     *
     * @param id 合同ID
     * @return 合同对象
     */
    @Override
    public Contract findById(int id) {
        return contractDao.findById(id);
    }

    /**
     * 根据租户ID分页查询合同
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @param status      合同状态筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<Contract> findByTenantId(int tenantId, PageRequest pageRequest, String status) {
        return contractDao.findByTenantId(tenantId, pageRequest, status);
    }

    /**
     * 根据房东ID分页查询合同
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param status      合同状态筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<Contract> findByOwnerId(int ownerId, PageRequest pageRequest, String status) {
        return contractDao.findByOwnerId(ownerId, pageRequest, status);
    }

    /**
     * 分页查询所有合同
     *
     * @param pageRequest 分页参数
     * @param status      合同状态筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<Contract> findAll(PageRequest pageRequest, String status) {
        return contractDao.findAll(pageRequest, status);
    }

    /**
     * 终止合同
     * <p>将合同状态改为"已终止"，并将对应房源状态改回"空闲"</p>
     *
     * @param id 合同ID
     */
    @Override
    public void terminate(int id) {
        Contract contract = contractDao.findById(id);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        // 更新合同状态为"已终止"
        contractDao.updateStatus(id, "已终止");
        // 将房源状态改回"空闲"
        houseDao.updateStatus(contract.getHouseID(), "空闲");
    }

    /**
     * 合同到期处理
     * <p>将合同状态改为"到期"</p>
     *
     * @param id 合同ID
     */
    @Override
    public void expire(int id) {
        contractDao.updateStatus(id, "到期");
    }

    /**
     * 统计合同数量
     *
     * @param status 合同状态筛选（可为null）
     * @return 合同数量
     */
    @Override
    public long count(String status) {
        return contractDao.count(status);
    }

    /**
     * 统计租户的合同数量
     *
     * @param tenantId 租户ID
     * @param status   合同状态筛选（可为null）
     * @return 合同数量
     */
    @Override
    public long countByTenantId(int tenantId, String status) {
        return contractDao.countByTenantId(tenantId, status);
    }

    /**
     * 统计房东的合同数量
     *
     * @param ownerId 房东ID
     * @param status  合同状态筛选（可为null）
     * @return 合同数量
     */
    @Override
    public long countByOwnerId(int ownerId, String status) {
        return contractDao.countByOwnerId(ownerId, status);
    }
}
