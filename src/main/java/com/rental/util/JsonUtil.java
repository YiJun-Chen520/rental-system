package com.rental.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSON 工具类
 * <p>
 * 基于 Gson 实现 JSON 序列化 / 反序列化，支持 Java 8 日期类型。
 * </p>
 */
public class JsonUtil {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Gson 实例（线程安全，可复用） */
    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            // LocalDateTime 序列化/反序列化
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src == null ? null : src.format(DATETIME_FMT));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    if (json == null || json.isJsonNull()) return null;
                    String s = json.getAsString();
                    if (s.isEmpty()) return null;
                    return LocalDateTime.parse(s, DATETIME_FMT);
                }
            })
            // LocalDate 序列化/反序列化
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src == null ? null : src.format(DATE_FMT));
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                    if (json == null || json.isJsonNull()) return null;
                    String s = json.getAsString();
                    if (s.isEmpty()) return null;
                    return LocalDate.parse(s, DATE_FMT);
                }
            })
            .create();

    /**
     * 将对象转换为 JSON 字符串
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 将 JSON 字符串转换为指定类型的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 将 JSON 字符串转换为泛型类型的对象
     */
    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * 从 HttpServletRequest 中读取请求体
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
