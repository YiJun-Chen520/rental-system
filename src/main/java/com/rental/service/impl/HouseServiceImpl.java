package com.rental.service.impl;

import com.rental.dao.HouseDao;
import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.House;
import com.rental.service.HouseService;

import java.math.BigDecimal;

/**
 * 房源业务逻辑层实现
 */
public class HouseServiceImpl implements HouseService {

    private HouseDao houseDao = new HouseDao();

    /**
     * 添加房源
     *
     * @param house 房源信息
     * @return 添加后的房源对象
     */
    @Override
    public House add(House house) {
        houseDao.insert(house);
        return house;
    }

    /**
     * 更新房源信息
     *
     * @param house 房源信息
     */
    @Override
    public void update(House house) {
        houseDao.update(house);
    }

    /**
     * 根据ID查询房源
     *
     * @param id 房源ID
     * @return 房源对象
     */
    @Override
    public House findById(int id) {
        return houseDao.findById(id);
    }

    /**
     * 根据房东ID分页查询房源
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param status      房源状态筛选（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<House> findByOwnerId(int ownerId, PageRequest pageRequest, String status) {
        return houseDao.findByOwnerId(ownerId, pageRequest, status);
    }

    /**
     * 分页查询所有房源
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（地址）
     * @param status      房源状态筛选（可为null）
     * @param minRent     最低租金（可为null）
     * @param maxRent     最高租金（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<House> findAll(PageRequest pageRequest, String keyword, String status, BigDecimal minRent, BigDecimal maxRent) {
        return houseDao.findAll(pageRequest, keyword, status, minRent, maxRent);
    }

    /**
     * 分页查询可租房源
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（地址）
     * @param minRent     最低租金（可为null）
     * @param maxRent     最高租金（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<House> findAvailable(PageRequest pageRequest, String keyword, BigDecimal minRent, BigDecimal maxRent) {
        return houseDao.findAll(pageRequest, keyword, "空闲", minRent, maxRent);
    }

    /**
     * 更新房源状态
     *
     * @param id     房源ID
     * @param status 房源状态（空闲/已租/维护中）
     */
    @Override
    public void updateStatus(int id, String status) {
        houseDao.updateStatus(id, status);
    }

    /**
     * 删除房源
     *
     * @param id 房源ID
     */
    @Override
    public void delete(int id) {
        houseDao.delete(id);
    }

    /**
     * 统计房东的房源数量
     *
     * @param ownerId 房东ID
     * @param status  房源状态筛选（可为null）
     * @return 房源数量
     */
    @Override
    public long countByOwnerId(int ownerId, String status) {
        return houseDao.countByOwnerId(ownerId, status);
    }

    /**
     * 统计房源数量
     *
     * @param keyword 搜索关键词（可为null）
     * @param status  房源状态筛选（可为null）
     * @return 房源数量
     */
    @Override
    public long count(String keyword, String status) {
        return houseDao.count(keyword, status);
    }
}
