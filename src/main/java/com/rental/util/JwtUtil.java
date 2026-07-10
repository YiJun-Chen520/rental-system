package com.rental.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * <p>
 * 使用 io.jsonwebtoken (jjwt) 库实现 Token 的生成与解析。
 * </p>
 */
public class JwtUtil {

    /** 密钥（长度需 >= 256 位） */
    private static final String SECRET_KEY = "rental-system-secret-key-2024-very-long-and-secure";

    /** 过期时间：24 小时（毫秒） */
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    /** 生成签名密钥 */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userId 用户 ID
     * @param role   用户角色（owner / tenant / admin）
     * @return Token 字符串
     */
    public static String generateToken(int userId, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token Token 字符串
     * @return Claims 载荷信息
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Claims 中获取用户 ID
     *
     * @param claims 载荷信息
     * @return 用户 ID
     */
    public static int getUserId(Claims claims) {
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * 从 Claims 中获取用户角色
     *
     * @param claims 载荷信息
     * @return 角色字符串（owner / tenant / admin）
     */
    public static String getRole(Claims claims) {
        return claims.get("role", String.class);
    }
}
