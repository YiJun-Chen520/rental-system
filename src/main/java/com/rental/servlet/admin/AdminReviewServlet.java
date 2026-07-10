package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Review;
import com.rental.service.ReviewService;
import com.rental.service.impl.ReviewServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理端 - 评价管理接口
 * <p>
 * 处理评价列表查询和删除操作。
 * </p>
 */
@WebServlet("/api/admin/review")
public class AdminReviewServlet extends HttpServlet {

    private ReviewService reviewService = new ReviewServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            handleList(request, response);
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
     * 评价列表（分页）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));

        PageResult<Review> pageResult = reviewService.findAll(pageRequest);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 删除评价
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少评价ID");
            return;
        }

        reviewService.delete(id);
        ResultUtil.writeSuccess(response);
    }
}
