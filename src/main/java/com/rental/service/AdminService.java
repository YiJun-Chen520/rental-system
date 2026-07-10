package com.rental.service;

import com.rental.entity.Admin;

/**
 * 管理员业务逻辑层接口
 */
public interface AdminService {

    /**
     * 管理员登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 管理员对象，验证失败返回null
     */
    Admin login(String username, String password);

    /**
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员对象
     */
    Admin findById(int id);
}
