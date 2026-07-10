package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.House;
import com.rental.service.HouseService;
import com.rental.service.impl.HouseServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理端 - 房源管理接口
 * <p>
 * 处理房源列表查询、详情查看、状态修改和删除操作。
 * </p>
 */
@WebServlet("/api/admin/house")
public class AdminHouseServlet extends HttpServlet {

    private HouseService houseService = new HouseServiceImpl();

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
     * 房源列表（分页 + 关键词搜索 + 状态筛选）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String keyword = ServletUtil.getStringParam(request, "keyword", null);
        String status = ServletUtil.getStringParam(request, "status", null);

        PageResult<House> pageResult = houseService.findAll(pageRequest, keyword, status, null, null);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 房源详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少房源ID");
            return;
        }

        House house = houseService.findById(id);
        if (house == null) {
            ResultUtil.writeError(response, 404, "房源不存在");
            return;
        }
        ResultUtil.writeSuccess(response, house);
    }

    /**
     * 修改房源状态
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        String status = ServletUtil.getStringParam(request, "status", "");
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少房源ID");
            return;
        }
        if (status.isEmpty()) {
            ResultUtil.writeError(response, 400, "缺少状态参数");
            return;
        }

        houseService.updateStatus(id, status);
        ResultUtil.writeSuccess(response);
    }

    /**
     * 删除房源
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少房源ID");
            return;
        }

        houseService.delete(id);
        ResultUtil.writeSuccess(response);
    }
}
