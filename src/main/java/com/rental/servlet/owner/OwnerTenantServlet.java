package com.rental.servlet.owner;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.entity.Tenant;
import com.rental.service.ContractService;
import com.rental.service.TenantService;
import com.rental.service.impl.ContractServiceImpl;
import com.rental.service.impl.TenantServiceImpl;
import com.rental.util.ResultUtil;
import com.rental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 房东租户管理接口
 * <p>
 * 房东查看与自己有合同关系的租户列表。
 * </p>
 */
@WebServlet("/api/owner/tenant")
public class OwnerTenantServlet extends HttpServlet {

    private ContractService contractService = new ContractServiceImpl();
    private TenantService tenantService = new TenantServiceImpl();

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

    /**
     * 获取与房东有合同关系的租户列表（分页）
     * <p>
     * 通过房东的合同获取关联的租户ID，再去重后返回租户列表。
     * </p>
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int page = ServletUtil.getIntParam(request, "page", 1);
        int pageSize = ServletUtil.getIntParam(request, "pageSize", 10);

        // 获取房东所有合同（使用较大的分页以获取全部租户ID）
        PageRequest queryRequest = new PageRequest();
        queryRequest.setPage(1);
        queryRequest.setPageSize(Integer.MAX_VALUE);
        PageResult<Contract> contractPage = contractService.findByOwnerId(ownerId, queryRequest, null);

        // 提取不重复的租户ID（保持顺序）
        Map<Integer, Boolean> tenantIdMap = new LinkedHashMap<>();
        for (Contract contract : contractPage.getList()) {
            tenantIdMap.putIfAbsent(contract.getTenantID(), true);
        }

        // 根据租户ID列表查询租户信息
        List<Tenant> allTenants = new ArrayList<>();
        for (int tenantId : tenantIdMap.keySet()) {
            Tenant tenant = tenantService.findById(tenantId);
            if (tenant != null) {
                // 清除密码后返回
                tenant.setPassword(null);
                allTenants.add(tenant);
            }
        }

        // 手动分页
        int total = allTenants.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Tenant> pageList = (fromIndex < total) ? allTenants.subList(fromIndex, toIndex) : new ArrayList<>();

        PageResult<Tenant> pageResult = new PageResult<>(pageList, total, page, pageSize);
        ResultUtil.writePageResult(response, pageResult);
    }

    /**
     * 获取租户详情
     * <p>
     * 验证该租户与当前房东存在合同关系后返回租户信息。
     * </p>
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int ownerId = (int) request.getAttribute("userId");
        int id = ServletUtil.getIntParam(request, "id", -1);

        if (id <= 0) {
            ResultUtil.writeError(response, 400, "无效的租户ID");
            return;
        }

        // 查询租户信息
        Tenant tenant = tenantService.findById(id);
        if (tenant == null) {
            ResultUtil.writeError(response, 404, "租户不存在");
            return;
        }

        // 验证租户是否与当前房东存在合同关系
        PageRequest queryRequest = new PageRequest();
        queryRequest.setPage(1);
        queryRequest.setPageSize(Integer.MAX_VALUE);
        PageResult<Contract> contractPage = contractService.findByOwnerId(ownerId, queryRequest, null);

        boolean hasRelation = false;
        for (Contract contract : contractPage.getList()) {
            if (contract.getTenantID() == id) {
                hasRelation = true;
                break;
            }
        }

        if (!hasRelation) {
            ResultUtil.writeError(response, 403, "无权查看该租户信息");
            return;
        }

        // 清除密码后返回
        tenant.setPassword(null);
        ResultUtil.writeSuccess(response, tenant);
    }
}
