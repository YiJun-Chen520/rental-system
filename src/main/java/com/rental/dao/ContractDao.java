package com.rental.dao;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Contract;
import com.rental.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 合同数据访问层
 */
public class ContractDao {

    /**
     * 新增合同
     *
     * @param contract 合同对象
     * @return 生成的合同ID，失败返回0
     */
    public int insert(Contract contract) {
        String sql = "INSERT INTO Contract (HouseID, TenantID, SignDate, StartDate, EndDate, MonthlyRent, Deposit, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, contract.getHouseID());
            ps.setInt(2, contract.getTenantID());
            ps.setDate(3, Date.valueOf(contract.getSignDate()));
            ps.setDate(4, Date.valueOf(contract.getStartDate()));
            ps.setDate(5, Date.valueOf(contract.getEndDate()));
            ps.setBigDecimal(6, contract.getMonthlyRent());
            ps.setBigDecimal(7, contract.getDeposit());
            ps.setString(8, contract.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新合同信息
     *
     * @param contract 合同对象
     * @return 受影响行数
     */
    public int update(Contract contract) {
        String sql = "UPDATE Contract SET HouseID=?, TenantID=?, SignDate=?, StartDate=?, EndDate=?, MonthlyRent=?, Deposit=?, Status=?, UpdateTime=NOW() WHERE ContractID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contract.getHouseID());
            ps.setInt(2, contract.getTenantID());
            ps.setDate(3, Date.valueOf(contract.getSignDate()));
            ps.setDate(4, Date.valueOf(contract.getStartDate()));
            ps.setDate(5, Date.valueOf(contract.getEndDate()));
            ps.setBigDecimal(6, contract.getMonthlyRent());
            ps.setBigDecimal(7, contract.getDeposit());
            ps.setString(8, contract.getStatus());
            ps.setInt(9, contract.getContractID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据ID查询合同（JOIN房源表和租户表获取地址和租户名）
     *
     * @param id 合同ID
     * @return 合同对象，未找到返回null
     */
    public Contract findById(int id) {
        String sql = "SELECT c.*, h.Address, t.TenantName FROM Contract c "
                + "LEFT JOIN House h ON c.HouseID = h.HouseID "
                + "LEFT JOIN Tenant t ON c.TenantID = t.TenantID "
                + "WHERE c.ContractID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据房源ID查询合同
     *
     * @param houseId 房源ID
     * @return 合同对象，未找到返回null
     */
    public Contract findByHouseId(int houseId) {
        String sql = "SELECT * FROM Contract WHERE HouseID = ? ORDER BY ContractID DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, houseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据租户ID分页查询合同列表
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @param status      合同状态（可选）
     * @return 分页结果
     */
    public PageResult<Contract> findByTenantId(int tenantId, PageRequest pageRequest, String status) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE TenantID = ?");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);
        if (status != null && !status.trim().isEmpty()) {
            where.append(" AND Status = ?");
            params.add(status);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Contract" + whereClause;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new PageResult<>(new ArrayList<>(), 0, pageRequest.getPage(), pageRequest.getPageSize());
        }

        // 查询列表
        List<Contract> list = new ArrayList<>();
        String listSql = "SELECT * FROM Contract" + whereClause + " ORDER BY ContractID DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(listSql)) {
            int idx = setParams(ps, params);
            ps.setInt(idx + 1, pageRequest.getOffset());
            ps.setInt(idx + 2, pageRequest.getPageSize());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageResult<>(list, total, pageRequest.getPage(), pageRequest.getPageSize());
    }

    /**
     * 根据房东ID分页查询合同列表（通过房源表关联）
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param status      合同状态（可选）
     * @return 分页结果
     */
    public PageResult<Contract> findByOwnerId(int ownerId, PageRequest pageRequest, String status) {
        // 构建WHERE条件（通过House表关联房东）
        StringBuilder where = new StringBuilder(" WHERE c.HouseID IN (SELECT HouseID FROM House WHERE OwnerID = ?)");
        List<Object> params = new ArrayList<>();
        params.add(ownerId);
        if (status != null && !status.trim().isEmpty()) {
            where.append(" AND c.Status = ?");
            params.add(status);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Contract c" + whereClause;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new PageResult<>(new ArrayList<>(), 0, pageRequest.getPage(), pageRequest.getPageSize());
        }

        // 查询列表
        List<Contract> list = new ArrayList<>();
        String listSql = "SELECT c.* FROM Contract c" + whereClause + " ORDER BY c.ContractID DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(listSql)) {
            int idx = setParams(ps, params);
            ps.setInt(idx + 1, pageRequest.getOffset());
            ps.setInt(idx + 2, pageRequest.getPageSize());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageResult<>(list, total, pageRequest.getPage(), pageRequest.getPageSize());
    }

    /**
     * 分页查询所有合同
     *
     * @param pageRequest 分页参数
     * @param status      合同状态（可选）
     * @return 分页结果
     */
    public PageResult<Contract> findAll(PageRequest pageRequest, String status) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (status != null && !status.trim().isEmpty()) {
            where.append(" AND Status = ?");
            params.add(status);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Contract" + whereClause;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new PageResult<>(new ArrayList<>(), 0, pageRequest.getPage(), pageRequest.getPageSize());
        }

        // 查询列表
        List<Contract> list = new ArrayList<>();
        String listSql = "SELECT * FROM Contract" + whereClause + " ORDER BY ContractID DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(listSql)) {
            int idx = setParams(ps, params);
            ps.setInt(idx + 1, pageRequest.getOffset());
            ps.setInt(idx + 2, pageRequest.getPageSize());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PageResult<>(list, total, pageRequest.getPage(), pageRequest.getPageSize());
    }

    /**
     * 统计合同数量
     *
     * @param status 合同状态（可选）
     * @return 合同数量
     */
    public long count(String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Contract WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND Status = ?");
            params.add(status);
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 统计租户的合同数量
     *
     * @param tenantId 租户ID
     * @param status   合同状态（可选）
     * @return 合同数量
     */
    public long countByTenantId(int tenantId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Contract WHERE TenantID = ?");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND Status = ?");
            params.add(status);
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 统计房东的合同数量（通过房源表关联）
     *
     * @param ownerId 房东ID
     * @param status  合同状态（可选）
     * @return 合同数量
     */
    public long countByOwnerId(int ownerId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Contract WHERE HouseID IN (SELECT HouseID FROM House WHERE OwnerID = ?)");
        List<Object> params = new ArrayList<>();
        params.add(ownerId);
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND Status = ?");
            params.add(status);
        }
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 更新合同状态
     *
     * @param id     合同ID
     * @param status 合同状态（生效/已到期/已终止）
     * @return 受影响行数
     */
    public int updateStatus(int id, String status) {
        String sql = "UPDATE Contract SET Status=?, UpdateTime=NOW() WHERE ContractID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 设置预编译语句参数
     *
     * @param ps     预编译语句
     * @param params 参数列表
     * @return 最后使用的参数索引
     * @throws SQLException SQL异常
     */
    private int setParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) {
                ps.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                ps.setInt(i + 1, (Integer) param);
            } else if (param instanceof BigDecimal) {
                ps.setBigDecimal(i + 1, (BigDecimal) param);
            }
        }
        return params.size();
    }

    /**
     * 将结果集映射为合同对象（支持JOIN查询中的额外字段）
     *
     * @param rs 结果集
     * @return 合同对象
     * @throws SQLException SQL异常
     */
    private Contract mapRow(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setContractID(rs.getInt("ContractID"));
        contract.setHouseID(rs.getInt("HouseID"));
        contract.setTenantID(rs.getInt("TenantID"));
        Date signDate = rs.getDate("SignDate");
        if (signDate != null) {
            contract.setSignDate(signDate.toLocalDate());
        }
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            contract.setStartDate(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("EndDate");
        if (endDate != null) {
            contract.setEndDate(endDate.toLocalDate());
        }
        contract.setMonthlyRent(rs.getBigDecimal("MonthlyRent"));
        contract.setDeposit(rs.getBigDecimal("Deposit"));
        contract.setStatus(rs.getString("Status"));
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            contract.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("UpdateTime");
        if (updateTime != null) {
            contract.setUpdateTime(updateTime.toLocalDateTime());
        }
        // 尝试读取JOIN查询中的额外字段（非所有查询都有）
        try {
            contract.setAddress(rs.getString("Address"));
        } catch (SQLException ignored) {
        }
        try {
            contract.setTenantName(rs.getString("TenantName"));
        } catch (SQLException ignored) {
        }
        return contract;
    }
}
