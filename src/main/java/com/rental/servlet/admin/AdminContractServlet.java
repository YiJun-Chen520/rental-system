package com.rental.servlet.admin;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.service.ContractService;
import com.rental.service.impl.ContractServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 管理端 - 合同管理接口
 * <p>
 * 处理合同列表查询、详情查看、终止合同和到期处理操作。
 * </p>
 */
@WebServlet("/api/admin/contract")
public class AdminContractServlet extends HttpServlet {

    private ContractService contractService = new ContractServiceImpl();

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
                case "terminate":
                    handleTerminate(request, response);
                    break;
                case "expire":
                    handleExpire(request, response);
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
     * 合同列表（分页 + 状态筛选）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String status = ServletUtil.getStringParam(request, "status", null);

        PageResult<Contract> pageResult = contractService.findAll(pageRequest, status);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 合同详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        ResultUtil.writeSuccess(response, contract);
    }

    /**
     * 终止合同
     */
    private void handleTerminate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少合同ID");
            return;
        }

        contractService.terminate(id);
        ResultUtil.writeSuccess(response);
    }

    /**
     * 合同到期处理
     */
    private void handleExpire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = ServletUtil.getIntParam(request, "id", -1);
        if (id == -1) {
            ResultUtil.writeError(response, 400, "缺少合同ID");
            return;
        }

        contractService.expire(id);
        ResultUtil.writeSuccess(response);
    }
}
