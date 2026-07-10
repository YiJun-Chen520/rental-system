package com.rental.entity;

import java.time.LocalDateTime;

/**
 * 租户实体类
 */
public class Tenant {

    /** 租户ID */
    private int tenantID;

    /** 租户姓名 */
    private String tenantName;

    /** 手机号 */
    private String phone;

    /** 身份证号 */
    private String idCard;

    /** 工作单位 */
    private String workplace;

    /** 密码 */
    private String password;

    /** 状态（1-正常，0-禁用） */
    private int status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIDCard() {
        return idCard;
    }

    public void setIDCard(String idCard) {
        this.idCard = idCard;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
