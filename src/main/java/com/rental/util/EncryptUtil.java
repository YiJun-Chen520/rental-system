package com.rental.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 加密工具类
 * <p>
 * 基于 BCrypt 实现密码的加密与验证。
 * </p>
 */
public class EncryptUtil {

    /**
     * 对密码进行 BCrypt 加密
     *
     * @param password 明文密码
     * @return BCrypt 哈希值
     */
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 验证明文密码是否匹配 BCrypt 哈希值
     *
     * @param password 明文密码
     * @param hash     BCrypt 哈希值
     * @return true-匹配成功，false-匹配失败
     */
    public static boolean verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
