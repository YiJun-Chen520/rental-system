package com.rental.service;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Tenant;

/**
 * 租户业务逻辑层接口
 */
public interface TenantService {

    /**
     * 租户注册
     *
     * @param tenant 租户信息
     * @return 注册后的租户对象
     * @throws RuntimeException 手机号或身份证号已存在时抛出异常
     */
    Tenant register(Tenant tenant);

    /**
     * 租户登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 租户对象，验证失败返回null
     */
    Tenant login(String phone, String password);

    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户对象
     */
    Tenant findById(int id);

    /**
     * 更新租户信息
     *
     * @param tenant 租户信息
     */
    void update(Tenant tenant);

    /**
     * 分页查询租户列表
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（姓名或手机号）
     * @return 分页结果
     */
    PageResult<Tenant> findAll(PageRequest pageRequest, String keyword);

    /**
     * 更新租户状态
     *
     * @param id     租户ID
     * @param status 状态（1-正常，0-禁用）
     */
    void updateStatus(int id, int status);

    /**
     * 删除租户
     *
     * @param id 租户ID
     */
    void delete(int id);
}
