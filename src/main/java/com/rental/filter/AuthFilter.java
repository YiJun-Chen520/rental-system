package com.rental.filter;

import com.rental.util.JwtUtil;
import com.rental.util.ResultUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 认证过滤器 - JWT Token验证
 */
@WebFilter("/api/*")
public class AuthFilter implements Filter {

    // 无需认证的公开路径：登录、注册
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法，无需特殊处理
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 获取请求路径（去除上下文路径）
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        String relativePath = path.substring(contextPath.length());

        // 检查是否为公开路径（登录/注册），公开路径直接放行
        if (isPublicPath(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        // 从Authorization头提取Token
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResultUtil.writeError(resp, 401, "未登录或Token已过期");
            return;
        }

        // 解析Token
        String token = authHeader.substring(7);
        try {
            Claims claims = JwtUtil.parseToken(token);
            int userId = JwtUtil.getUserId(claims);
            String role = JwtUtil.getRole(claims);

            // 验证角色是否与URL路径匹配
            if (!verifyRole(relativePath, role)) {
                ResultUtil.writeError(resp, 403, "无权访问");
                return;
            }

            // 将用户信息设置到请求属性中，供Servlet使用
            req.setAttribute("userId", userId);
            req.setAttribute("role", role);
            chain.doFilter(request, response);
        } catch (Exception e) {
            // Token无效或已过期
            ResultUtil.writeError(resp, 401, "Token无效或已过期");
        }
    }

    @Override
    public void destroy() {
        // 销毁方法，无需特殊处理
    }

    /**
     * 判断是否为公开路径（登录/注册接口）
     *
     * @param path 请求相对路径
     * @return 是否为公开路径
     */
    private boolean isPublicPath(String path) {
        // 匹配认证接口：/api/owner/auth、/api/tenant/auth、/api/admin/auth
        return path.matches(".*/auth$");
    }

    /**
     * 验证用户角色是否与请求路径匹配
     *
     * @param path  请求相对路径
     * @param role  用户角色
     * @return 角色是否匹配
     */
    private boolean verifyRole(String path, String role) {
        // 房东接口仅允许owner角色访问
        if (path.startsWith("/api/owner/") && !"owner".equals(role)) return false;
        // 租户接口仅允许tenant角色访问
        if (path.startsWith("/api/tenant/") && !"tenant".equals(role)) return false;
        // 管理员接口仅允许admin角色访问
        if (path.startsWith("/api/admin/") && !"admin".equals(role)) return false;
        return true;
    }
}
