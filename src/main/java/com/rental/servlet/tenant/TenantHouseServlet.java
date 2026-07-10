package com.rental.servlet.tenant;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.House;
import com.rental.service.HouseService;
import com.rental.service.impl.HouseServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;

/**
 * 租户房源浏览接口
 * <p>
 * 提供可租房源的列表查询和房源详情查看功能。
 * </p>
 */
@WebServlet("/api/tenant/house")
public class TenantHouseServlet extends HttpServlet {

    private HouseService houseService = new HouseServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            handleDetail(request, response);
        } else {
            handleList(request, response);
        }
    }

    /**
     * 浏览可租房源列表
     * <p>支持分页、关键词搜索、租金范围筛选</p>
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 解析分页参数
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
            pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));

            // 解析筛选参数
            String keyword = ServletUtil.getStringParam(request, "keyword", null);
            BigDecimal minRent = parseBigDecimal(request, "minRent");
            BigDecimal maxRent = parseBigDecimal(request, "maxRent");

            PageResult<House> pageResult = houseService.findAvailable(pageRequest, keyword, minRent, maxRent);
            ResultUtil.writePageResult(response, pageResult);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询房源失败: " + e.getMessage());
        }
    }

    /**
     * 查看房源详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
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
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询房源详情失败: " + e.getMessage());
        }
    }

    /**
     * 解析 BigDecimal 类型参数，参数为空时返回 null
     */
    private BigDecimal parseBigDecimal(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
