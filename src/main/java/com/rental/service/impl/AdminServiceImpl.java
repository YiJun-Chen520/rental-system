package com.rental.service.impl;

import com.rental.dao.AdminDao;
import com.rental.entity.Admin;
import com.rental.service.AdminService;
import com.rental.util.EncryptUtil;

/**
 * 管理员业务逻辑层实现
 */
public class AdminServiceImpl implements AdminService {

    private AdminDao adminDao = new AdminDao();

    /**
     * 管理员登录
     * <p>根据用户名查询管理员，验证密码是否匹配</p>
     *
     * @param username 用户名
     * @param password 密码
     * @return 管理员对象，验证失败返回null
     */
    @Override
    public Admin login(String username, String password) {
        Admin admin = adminDao.findByUsername(username);
        if (admin == null) {
            return null;
        }
        if (EncryptUtil.verify(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }

    /**
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员对象
     */
    @Override
    public Admin findById(int id) {
        return adminDao.findById(id);
    }
}
