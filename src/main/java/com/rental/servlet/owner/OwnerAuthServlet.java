package com.rental.servlet.owner;

import com.rental.dto.LoginRequest;
import com.rental.entity.Owner;
import com.rental.service.OwnerService;
import com.rental.service.impl.OwnerServiceImpl;
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
 * 房东认证接口
 * <p>
 * 处理房东的注册、登录、获取信息和更新个人信息。
 * </p>
 */
@WebServlet("/api/owner/auth")
public class OwnerAuthServlet extends HttpServlet {

    private OwnerService ownerService = new OwnerServiceImpl();

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
                case "register":
                    handleRegister(request, response);
                    break;
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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = ServletUtil.getStringParam(request, "action", "");
            switch (action) {
                case "update":
                    handleUpdate(request, response);
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
     * 注册
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = JsonUtil.readJson(request);
        Owner owner = JsonUtil.fromJson(json, Owner.class);

        if (owner.getOwnerName() == null || owner.getOwnerName().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "房东姓名不能为空");
            return;
        }
        if (owner.getPhone() == null || owner.getPhone().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "手机号不能为空");
            return;
        }
        if (owner.getPassword() == null || owner.getPassword().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "密码不能为空");
            return;
        }

        try {
            Owner registeredOwner = ownerService.register(owner);
            // 清除密码后返回
            registeredOwner.setPassword(null);
            ResultUtil.writeSuccess(response, registeredOwner);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }

    /**
     * 登录
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = JsonUtil.readJson(request);
        LoginRequest loginRequest = JsonUtil.fromJson(json, LoginRequest.class);

        if (loginRequest.getPhone() == null || loginRequest.getPhone().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "手机号不能为空");
            return;
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "密码不能为空");
            return;
        }

        Owner owner = ownerService.login(loginRequest.getPhone(), loginRequest.getPassword());
        if (owner == null) {
            ResultUtil.writeError(response, 401, "手机号或密码错误");
            return;
        }

        // 生成JWT Token
        String token = JwtUtil.generateToken(owner.getOwnerID(), "owner");

        // 清除密码后返回
        owner.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", owner);
        ResultUtil.writeSuccess(response, data);
    }

    /**
     * 获取当前登录房东信息
     */
    private void handleGetInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = (int) request.getAttribute("userId");
        Owner owner = ownerService.findById(userId);
        if (owner == null) {
            ResultUtil.writeError(response, 404, "房东信息不存在");
            return;
        }
        // 清除密码后返回
        owner.setPassword(null);
        ResultUtil.writeSuccess(response, owner);
    }

    /**
     * 更新个人信息
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = (int) request.getAttribute("userId");
        String json = JsonUtil.readJson(request);
        Owner owner = JsonUtil.fromJson(json, Owner.class);
        owner.setOwnerID(userId);

        try {
            ownerService.update(owner);
            ResultUtil.writeSuccess(response);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }
}
