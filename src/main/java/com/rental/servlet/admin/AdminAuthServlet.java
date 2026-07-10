package com.rental.servlet.admin;

import com.rental.dto.LoginRequest;
import com.rental.entity.Admin;
import com.rental.service.AdminService;
import com.rental.service.impl.AdminServiceImpl;
import com.rental.util.JwtUtil;
import com.rental.util.JsonUtil;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员认证接口
 * <p>
 * 处理管理员登录和获取管理员信息。
 * </p>
 */
@WebServlet("/api/admin/auth")
public class AdminAuthServlet extends HttpServlet {

    private AdminService adminService = new AdminServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = ServletUtil.getStringParam(request, "action", "");
            switch (action) {
                case "info":
                    handleGetInfo(request, response);
                    break;
                default:
                    ResultUtil.writeError(response, 400, "未知操作");
                    break;
            }
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = ServletUtil.getStringParam(request, "action", "");
            switch (action) {
                case "login":
                    handleLogin(request, response);
                    break;
                default:
                    ResultUtil.writeError(response, 400, "未知操作");
                    break;
            }
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 管理员登录
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = JsonUtil.readJson(request);
        LoginRequest loginRequest = JsonUtil.fromJson(json, LoginRequest.class);

        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "用户名不能为空");
            return;
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "密码不能为空");
            return;
        }

        Admin admin = adminService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (admin == null) {
            ResultUtil.writeError(response, 401, "用户名或密码错误");
            return;
        }

        // 生成JWT Token，角色为admin
        String token = JwtUtil.generateToken(admin.getAdminID(), "admin");

        // 清除密码后返回
        admin.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", admin);
        ResultUtil.writeSuccess(response, data);
    }

    /**
     * 获取当前登录管理员信息
     */
    private void handleGetInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = (int) request.getAttribute("userId");
        Admin admin = adminService.findById(userId);
        if (admin == null) {
            ResultUtil.writeError(response, 404, "管理员信息不存在");
            return;
        }
        // 清除密码后返回
        admin.setPassword(null);
        ResultUtil.writeSuccess(response, admin);
    }
}
