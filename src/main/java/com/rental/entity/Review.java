package com.rental.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
public class Review {

    /** 评价ID */
    private int reviewID;

    /** 租户ID */
    private int tenantID;

    /** 房源ID */
    private int houseID;

    /** 评分（1-5） */
    private int rating;

    /** 评价内容 */
    private String content;

    /** 租户姓名（JOIN查询时填充，非数据库字段） */
    private String tenantName;

    /** 评价日期 */
    private LocalDate reviewDate;

    /** 创建时间 */
    private LocalDateTime createTime;

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getHouseID() {
        return houseID;
    }

    public void setHouseID(int houseID) {
        this.houseID = houseID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
