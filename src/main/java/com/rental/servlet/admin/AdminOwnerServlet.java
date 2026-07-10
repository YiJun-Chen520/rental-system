package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Owner;
import com.rental.service.OwnerService;
import com.rental.service.impl.OwnerServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理端 - 房东管理接口
 * <p>
 * 处理房东列表查询、详情查看、状态修改和删除操作。
 * </p>
 */
@WebServlet("/api/admin/owner")
public class AdminOwnerServlet extends HttpServlet {

    private OwnerService ownerService = new OwnerServiceImpl();

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
     * 房东列表（分页 + 关键词搜索）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String keyword = ServletUtil.getStringParam(request, "keyword", null);

        PageResult<Owner> pageResult = ownerService.findAll(pageRequest, keyword);
        // 清除所有房东的密码
        if (pageResult.getList() != null) {
            pageResult.getList().forEach(owner -> owner.setPassword(null));
        }
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 房东详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少房东ID");
            return;
        }

        Owner owner = ownerService.findById(id);
        if (owner == null) {
            ResultUtil.writeError(response, 404, "房东不存在");
            return;
        }
        // 清除密码后返回
        owner.setPassword(null);
        ResultUtil.writeSuccess(response, owner);
    }

    /**
     * 启用/禁用房东
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        int status = ServletUtil.getIntParam(request, "status", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少房东ID");
            return;
        }
        if (status != 0 && status != 1) {
            ResultUtil.writeError(response, 400, "状态值无效，应为0（禁用）或1（启用）");
            return;
        }

        ownerService.updateStatus(id, status);
        ResultUtil.writeSuccess(response);
    }

    /**
     * 删除房东
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少房东ID");
            return;
        }

        ownerService.delete(id);
        ResultUtil.writeSuccess(response);
    }
}
