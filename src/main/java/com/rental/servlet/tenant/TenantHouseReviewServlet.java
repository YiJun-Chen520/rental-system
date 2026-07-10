package com.rental.servlet.tenant;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Review;
import com.rental.service.ReviewService;
import com.rental.service.impl.ReviewServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

/**
 * 租户查看房源评价接口
 * <p>
 * 提供查看指定房源的评价列表功能，供租户在浏览房源时参考其他租户的评价。
 * </p>
 */
@WebServlet("/api/tenant/house-review")
public class TenantHouseReviewServlet extends HttpServlet {

    private ReviewService reviewService = new ReviewServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleList(request, response);
    }

    /**
     * 查看某房源的评价列表
     * <p>根据房源ID分页查询评价记录</p>
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int houseId = ServletUtil.getIntParam(request, "houseId", -1);
            if (houseId == -1) {
                ResultUtil.writeError(response, 400, "缺少房源ID");
                return;
            }

            // 解析分页参数
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
            pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));

            PageResult<Review> pageResult = reviewService.findByHouseId(houseId, pageRequest);
            ResultUtil.writePageResult(response, pageResult);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询房源评价失败: " + e.getMessage());
        }
    }
}
