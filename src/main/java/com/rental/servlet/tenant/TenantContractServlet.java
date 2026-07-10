package com.rental.servlet.tenant;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.service.ContractService;
import com.rental.service.impl.ContractServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

/**
 * 租户合同接口
 * <p>
 * 提供合同列表查询和合同详情查看功能，租户只能查看自己的合同。
 * </p>
 */
@WebServlet("/api/tenant/contract")
public class TenantContractServlet extends HttpServlet {

    private ContractService contractService = new ContractServiceImpl();

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
     * 我的合同列表
     * <p>根据租户ID分页查询合同，支持按合同状态筛选</p>
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");

            // 解析分页参数
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
            pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));

            // 解析状态筛选参数
            String status = ServletUtil.getStringParam(request, "status", null);

            PageResult<Contract> pageResult = contractService.findByTenantId(tenantId, pageRequest, status);
            ResultUtil.writePageResult(response, pageResult);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询合同列表失败: " + e.getMessage());
        }
    }

    /**
     * 合同详情
     * <p>查询合同详情，并验证是否属于当前登录租户，防止越权访问</p>
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int tenantId = (int) request.getAttribute("userId");
            int id = ServletUtil.getIntParam(request, "id", -1);
            if (id == -1) {
                ResultUtil.writeError(response, 400, "缺少合同ID");
                return;
            }
            Contract contract = contractService.findById(id);
            if (contract == null) {
                ResultUtil.writeError(response, 404, "合同不存在");
                return;
            }
            // 验证合同是否属于当前租户
            if (contract.getTenantID() != tenantId) {
                ResultUtil.writeError(response, 403, "无权查看该合同");
                return;
            }
            ResultUtil.writeSuccess(response, contract);
        } catch (Exception e) {
            ResultUtil.writeError(response, "查询合同详情失败: " + e.getMessage());
        }
    }
}
