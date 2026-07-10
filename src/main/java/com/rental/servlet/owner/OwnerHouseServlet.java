package com.rental.servlet.owner;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.House;
import com.rental.service.HouseService;
import com.rental.service.impl.HouseServiceImpl;
import com.rental.util.JsonUtil;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 房东房源管理接口
 * <p>
 * 处理房东对房源的增删改查操作。
 * </p>
 */
@WebServlet("/api/owner/house")
public class OwnerHouseServlet extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleAdd(request, response);
        } catch (Exception e) {
            ResultUtil.writeError(response, "服务器内部错误：" + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleUpdate(request, response);
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
     * 获取房东的房源列表（分页）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String status = ServletUtil.getStringParam(request, "status", null);

        PageResult<House> pageResult = houseService.findByOwnerId(ownerId, pageRequest, status);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 获取房源详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的房源ID");
            return;
        }

        House house = houseService.findById(id);
        if (house == null) {
            ResultUtil.writeError(response, 404, "房源不存在");
            return;
        }

        // 验证房源归属
        if (house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权访问该房源");
            return;
        }

        ResultUtil.writeSuccess(response, house);
    }

    /**
     * 添加房源
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        String json = JsonUtil.readJson(request);
        House house = JsonUtil.fromJson(json, House.class);

        // 设置房东ID
        house.setOwnerID(ownerId);

        if (house.getAddress() == null || house.getAddress().trim().isEmpty()) {
            ResultUtil.writeError(response, 400, "房源地址不能为空");
            return;
        }

        try {
            House addedHouse = houseService.add(house);
            ResultUtil.writeSuccess(response, addedHouse);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }

    /**
     * 更新房源
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        String json = JsonUtil.readJson(request);
        House house = JsonUtil.fromJson(json, House.class);

        if (house.getHouseID() <= 0) {
            ResultUtil.writeError(response, 400, "无效的房源ID");
            return;
        }

        // 验证房源归属
        House existingHouse = houseService.findById(house.getHouseID());
        if (existingHouse == null) {
            ResultUtil.writeError(response, 404, "房源不存在");
            return;
        }
        if (existingHouse.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权修改该房源");
            return;
        }

        // 确保房东ID不被篡改
        house.setOwnerID(ownerId);

        try {
            houseService.update(house);
            ResultUtil.writeSuccess(response);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }

    /**
     * 删除房源（下架）
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的房源ID");
            return;
        }

        // 验证房源归属
        House house = houseService.findById(id);
        if (house == null) {
            ResultUtil.writeError(response, 404, "房源不存在");
            return;
        }
        if (house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权操作该房源");
            return;
        }

        try {
            houseService.updateStatus(id, "已下架");
            ResultUtil.writeSuccess(response);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }
}
