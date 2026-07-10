package com.rental.dao;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.Review;
import com.rental.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价数据访问层
 */
public class ReviewDao {

    /**
     * 新增评价
     *
     * @param review 评价对象
     * @return 生成的评价ID，失败返回0
     */
    public int insert(Review review) {
        String sql = "INSERT INTO Review (TenantID, HouseID, Rating, Content, ReviewDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getTenantID());
            ps.setInt(2, review.getHouseID());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getContent());
            if (review.getReviewDate() != null) {
                ps.setDate(5, Date.valueOf(review.getReviewDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
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
     * 更新评价
     *
     * @param review 评价对象
     * @return 受影响行数
     */
    public int update(Review review) {
        String sql = "UPDATE Review SET TenantID=?, HouseID=?, Rating=?, Content=?, ReviewDate=? WHERE ReviewID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, review.getTenantID());
            ps.setInt(2, review.getHouseID());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getContent());
            if (review.getReviewDate() != null) {
                ps.setDate(5, Date.valueOf(review.getReviewDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setInt(6, review.getReviewID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除评价
     *
     * @param id 评价ID
     * @return 受影响行数
     */
    public int delete(int id) {
        String sql = "DELETE FROM Review WHERE ReviewID = ?";
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
     * 根据ID查询评价
     *
     * @param id 评价ID
     * @return 评价对象，未找到返回null
     */
    public Review findById(int id) {
        String sql = "SELECT * FROM Review WHERE ReviewID = ?";
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
     * 根据房源ID分页查询评价列表（JOIN租户表获取租户名）
     *
     * @param houseId     房源ID
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    public PageResult<Review> findByHouseId(int houseId, PageRequest pageRequest) {
        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Review WHERE HouseID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, houseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new PageResult<>(new ArrayList<>(), 0, pageRequest.getPage(), pageRequest.getPageSize());
        }

        // 查询列表（JOIN租户表获取租户名）
        List<Review> list = new ArrayList<>();
        String listSql = "SELECT r.*, t.TenantName FROM Review r LEFT JOIN Tenant t ON r.TenantID = t.TenantID WHERE r.HouseID = ? ORDER BY r.ReviewID DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(listSql)) {
            ps.setInt(1, houseId);
            ps.setInt(2, pageRequest.getOffset());
            ps.setInt(3, pageRequest.getPageSize());
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
     * 根据租户ID分页查询评价列表
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    public PageResult<Review> findByTenantId(int tenantId, PageRequest pageRequest) {
        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Review WHERE TenantID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, tenantId);
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
        List<Review> list = new ArrayList<>();
        String listSql = "SELECT * FROM Review WHERE TenantID = ? ORDER BY ReviewID DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(listSql)) {
            ps.setInt(1, tenantId);
            ps.setInt(2, pageRequest.getOffset());
            ps.setInt(3, pageRequest.getPageSize());
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
     * 统计房源的评价数量
     *
     * @param houseId 房源ID
     * @return 评价数量
     */
    public long countByHouseId(int houseId) {
        String sql = "SELECT COUNT(*) FROM Review WHERE HouseID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, houseId);
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
     * 分页查询所有评价
     *
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    public PageResult<Review> findAll(PageRequest pageRequest) {
        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM Review";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
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
        List<Review> list = new ArrayList<>();
        String listSql = "SELECT * FROM Review ORDER BY ReviewID DESC LIMIT ?, ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(listSql)) {
            ps.setInt(1, pageRequest.getOffset());
            ps.setInt(2, pageRequest.getPageSize());
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
     * 将结果集映射为评价对象（支持JOIN查询中的额外字段）
     *
     * @param rs 结果集
     * @return 评价对象
     * @throws SQLException SQL异常
     */
    private Review mapRow(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewID(rs.getInt("ReviewID"));
        review.setTenantID(rs.getInt("TenantID"));
        review.setHouseID(rs.getInt("HouseID"));
        review.setRating(rs.getInt("Rating"));
        review.setContent(rs.getString("Content"));
        Date reviewDate = rs.getDate("ReviewDate");
        if (reviewDate != null) {
            review.setReviewDate(reviewDate.toLocalDate());
        }
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            review.setCreateTime(createTime.toLocalDateTime());
        }
        // 尝试读取JOIN查询中的租户姓名（非所有查询都有此字段）
        try {
            review.setTenantName(rs.getString("TenantName"));
        } catch (SQLException ignored) {
            // 非JOIN查询时忽略
        }
        return review;
    }
}
