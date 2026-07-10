package com.rental.dao;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Tenant;
import com.rental.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 租户数据访问层
 */
public class TenantDao {

    /**
     * 新增租户
     *
     * @param tenant 租户对象
     * @return 生成的租户ID，失败返回0
     */
    public int insert(Tenant tenant) {
        String sql = "INSERT INTO Tenant (TenantName, Phone, IDCard, Workplace, Password, Status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tenant.getTenantName());
            ps.setString(2, tenant.getPhone());
            ps.setString(3, tenant.getIDCard());
            ps.setString(4, tenant.getWorkplace());
            ps.setString(5, tenant.getPassword());
            ps.setInt(6, tenant.getStatus());
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
     * 更新租户信息
     *
     * @param tenant 租户对象
     * @return 受影响行数
     */
    public int update(Tenant tenant) {
        String sql = "UPDATE Tenant SET TenantName=?, Phone=?, IDCard=?, Workplace=?, Password=?, Status=?, UpdateTime=NOW() WHERE TenantID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenant.getTenantName());
            ps.setString(2, tenant.getPhone());
            ps.setString(3, tenant.getIDCard());
            ps.setString(4, tenant.getWorkplace());
            ps.setString(5, tenant.getPassword());
            ps.setInt(6, tenant.getStatus());
            ps.setInt(7, tenant.getTenantID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户对象，未找到返回null
     */
    public Tenant findById(int id) {
        String sql = "SELECT * FROM Tenant WHERE TenantID = ?";
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
     * 根据手机号查询租户（登录用）
     *
     * @param phone 手机号
     * @return 租户对象，未找到返回null
     */
    public Tenant findByPhone(String phone) {
        String sql = "SELECT * FROM Tenant WHERE Phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
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
     * 根据身份证号查询租户
     *
     * @param idCard 身份证号
     * @return 租户对象，未找到返回null
     */
    public Tenant findByIDCard(String idCard) {
        String sql = "SELECT * FROM Tenant WHERE IDCard = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCard);
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
     * 分页查询租户列表（支持按姓名或手机号模糊搜索）
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键字（姓名或手机号）
     * @return 分页结果
     */
    public PageResult<Tenant> findAll(PageRequest pageRequest, String keyword) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND (TenantName LIKE ? OR Phone LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Tenant" + whereClause;
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
        List<Tenant> list = new ArrayList<>();
        String listSql = "SELECT * FROM Tenant" + whereClause + " ORDER BY TenantID DESC LIMIT ?, ?";
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
     * 统计租户数量（支持按姓名或手机号模糊搜索）
     *
     * @param keyword 搜索关键字
     * @return 租户数量
     */
    public long count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Tenant WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (TenantName LIKE ? OR Phone LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
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
     * 更新租户状态
     *
     * @param id     租户ID
     * @param status 状态值（1-正常，0-禁用）
     * @return 受影响行数
     */
    public int updateStatus(int id, int status) {
        String sql = "UPDATE Tenant SET Status=?, UpdateTime=NOW() WHERE TenantID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除租户
     *
     * @param id 租户ID
     * @return 受影响行数
     */
    public int delete(int id) {
        String sql = "DELETE FROM Tenant WHERE TenantID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
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
            } else if (param instanceof Long) {
                ps.setLong(i + 1, (Long) param);
            }
        }
        return params.size();
    }

    /**
     * 将结果集映射为租户对象
     *
     * @param rs 结果集
     * @return 租户对象
     * @throws SQLException SQL异常
     */
    private Tenant mapRow(ResultSet rs) throws SQLException {
        Tenant tenant = new Tenant();
        tenant.setTenantID(rs.getInt("TenantID"));
        tenant.setTenantName(rs.getString("TenantName"));
        tenant.setPhone(rs.getString("Phone"));
        tenant.setIDCard(rs.getString("IDCard"));
        tenant.setWorkplace(rs.getString("Workplace"));
        tenant.setPassword(rs.getString("Password"));
        tenant.setStatus(rs.getInt("Status"));
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            tenant.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("UpdateTime");
        if (updateTime != null) {
            tenant.setUpdateTime(updateTime.toLocalDateTime());
        }
        return tenant;
    }
}
