package com.rental.dao;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Owner;
import com.rental.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 房东数据访问层
 */
public class OwnerDao {

    /**
     * 新增房东
     *
     * @param owner 房东对象
     * @return 生成的房东ID，失败返回0
     */
    public int insert(Owner owner) {
        String sql = "INSERT INTO Owner (OwnerName, Phone, IDCard, BankAccount, Password, Status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, owner.getOwnerName());
            ps.setString(2, owner.getPhone());
            ps.setString(3, owner.getIDCard());
            ps.setString(4, owner.getBankAccount());
            ps.setString(5, owner.getPassword());
            ps.setInt(6, owner.getStatus());
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
     * 更新房东信息
     *
     * @param owner 房东对象
     * @return 受影响行数
     */
    public int update(Owner owner) {
        String sql = "UPDATE Owner SET OwnerName=?, Phone=?, IDCard=?, BankAccount=?, Password=?, Status=?, UpdateTime=NOW() WHERE OwnerID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner.getOwnerName());
            ps.setString(2, owner.getPhone());
            ps.setString(3, owner.getIDCard());
            ps.setString(4, owner.getBankAccount());
            ps.setString(5, owner.getPassword());
            ps.setInt(6, owner.getStatus());
            ps.setInt(7, owner.getOwnerID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据ID查询房东
     *
     * @param id 房东ID
     * @return 房东对象，未找到返回null
     */
    public Owner findById(int id) {
        String sql = "SELECT * FROM Owner WHERE OwnerID = ?";
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
     * 根据手机号查询房东（登录用）
     *
     * @param phone 手机号
     * @return 房东对象，未找到返回null
     */
    public Owner findByPhone(String phone) {
        String sql = "SELECT * FROM Owner WHERE Phone = ?";
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
     * 根据身份证号查询房东
     *
     * @param idCard 身份证号
     * @return 房东对象，未找到返回null
     */
    public Owner findByIDCard(String idCard) {
        String sql = "SELECT * FROM Owner WHERE IDCard = ?";
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
     * 分页查询房东列表（支持按姓名或手机号模糊搜索）
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键字（姓名或手机号）
     * @return 分页结果
     */
    public PageResult<Owner> findAll(PageRequest pageRequest, String keyword) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND (OwnerName LIKE ? OR Phone LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Owner" + whereClause;
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
        List<Owner> list = new ArrayList<>();
        String listSql = "SELECT * FROM Owner" + whereClause + " ORDER BY OwnerID DESC LIMIT ?, ?";
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
     * 统计房东数量（支持按姓名或手机号模糊搜索）
     *
     * @param keyword 搜索关键字
     * @return 房东数量
     */
    public long count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Owner WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (OwnerName LIKE ? OR Phone LIKE ?)");
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
     * 更新房东状态
     *
     * @param id     房东ID
     * @param status 状态值（1-正常，0-禁用）
     * @return 受影响行数
     */
    public int updateStatus(int id, int status) {
        String sql = "UPDATE Owner SET Status=?, UpdateTime=NOW() WHERE OwnerID=?";
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
     * 删除房东
     *
     * @param id 房东ID
     * @return 受影响行数
     */
    public int delete(int id) {
        String sql = "DELETE FROM Owner WHERE OwnerID = ?";
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
     * 将结果集映射为房东对象
     *
     * @param rs 结果集
     * @return 房东对象
     * @throws SQLException SQL异常
     */
    private Owner mapRow(ResultSet rs) throws SQLException {
        Owner owner = new Owner();
        owner.setOwnerID(rs.getInt("OwnerID"));
        owner.setOwnerName(rs.getString("OwnerName"));
        owner.setPhone(rs.getString("Phone"));
        owner.setIDCard(rs.getString("IDCard"));
        owner.setBankAccount(rs.getString("BankAccount"));
        owner.setPassword(rs.getString("Password"));
        owner.setStatus(rs.getInt("Status"));
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            owner.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("UpdateTime");
        if (updateTime != null) {
            owner.setUpdateTime(updateTime.toLocalDateTime());
        }
        return owner;
    }
}
