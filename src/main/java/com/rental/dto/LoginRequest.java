package com.rental.dto;

/**
 * 登录请求参数
 */
public class LoginRequest {

    /** 用户名（管理员用） */
    private String username;

    /** 手机号（房东/租户用） */
    private String phone;

    /** 密码 */
    private String password;

    public LoginRequest() {
    }

    // ========== Getter / Setter ==========

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
