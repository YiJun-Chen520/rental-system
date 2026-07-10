package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Tenant;
import com.rental.service.TenantService;
import com.rental.service.impl.TenantServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理端 - 租户管理接口
 * <p>
 * 处理租户列表查询、详情查看、状态修改和删除操作。
 * </p>
 */
@WebServlet("/api/admin/tenant")
public class AdminTenantServlet extends HttpServlet {

    private TenantService tenantService = new TenantServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = ServletUtil.getStringParam(request, "action", "");
            switch (action) {
                case "detail":
                    handleDetail(request, response);
                    break;
                default:
                    handleList(request, response);
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
                case "status":
                    handleUpdateStatus(request, response);
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleDelete(request, response);
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    /**
     * 租户列表（分页 + 关键词搜索）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String keyword = ServletUtil.getStringParam(request, "keyword", null);

        PageResult<Tenant> pageResult = tenantService.findAll(pageRequest, keyword);
        // 清除所有租户的密码
        if (pageResult.getList() != null) {
            pageResult.getList().forEach(tenant -> tenant.setPassword(null));
        }
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 租户详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少租户ID");
            return;
        }

        Tenant tenant = tenantService.findById(id);
        if (tenant == null) {
            ResultUtil.writeError(response, 404, "租户不存在");
            return;
        }
        // 清除密码后返回
        tenant.setPassword(null);
        ResultUtil.writeSuccess(response, tenant);
    }

    /**
     * 启用/禁用租户
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        int status = ServletUtil.getIntParam(request, "status", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少租户ID");
            return;
        }
        if (status != 0 && status != 1) {
            ResultUtil.writeError(response, 400, "状态值无效，应为0（禁用）或1（启用）");
            return;
        }

        tenantService.updateStatus(id, status);
        ResultUtil.writeSuccess(response);
    }

    /**
     * 删除租户
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少租户ID");
            return;
        }

        tenantService.delete(id);
        ResultUtil.writeSuccess(response);
    }
}
