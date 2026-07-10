package com.rental.servlet.tenant;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Review;
import com.rental.service.ReviewService;
import com.rental.service.impl.ReviewServiceImpl;
import com.rental.util.JsonUtil;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

/**
 * 租户评价接口
 * <p>
 * 提供评价的查询、发表、修改和删除功能，租户只能操作自己的评价。
 * </p>
 */
@WebServlet("/api/tenant/review")
public class TenantReviewServlet extends HttpServlet {

    private ReviewService reviewService = new ReviewServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleAdd(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleUpdate(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleDelete(request, response);
    }

    /**
     * 我的评价列表
     * <p>根据租户ID分页查询评价记录</p>
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");

            // 解析分页参数
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
            pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));

            PageResult<Review> pageResult = reviewService.findByTenantId(tenantId, pageRequest);
            ResultUtil.writePageResult(response, pageResult);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询评价列表失败: " + e.getMessage());
        }
    }

    /**
     * 发表评价
     * <p>从请求体解析 Review 对象，自动设置租户ID为当前登录用户</p>
     */
    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");
            String json = JsonUtil.readJson(request);
            Review review = JsonUtil.fromJson(json, Review.class);
            // 设置租户ID为当前登录用户
            review.setTenantID(tenantId);
            reviewService.add(review);
            ResultUtil.writeSuccess(response, review);
        } catch (Exception e) {
            ResultUtil.writeError(response, "发表评价失败: " + e.getMessage());
        }
    }

    /**
     * 修改评价
     * <p>从请求体解析 Review 对象，验证评价归属后更新</p>
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");
            String json = JsonUtil.readJson(request);
            Review review = JsonUtil.fromJson(json, Review.class);
            // 验证评价是否属于当前租户
            Review existing = reviewService.findById(review.getReviewID());
            if (existing == null) {
                ResultUtil.writeError(response, 404, "评价不存在");
                return;
            }
            if (existing.getTenantID() != tenantId) {
                ResultUtil.writeError(response, 403, "无权修改该评价");
                return;
            }
            // 确保租户ID不被篡改
            review.setTenantID(tenantId);
            reviewService.update(review);
            ResultUtil.writeSuccess(response, "修改成功");
        } catch (Exception e) {
            ResultUtil.writeError(response, "修改评价失败: " + e.getMessage());
        }
    }

    /**
     * 删除评价
     * <p>根据评价ID删除，验证评价归属后执行删除</p>
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");
            int id = ServletUtil.getIntParam(request, "id", -1);
            if (id == -1) {
                ResultUtil.writeError(response, 400, "缺少评价ID");
                return;
            }
            // 验证评价是否属于当前租户
            Review existing = reviewService.findById(id);
            if (existing == null) {
                ResultUtil.writeError(response, 404, "评价不存在");
                return;
            }
            if (existing.getTenantID() != tenantId) {
                ResultUtil.writeError(response, 403, "无权删除该评价");
                return;
            }
            reviewService.delete(id);
            ResultUtil.writeSuccess(response, "删除成功");
        } catch (Exception e) {
            ResultUtil.writeError(response, "删除评价失败: " + e.getMessage());
        }
    }
}
