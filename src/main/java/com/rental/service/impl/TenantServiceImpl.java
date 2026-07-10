package com.rental.service.impl;

import com.rental.dao.TenantDao;
import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Tenant;
import com.rental.service.TenantService;
import com.rental.util.EncryptUtil;

/**
 * 租户业务逻辑层实现
 */
public class TenantServiceImpl implements TenantService {

    private TenantDao tenantDao = new TenantDao();

    /**
     * 租户注册
     * <p>检查手机号和身份证号唯一性，加密密码后插入数据库</p>
     *
     * @param tenant 租户信息
     * @return 注册后的租户对象
     * @throws RuntimeException 手机号或身份证号已存在时抛出异常
     */
    @Override
    public Tenant register(Tenant tenant) {
        // 检查手机号唯一性
        Tenant existing = tenantDao.findByPhone(tenant.getPhone());
        if (existing != null) {
            throw new RuntimeException("该手机号已注册");
        }
        // 检查身份证号唯一性
        existing = tenantDao.findByIDCard(tenant.getIDCard());
        if (existing != null) {
            throw new RuntimeException("该身份证号已注册");
        }
        // 加密密码
        tenant.setPassword(EncryptUtil.encrypt(tenant.getPassword()));
        tenantDao.insert(tenant);
        return tenant;
    }

    /**
     * 租户登录
     * <p>根据手机号查询租户，验证密码是否匹配</p>
     *
     * @param phone    手机号
     * @param password 密码
     * @return 租户对象，验证失败返回null
     */
    @Override
    public Tenant login(String phone, String password) {
        Tenant tenant = tenantDao.findByPhone(phone);
        if (tenant == null) {
            return null;
        }
        if (EncryptUtil.verify(password, tenant.getPassword())) {
            return tenant;
        }
        return null;
    }

    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户对象
     */
    @Override
    public Tenant findById(int id) {
        return tenantDao.findById(id);
    }

    /**
     * 更新租户信息
     * <p>如果密码为空则不更新密码字段</p>
     *
     * @param tenant 租户信息
     */
    @Override
    public void update(Tenant tenant) {
        // 如果密码为空，说明不需要更新密码
        if (tenant.getPassword() == null || tenant.getPassword().isEmpty()) {
            tenant.setPassword(null);
        } else {
            // 加密新密码
            tenant.setPassword(EncryptUtil.encrypt(tenant.getPassword()));
        }
        tenantDao.update(tenant);
    }

    /**
     * 分页查询租户列表
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（姓名或手机号）
     * @return 分页结果
     */
    @Override
    public PageResult<Tenant> findAll(PageRequest pageRequest, String keyword) {
        return tenantDao.findAll(pageRequest, keyword);
    }

    /**
     * 更新租户状态
     *
     * @param id     租户ID
     * @param status 状态（1-正常，0-禁用）
     */
    @Override
    public void updateStatus(int id, int status) {
        tenantDao.updateStatus(id, status);
    }

    /**
     * 删除租户
     *
     * @param id 租户ID
     */
    @Override
    public void delete(int id) {
        tenantDao.delete(id);
    }
}
