package com.rental.servlet.owner;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.entity.House;
import com.rental.service.ContractService;
import com.rental.service.HouseService;
import com.rental.service.impl.ContractServiceImpl;
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
 * 房东合同管理接口
 * <p>
 * 处理房东对合同的查询、创建、终止和到期处理。
 * </p>
 */
@WebServlet("/api/owner/contract")
public class OwnerContractServlet extends HttpServlet {

    private ContractService contractService = new ContractServiceImpl();
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
            handleCreate(request, response);
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
     * 获取房东的合同列表（分页）
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(ServletUtil.getIntParam(request, "page", 1));
        pageRequest.setPageSize(ServletUtil.getIntParam(request, "pageSize", 10));
        String status = ServletUtil.getStringParam(request, "status", null);

        PageResult<Contract> pageResult = contractService.findByOwnerId(ownerId, pageRequest, status);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 获取合同详情
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的合同ID");
            return;
        }

        Contract contract = contractService.findById(id);
        if (contract == null) {
            ResultUtil.writeError(response, 404, "合同不存在");
            return;
        }

        // 验证合同关联的房源是否属于当前房东
        House house = houseService.findById(contract.getHouseID());
        if (house == null || house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权访问该合同");
            return;
        }

        ResultUtil.writeSuccess(response, contract);
    }

    /**
     * 创建合同
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        String json = JsonUtil.readJson(request);
        Contract contract = JsonUtil.fromJson(json, Contract.class);

        // 验证房源归属
        if (contract.getHouseID() <= 0) {
            ResultUtil.writeError(response, 400, "无效的房源ID");
            return;
        }
        House house = houseService.findById(contract.getHouseID());
        if (house == null || house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权为该房源创建合同");
            return;
        }

        if (contract.getTenantID() <= 0) {
            ResultUtil.writeError(response, 400, "无效的租户ID");
            return;
        }
        if (contract.getStartDate() == null || contract.getEndDate() == null) {
            ResultUtil.writeError(response, 400, "合同起止日期不能为空");
            return;
        }

        try {
            Contract createdContract = contractService.create(contract);
            ResultUtil.writeSuccess(response, createdContract);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }

    /**
     * 终止合同
     */
    private void handleTerminate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的合同ID");
            return;
        }

        // 验证合同归属
        Contract contract = contractService.findById(id);
        if (contract == null) {
            ResultUtil.writeError(response, 404, "合同不存在");
            return;
        }
        House house = houseService.findById(contract.getHouseID());
        if (house == null || house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权终止该合同");
            return;
        }

        try {
            contractService.terminate(id);
            ResultUtil.writeSuccess(response);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }

    /**
     * 到期处理
     */
    private void handleExpire(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的合同ID");
            return;
        }

        // 验证合同归属
        Contract contract = contractService.findById(id);
        if (contract == null) {
            ResultUtil.writeError(response, 404, "合同不存在");
            return;
        }
        House house = houseService.findById(contract.getHouseID());
        if (house == null || house.getOwnerID() != ownerId) {
            ResultUtil.writeError(response, 403, "无权操作该合同");
            return;
        }

        try {
            contractService.expire(id);
            ResultUtil.writeSuccess(response);
        } catch (RuntimeException e) {
            ResultUtil.writeError(response, 400, e.getMessage());
        }
    }
}
