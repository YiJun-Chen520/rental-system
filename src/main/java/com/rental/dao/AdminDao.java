package com.rental.dao;

import com.rental.entity.Admin;
import com.rental.util.DBUtil;

import java.sql.*;

/**
 * 管理员数据访问层
 */
public class AdminDao {

    /**
     * 根据用户名查询管理员（登录用）
     *
     * @param username 用户名
     * @return 管理员对象，未找到返回null
     */
    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM Admin WHERE Username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
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
     * 根据ID查询管理员
     *
     * @param id 管理员ID
     * @return 管理员对象，未找到返回null
     */
    public Admin findById(int id) {
        String sql = "SELECT * FROM Admin WHERE AdminID = ?";
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
     * 将结果集映射为管理员对象
     *
     * @param rs 结果集
     * @return 管理员对象
     * @throws SQLException SQL异常
     */
    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminID(rs.getInt("AdminID"));
        admin.setUsername(rs.getString("Username"));
        admin.setPassword(rs.getString("Password"));
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            admin.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("UpdateTime");
        if (updateTime != null) {
            admin.setUpdateTime(updateTime.toLocalDateTime());
        }
        return admin;
    }
}
