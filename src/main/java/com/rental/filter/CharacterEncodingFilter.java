package com.rental.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * 字符编码过滤器 - 统一UTF-8编码
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法，无需特殊处理
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置请求编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        // 设置响应编码为UTF-8
        response.setCharacterEncoding("UTF-8");
        // 设置响应内容类型为HTML，编码UTF-8
        response.setContentType("text/html;charset=UTF-8");
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 销毁方法，无需特殊处理
    }
}
