package com.rental.dao;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Payment;
import com.rental.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 费用数据访问层
 */
public class PaymentDao {

    /**
     * 新增费用记录
     *
     * @param payment 费用对象
     * @return 生成的费用ID，失败返回0
     */
    public int insert(Payment payment) {
        String sql = "INSERT INTO Payment (ContractID, PaymentType, Amount, PayDate, PayStatus) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getContractID());
            ps.setString(2, payment.getPaymentType());
            ps.setBigDecimal(3, payment.getAmount());
            if (payment.getPayDate() != null) {
                ps.setDate(4, Date.valueOf(payment.getPayDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setString(5, payment.getPayStatus());
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
     * 更新费用记录
     *
     * @param payment 费用对象
     * @return 受影响行数
     */
    public int update(Payment payment) {
        String sql = "UPDATE Payment SET ContractID=?, PaymentType=?, Amount=?, PayDate=?, PayStatus=? WHERE PaymentID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getContractID());
            ps.setString(2, payment.getPaymentType());
            ps.setBigDecimal(3, payment.getAmount());
            if (payment.getPayDate() != null) {
                ps.setDate(4, Date.valueOf(payment.getPayDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setString(5, payment.getPayStatus());
            ps.setInt(6, payment.getPaymentID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据ID查询费用记录
     *
     * @param id 费用ID
     * @return 费用对象，未找到返回null
     */
    public Payment findById(int id) {
        String sql = "SELECT * FROM Payment WHERE PaymentID = ?";
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
     * 根据合同ID查询费用列表
     *
     * @param contractId 合同ID
     * @return 费用列表
     */
    public List<Payment> findByContractId(int contractId) {
        String sql = "SELECT * FROM Payment WHERE ContractID = ? ORDER BY PaymentID";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据合同ID和缴费状态查询费用列表
     *
     * @param contractId 合同ID
     * @param payStatus  缴费状态（未缴/已缴）
     * @return 费用列表
     */
    public List<Payment> findByContractId(int contractId, String payStatus) {
        String sql = "SELECT * FROM Payment WHERE ContractID = ? AND PayStatus = ? ORDER BY PaymentID";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setString(2, payStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 分页查询所有费用记录
     *
     * @param pageRequest 分页参数
     * @param payStatus   缴费状态（可选）
     * @param paymentType 费用类型（可选）
     * @return 分页结果
     */
    public PageResult<Payment> findAll(PageRequest pageRequest, String payStatus, String paymentType) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (payStatus != null && !payStatus.trim().isEmpty()) {
            where.append(" AND PayStatus = ?");
            params.add(payStatus);
        }
        if (paymentType != null && !paymentType.trim().isEmpty()) {
            where.append(" AND PaymentType = ?");
            params.add(paymentType);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Payment" + whereClause;
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
        List<Payment> list = new ArrayList<>();
        String listSql = "SELECT * FROM Payment" + whereClause + " ORDER BY PaymentID DESC LIMIT ?, ?";
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
     * 根据租户ID分页查询费用列表（通过合同表关联）
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @param payStatus   缴费状态（可选）
     * @return 分页结果
     */
    public PageResult<Payment> findByTenantId(int tenantId, PageRequest pageRequest, String payStatus) {
        // 构建WHERE条件（通过Contract表关联租户）
        StringBuilder where = new StringBuilder(" WHERE p.ContractID IN (SELECT ContractID FROM Contract WHERE TenantID = ?)");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);
        if (payStatus != null && !payStatus.trim().isEmpty()) {
            where.append(" AND p.PayStatus = ?");
            params.add(payStatus);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Payment p" + whereClause;
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
        List<Payment> list = new ArrayList<>();
        String listSql = "SELECT p.* FROM Payment p" + whereClause + " ORDER BY p.PaymentID DESC LIMIT ?, ?";
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
     * 根据房东ID分页查询费用列表（通过合同和房源表关联）
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param payStatus   缴费状态（可选）
     * @return 分页结果
     */
    public PageResult<Payment> findByOwnerId(int ownerId, PageRequest pageRequest, String payStatus) {
        // 构建WHERE条件（通过Contract和House表关联房东）
        StringBuilder where = new StringBuilder(" WHERE p.ContractID IN (SELECT c.ContractID FROM Contract c INNER JOIN House h ON c.HouseID = h.HouseID WHERE h.OwnerID = ?)");
        List<Object> params = new ArrayList<>();
        params.add(ownerId);
        if (payStatus != null && !payStatus.trim().isEmpty()) {
            where.append(" AND p.PayStatus = ?");
            params.add(payStatus);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Payment p" + whereClause;
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
        List<Payment> list = new ArrayList<>();
        String listSql = "SELECT p.* FROM Payment p" + whereClause + " ORDER BY p.PaymentID DESC LIMIT ?, ?";
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
     * 统计费用数量
     *
     * @param payStatus 缴费状态（可选）
     * @return 费用数量
     */
    public long count(String payStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Payment WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (payStatus != null && !payStatus.trim().isEmpty()) {
            sql.append(" AND PayStatus = ?");
            params.add(payStatus);
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
     * 更新缴费状态
     *
     * @param id        费用ID
     * @param payStatus 缴费状态（未缴/已缴）
     * @param payDate   缴费日期
     * @return 受影响行数
     */
    public int updatePayStatus(int id, String payStatus, Date payDate) {
        String sql = "UPDATE Payment SET PayStatus=?, PayDate=? WHERE PaymentID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, payStatus);
            if (payDate != null) {
                ps.setDate(2, payDate);
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setInt(3, id);
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
     * 将结果集映射为费用对象
     *
     * @param rs 结果集
     * @return 费用对象
     * @throws SQLException SQL异常
     */
    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentID(rs.getInt("PaymentID"));
        payment.setContractID(rs.getInt("ContractID"));
        payment.setPaymentType(rs.getString("PaymentType"));
        payment.setAmount(rs.getBigDecimal("Amount"));
        Date payDate = rs.getDate("PayDate");
        if (payDate != null) {
            payment.setPayDate(payDate.toLocalDate());
        }
        payment.setPayStatus(rs.getString("PayStatus"));
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            payment.setCreateTime(createTime.toLocalDateTime());
        }
        return payment;
    }
}
