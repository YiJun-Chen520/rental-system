package com.rental.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 跨域过滤器 - 处理CORS跨域请求
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法，无需特殊处理
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // 允许所有来源访问
        resp.setHeader("Access-Control-Allow-Origin", "*");
        // 允许的HTTP方法
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 允许的请求头
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        // 预检请求缓存时间（秒）
        resp.setHeader("Access-Control-Max-Age", "3600");

        // 预检请求直接返回成功
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 销毁方法，无需特殊处理
    }
}
