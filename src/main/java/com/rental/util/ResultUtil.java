package com.rental.util;

import com.rental.dto.PageResult;
import com.rental.dto.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应工具类
 * <p>
 * 向 HttpServletResponse 写入 JSON 格式的统一响应结果。
 * </p>
 */
public class ResultUtil {

    /**
     * 设置响应头为 JSON 格式
     */
    private static void setJsonHeader(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    /**
     * 写入成功响应（无数据）
     */
    public static void writeSuccess(HttpServletResponse response) throws IOException {
        setJsonHeader(response);
        response.getWriter().write(JsonUtil.toJson(Result.success()));
    }

    /**
     * 写入成功响应（带数据）
     */
    public static void writeSuccess(HttpServletResponse response, Object data) throws IOException {
        setJsonHeader(response);
        response.getWriter().write(JsonUtil.toJson(Result.success(data)));
    }

    /**
     * 写入失败响应（默认状态码 500）
     */
    public static void writeError(HttpServletResponse response, String message) throws IOException {
        setJsonHeader(response);
        response.getWriter().write(JsonUtil.toJson(Result.error(message)));
    }

    /**
     * 写入失败响应（自定义状态码）
     */
    public static void writeError(HttpServletResponse response, int code, String message) throws IOException {
        setJsonHeader(response);
        response.getWriter().write(JsonUtil.toJson(Result.error(code, message)));
    }

    /**
     * 写入分页查询结果
     */
    public static void writePageResult(HttpServletResponse response, PageResult<?> pageResult) throws IOException {
        setJsonHeader(response);
        response.getWriter().write(JsonUtil.toJson(Result.success(pageResult)));
    }
}
