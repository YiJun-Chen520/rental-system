package com.rental.service.impl;

import com.rental.dao.OwnerDao;
import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Owner;
import com.rental.service.OwnerService;
import com.rental.util.EncryptUtil;

/**
 * 房东业务逻辑层实现
 */
public class OwnerServiceImpl implements OwnerService {

    private OwnerDao ownerDao = new OwnerDao();

    /**
     * 房东注册
     * <p>检查手机号和身份证号唯一性，加密密码后插入数据库</p>
     *
     * @param owner 房东信息
     * @return 注册后的房东对象
     * @throws RuntimeException 手机号或身份证号已存在时抛出异常
     */
    @Override
    public Owner register(Owner owner) {
        // 检查手机号唯一性
        Owner existing = ownerDao.findByPhone(owner.getPhone());
        if (existing != null) {
            throw new RuntimeException("该手机号已注册");
        }
        // 检查身份证号唯一性
        existing = ownerDao.findByIdCard(owner.getIDCard());
        if (existing != null) {
            throw new RuntimeException("该身份证号已注册");
        }
        // 加密密码
        owner.setPassword(EncryptUtil.encrypt(owner.getPassword()));
        ownerDao.insert(owner);
        return owner;
    }

    /**
     * 房东登录
     * <p>根据手机号查询房东，验证密码是否匹配</p>
     *
     * @param phone    手机号
     * @param password 密码
     * @return 房东对象，验证失败返回null
     */
    @Override
    public Owner login(String phone, String password) {
        Owner owner = ownerDao.findByPhone(phone);
        if (owner == null) {
            return null;
        }
        if (EncryptUtil.verify(password, owner.getPassword())) {
            return owner;
        }
        return null;
    }

    /**
     * 根据ID查询房东
     *
     * @param id 房东ID
     * @return 房东对象
     */
    @Override
    public Owner findById(int id) {
        return ownerDao.findById(id);
    }

    /**
     * 更新房东信息
     * <p>如果密码为空则不更新密码字段</p>
     *
     * @param owner 房东信息
     */
    @Override
    public void update(Owner owner) {
        // 如果密码为空，说明不需要更新密码
        if (owner.getPassword() == null || owner.getPassword().isEmpty()) {
            owner.setPassword(null);
        } else {
            // 加密新密码
            owner.setPassword(EncryptUtil.encrypt(owner.getPassword()));
        }
        ownerDao.update(owner);
    }

    /**
     * 分页查询房东列表
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（姓名或手机号）
     * @return 分页结果
     */
    @Override
    public PageResult<Owner> findAll(PageRequest pageRequest, String keyword) {
        return ownerDao.findAll(pageRequest, keyword);
    }

    /**
     * 更新房东状态
     *
     * @param id     房东ID
     * @param status 状态（1-正常，0-禁用）
     */
    @Override
    public void updateStatus(int id, int status) {
        ownerDao.updateStatus(id, status);
    }

    /**
     * 删除房东
     *
     * @param id 房东ID
     */
    @Override
    public void delete(int id) {
        ownerDao.delete(id);
    }
}
