package com.rental.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * JSON 工具类
 * <p>
 * 基于 Gson 实现 JSON 序列化 / 反序列化。
 * </p>
 */
public class JsonUtil {

    /** Gson 实例（线程安全，可复用） */
    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 目标对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将 JSON 字符串转换为指定类型的对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 目标对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 将 JSON 字符串转换为泛型类型的对象（如 List、Map 等）
     *
     * @param json JSON 字符串
     * @param type 泛型类型（通过 TypeToken 获取）
     * @param <T>  泛型
     * @return 目标对象
     */
    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * 从 HttpServletRequest 中读取请求体并转为 JSON 字符串
     *
     * @param request HTTP 请求对象
     * @return 请求体字符串
     * @throws IOException 读取异常
     */
    public static String readJson(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
