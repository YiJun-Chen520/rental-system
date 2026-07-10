package com.rental.entity;

import java.time.LocalDateTime;

/**
 * 房东实体类
 */
public class Owner {

    /** 房东ID */
    private int ownerID;

    /** 房东姓名 */
    private String ownerName;

    /** 手机号 */
    private String phone;

    /** 身份证号 */
    private String idCard;

    /** 银行账户 */
    private String bankAccount;

    /** 密码 */
    private String password;

    /** 状态（1-正常，0-禁用） */
    private int status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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
