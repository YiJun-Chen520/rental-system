package com.rental.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * <p>
 * 从 classpath 下的 db.properties 加载数据库配置，
 * 通过 DriverManager 获取连接。
 * </p>
 */
public class DBUtil {

    private static final Properties props = new Properties();

    // 静态代码块：加载数据库配置
    static {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("找不到 db.properties 配置文件");
            }
            props.load(is);
            // 加载驱动
            Class.forName(props.getProperty("driver", "com.mysql.cj.jdbc.Driver"));
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库配置失败", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection 数据库连接对象
     * @throws SQLException 连接异常
     */
    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 关闭可关闭的资源
     *
     * @param resources 需要关闭的资源列表
     */
    public static void close(AutoCloseable... resources) {
        if (resources == null) {
            return;
        }
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
