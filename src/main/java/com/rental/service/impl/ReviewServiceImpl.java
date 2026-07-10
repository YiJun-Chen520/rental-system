package com.rental.service.impl;

import com.rental.dao.ReviewDao;
import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Review;
import com.rental.service.ReviewService;

import java.time.LocalDate;

/**
 * 评价业务逻辑层实现
 */
public class ReviewServiceImpl implements ReviewService {

    private ReviewDao reviewDao = new ReviewDao();

    /**
     * 添加评价
     * <p>自动设置评价日期为当前日期后插入数据库</p>
     *
     * @param review 评价信息
     * @return 添加后的评价对象
     */
    @Override
    public Review add(Review review) {
        review.setReviewDate(LocalDate.now());
        reviewDao.insert(review);
        return review;
    }

    /**
     * 更新评价
     *
     * @param review 评价信息
     */
    @Override
    public void update(Review review) {
        reviewDao.update(review);
    }

    /**
     * 删除评价
     *
     * @param id 评价ID
     */
    @Override
    public void delete(int id) {
        reviewDao.delete(id);
    }

    /**
     * 根据ID查询评价
     *
     * @param id 评价ID
     * @return 评价对象
     */
    @Override
    public Review findById(int id) {
        return reviewDao.findById(id);
    }

    /**
     * 根据房源ID分页查询评价
     *
     * @param houseId     房源ID
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<Review> findByHouseId(int houseId, PageRequest pageRequest) {
        return reviewDao.findByHouseId(houseId, pageRequest);
    }

    /**
     * 根据租户ID分页查询评价
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<Review> findByTenantId(int tenantId, PageRequest pageRequest) {
        return reviewDao.findByTenantId(tenantId, pageRequest);
    }

    /**
     * 分页查询所有评价
     *
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<Review> findAll(PageRequest pageRequest) {
        return reviewDao.findAll(pageRequest);
    }

    /**
     * 统计房源的评价数量
     *
     * @param houseId 房源ID
     * @return 评价数量
     */
    @Override
    public long countByHouseId(int houseId) {
        return reviewDao.countByHouseId(houseId);
    }
}
