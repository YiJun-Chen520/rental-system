package com.rental.service;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.House;

import java.math.BigDecimal;

/**
 * 房源业务逻辑层接口
 */
public interface HouseService {

    /**
     * 添加房源
     *
     * @param house 房源信息
     * @return 添加后的房源对象
     */
    House add(House house);

    /**
     * 更新房源信息
     *
     * @param house 房源信息
     */
    void update(House house);

    /**
     * 根据ID查询房源
     *
     * @param id 房源ID
     * @return 房源对象
     */
    House findById(int id);

    /**
     * 根据房东ID分页查询房源
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param status      房源状态筛选（可为null）
     * @return 分页结果
     */
    PageResult<House> findByOwnerId(int ownerId, PageRequest pageRequest, String status);

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
    PageResult<House> findAll(PageRequest pageRequest, String keyword, String status, BigDecimal minRent, BigDecimal maxRent);

    /**
     * 分页查询可租房源（面向租户）
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键词（地址）
     * @param minRent     最低租金（可为null）
     * @param maxRent     最高租金（可为null）
     * @return 分页结果
     */
    PageResult<House> findAvailable(PageRequest pageRequest, String keyword, BigDecimal minRent, BigDecimal maxRent);

    /**
     * 更新房源状态
     *
     * @param id     房源ID
     * @param status 房源状态（空闲/已租/维护中）
     */
    void updateStatus(int id, String status);

    /**
     * 删除房源
     *
     * @param id 房源ID
     */
    void delete(int id);

    /**
     * 统计房东的房源数量
     *
     * @param ownerId 房东ID
     * @param status  房源状态筛选（可为null）
     * @return 房源数量
     */
    long countByOwnerId(int ownerId, String status);

    /**
     * 统计房源数量
     *
     * @param keyword 搜索关键词（可为null）
     * @param status  房源状态筛选（可为null）
     * @return 房源数量
     */
    long count(String keyword, String status);
}
