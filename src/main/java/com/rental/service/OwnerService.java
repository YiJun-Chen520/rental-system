package com.rental.service;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Owner;

/**
 * 房东业务逻辑层接口
 */
public interface OwnerService {

    /**
     * 房东注册
     *
     * @param owner 房东信息
     * @return 注册后的房东对象
     * @throws RuntimeException 手机号或身份证号已存在时抛出异常
     */
    Owner register(Owner owner);

    /**
     * 房东登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 房东对象，验证失败返回null
     */
    Owner login(String phone, String password);

    /**
     * 根据ID查询房东
     *
     * @param id 房东ID
     * @return 房东对象
     */
    Owner findById(int id);

    /**
     * 更新房东信息
     *
     * @param owner 房东信息
     */
    void update(Owner owner);

    /**
     * 分页查询房东列表
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（姓名或手机号）
     * @return 分页结果
     */
    PageResult<Owner> findAll(PageRequest pageRequest, String keyword);

    /**
     * 更新房东状态
     *
     * @param id     房东ID
     * @param status 状态（1-正常，0-禁用）
     */
    void updateStatus(int id, int status);

    /**
     * 删除房东
     *
     * @param id 房东ID
     */
    void delete(int id);
}
