package com.rental.util;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Servlet 请求工具类
 * <p>
 * 提供从 HttpServletRequest 中获取参数的便捷方法。
 * </p>
 */
public class ServletUtil {

    /**
     * 获取 int 类型的请求参数
     *
     * @param request      HTTP 请求
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值（转换失败时返回默认值）
     */
    public static int getIntParam(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取 long 类型的请求参数
     *
     * @param request      HTTP 请求
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值（转换失败时返回默认值）
     */
    public static long getLongParam(HttpServletRequest request, String name, long defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取 String 类型的请求参数
     *
     * @param request      HTTP 请求
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 参数值（为空时返回默认值）
     */
    public static String getStringParam(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    /**
     * 从 URL 路径中提取资源 ID
     * <p>
     * 示例：/api/owner/house/123 -> 123
     * </p>
     *
     * @param request HTTP 请求
     * @return 资源 ID（提取失败时返回 -1）
     */
    public static int getPathId(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 去除末尾的斜杠
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        // 获取最后一段路径
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1 || lastSlash == path.length() - 1) {
            return -1;
        }
        String idStr = path.substring(lastSlash + 1);
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 读取请求体内容（代理调用 JsonUtil.readJson）
     *
     * @param request HTTP 请求
     * @return 请求体字符串
     * @throws IOException 读取异常
     */
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        return JsonUtil.readJson(request);
    }
}
