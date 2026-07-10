package com.rental;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * 嵌入式 Tomcat 启动入口
 * 运行此类即可启动项目，访问 http://localhost:8080
 */
public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector();

        // 获取项目根目录
        String projectDir = System.getProperty("user.dir");

        // webapp 目录（JSP、静态资源、WEB-INF）
        File webappDir = new File(projectDir, "src/main/webapp");
        Context ctx = tomcat.addWebapp("", webappDir.getAbsolutePath());

        // 添加编译输出到 classpath（使 Servlet、Filter 的注解扫描生效）
        File classesDir = new File(projectDir, "target/classes");
        if (classesDir.exists()) {
            StandardRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(
                    resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("========================================");
        System.out.println("  房屋租赁管理系统启动成功！");
        System.out.println("  访问地址: http://localhost:" + PORT);
        System.out.println("  默认管理员: admin / admin123");
        System.out.println("========================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
