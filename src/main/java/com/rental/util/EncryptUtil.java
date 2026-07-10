package com.rental.util;

/**
 * 加密工具类（当前使用明文存储，不做加密）
 */
public class EncryptUtil {

    /**
     * 返回原密码（不做加密）
     *
     * @param password 明文密码
     * @return 原密码
     */
    public static String encrypt(String password) {
        return password;
    }

    /**
     * 验证明文密码是否匹配
     *
     * @param password 输入的密码
     * @param stored   数据库中存储的密码
     * @return true-匹配成功，false-匹配失败
     */
    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        return password.equals(stored);
    }
}
