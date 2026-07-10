package com.rental.servlet.tenant;

import com.rental.dto.LoginRequest;
import com.rental.dto.PageRequest;
import com.rental.entity.Tenant;
import com.rental.service.TenantService;
import com.rental.service.impl.TenantServiceImpl;
import com.rental.util.JwtUtil;
import com.rental.util.JsonUtil;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 租户认证接口
 * <p>
 * 处理租户的注册、登录、获取个人信息和更新个人信息。
 * </p>
 */
@WebServlet("/api/tenant/auth")
public class TenantAuthServlet extends HttpServlet {

    private TenantService tenantService = new TenantServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            ResultUtil.writeError(response, 400, "缺少 action 参数");
            return;
        }
        switch (action) {
            case "register":
                handleRegister(request, response);
                break;
            case "login":
                handleLogin(request, response);
                break;
            default:
                ResultUtil.writeError(response, 400, "不支持的操作: " + action);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("info".equals(action)) {
            handleInfo(request, response);
        } else {
            ResultUtil.writeError(response, 400, "不支持的操作");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("update".equals(action)) {
            handleUpdate(request, response);
        } else {
            ResultUtil.writeError(response, 400, "不支持的操作");
        }
    }

    /**
     * 租户注册
     * <p>解析 Tenant 对象，调用 tenantService.register，返回不含密码的租户信息</p>
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = JsonUtil.readJson(request);
            Tenant tenant = JsonUtil.fromJson(json, Tenant.class);
            Tenant registered = tenantService.register(tenant);
            // 注册成功，清除密码后返回
            registered.setPassword(null);
            ResultUtil.writeSuccess(response, registered);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, e.getMessage());
        } catch (Exception e) {
            ResultUtil.writeError(response, "注册失败: " + e.getMessage());
        }
    }

    /**
     * 租户登录
     * <p>解析 LoginRequest（phone, password），验证成功返回 JWT Token 和租户信息</p>
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = JsonUtil.readJson(request);
            LoginRequest loginRequest = JsonUtil.fromJson(json, LoginRequest.class);
            Tenant tenant = tenantService.login(loginRequest.getPhone(), loginRequest.getPassword());
            if (tenant == null) {
                ResultUtil.writeError(response, 401, "手机号或密码错误");
                return;
            }
            // 生成 JWT Token
            String token = JwtUtil.generateToken(tenant.getTenantID(), "tenant");
            // 清除密码后返回
            tenant.setPassword(null);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", tenant);
            ResultUtil.writeSuccess(response, data);
        } catch (Exception e) {
            ResultUtil.writeError(response, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取个人信息
     * <p>从 Token 中获取 userId，查询租户信息并返回（不含密码）</p>
     */
    private void handleInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int userId = (int) request.getAttribute("userId");
            Tenant tenant = tenantService.findById(userId);
            if (tenant == null) {
                ResultUtil.writeError(response, 404, "租户不存在");
                return;
            }
            // 清除密码后返回
            tenant.setPassword(null);
            ResultUtil.writeSuccess(response, tenant);
        } catch (Exception e) {
            ResultUtil.writeError(response, "获取信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新个人信息
     * <p>从 Token 中获取 userId，解析 Tenant 对象并更新（不允许修改他人信息）</p>
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int userId = (int) request.getAttribute("userId");
            String json = JsonUtil.readJson(request);
            Tenant tenant = JsonUtil.fromJson(json, Tenant.class);
            // 设置租户ID为当前登录用户，防止越权修改
            tenant.setTenantID(userId);
            tenantService.update(tenant);
            ResultUtil.writeSuccess(response, "更新成功");
        } catch (Exception e) {
            ResultUtil.writeError(response, "更新失败: " + e.getMessage());
        }
    }
}
