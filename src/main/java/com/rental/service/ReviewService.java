package com.rental.service;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Review;

/**
 * 评价业务逻辑层接口
 */
public interface ReviewService {

    /**
     * 添加评价
     * <p>自动设置评价日期为当前日期</p>
     *
     * @param review 评价信息
     * @return 添加后的评价对象
     */
    Review add(Review review);

    /**
     * 更新评价
     *
     * @param review 评价信息
     */
    void update(Review review);

    /**
     * 删除评价
     *
     * @param id 评价ID
     */
    void delete(int id);

    /**
     * 根据ID查询评价
     *
     * @param id 评价ID
     * @return 评价对象
     */
    Review findById(int id);

    /**
     * 根据房源ID分页查询评价
     *
     * @param houseId     房源ID
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    PageResult<Review> findByHouseId(int houseId, PageRequest pageRequest);

    /**
     * 根据租户ID分页查询评价
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    PageResult<Review> findByTenantId(int tenantId, PageRequest pageRequest);

    /**
     * 分页查询所有评价
     *
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    PageResult<Review> findAll(PageRequest pageRequest);

    /**
     * 统计房源的评价数量
     *
     * @param houseId 房源ID
     * @return 评价数量
     */
    long countByHouseId(int houseId);
}
