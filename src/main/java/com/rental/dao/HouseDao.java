package com.rental.dao;

import com.rental.dto.PageRequest;
import com.rental.dto.PageResult;
import com.rental.entity.House;
import com.rental.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 房源数据访问层
 */
public class HouseDao {

    /**
     * 新增房源
     *
     * @param house 房源对象
     * @return 生成的房源ID，失败返回0
     */
    public int insert(House house) {
        String sql = "INSERT INTO House (OwnerID, Address, HouseType, Area, MonthlyRent, Status, Facilities, Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, house.getOwnerID());
            ps.setString(2, house.getAddress());
            ps.setString(3, house.getHouseType());
            ps.setBigDecimal(4, house.getArea());
            ps.setBigDecimal(5, house.getMonthlyRent());
            ps.setString(6, house.getStatus());
            ps.setString(7, house.getFacilities());
            ps.setString(8, house.getDescription());
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
     * 更新房源信息
     *
     * @param house 房源对象
     * @return 受影响行数
     */
    public int update(House house) {
        String sql = "UPDATE House SET Address=?, HouseType=?, Area=?, MonthlyRent=?, Status=?, Facilities=?, Description=?, UpdateTime=NOW() WHERE HouseID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, house.getAddress());
            ps.setString(2, house.getHouseType());
            ps.setBigDecimal(3, house.getArea());
            ps.setBigDecimal(4, house.getMonthlyRent());
            ps.setString(5, house.getStatus());
            ps.setString(6, house.getFacilities());
            ps.setString(7, house.getDescription());
            ps.setInt(8, house.getHouseID());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据ID查询房源（JOIN房东表获取房东姓名）
     *
     * @param id 房源ID
     * @return 房源对象，未找到返回null
     */
    public House findById(int id) {
        String sql = "SELECT h.*, o.OwnerName FROM House h LEFT JOIN Owner o ON h.OwnerID = o.OwnerID WHERE h.HouseID = ?";
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
     * 根据房东ID分页查询房源列表
     *
     * @param ownerId     房东ID
     * @param pageRequest 分页参数
     * @param status      房源状态（可选）
     * @return 分页结果
     */
    public PageResult<House> findByOwnerId(int ownerId, PageRequest pageRequest, String status) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE OwnerID = ?");
        List<Object> params = new ArrayList<>();
        params.add(ownerId);
        if (status != null && !status.trim().isEmpty()) {
            where.append(" AND Status = ?");
            params.add(status);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM House" + whereClause;
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
        List<House> list = new ArrayList<>();
        String listSql = "SELECT * FROM House" + whereClause + " ORDER BY HouseID DESC LIMIT ?, ?";
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
     * 分页查询所有房源（支持多条件筛选）
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键字（地址）
     * @param status      房源状态（可选）
     * @param minRent     最低租金（可选）
     * @param maxRent     最高租金（可选）
     * @return 分页结果
     */
    public PageResult<House> findAll(PageRequest pageRequest, String keyword, String status, BigDecimal minRent, BigDecimal maxRent) {
        // 构建WHERE条件
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND Address LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            where.append(" AND Status = ?");
            params.add(status);
        }
        if (minRent != null) {
            where.append(" AND MonthlyRent >= ?");
            params.add(minRent);
        }
        if (maxRent != null) {
            where.append(" AND MonthlyRent <= ?");
            params.add(maxRent);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM House" + whereClause;
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
        List<House> list = new ArrayList<>();
        String listSql = "SELECT * FROM House" + whereClause + " ORDER BY HouseID DESC LIMIT ?, ?";
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
     * 统计房东的房源数量
     *
     * @param ownerId 房东ID
     * @param status  房源状态（可选）
     * @return 房源数量
     */
    public long countByOwnerId(int ownerId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM House WHERE OwnerID = ?");
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
     * 统计房源数量（支持多条件筛选）
     *
     * @param keyword 搜索关键字（地址）
     * @param status  房源状态（可选）
     * @return 房源数量
     */
    public long count(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM House WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND Address LIKE ?");
            params.add("%" + keyword + "%");
        }
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
     * 更新房源状态
     *
     * @param id     房源ID
     * @param status 房源状态（空闲/已租/已下架）
     * @return 受影响行数
     */
    public int updateStatus(int id, String status) {
        String sql = "UPDATE House SET Status=?, UpdateTime=NOW() WHERE HouseID=?";
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
     * 删除房源
     *
     * @param id 房源ID
     * @return 受影响行数
     */
    public int delete(int id) {
        String sql = "DELETE FROM House WHERE HouseID = ?";
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
     * 分页查询空闲房源（支持多条件筛选）
     *
     * @param pageRequest 分页参数
     * @param keyword     搜索关键字（地址）
     * @param minRent     最低租金（可选）
     * @param maxRent     最高租金（可选）
     * @return 分页结果
     */
    public PageResult<House> findAvailable(PageRequest pageRequest, String keyword, BigDecimal minRent, BigDecimal maxRent) {
        // 构建WHERE条件（固定Status='空闲'）
        StringBuilder where = new StringBuilder(" WHERE Status = '空闲'");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND Address LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (minRent != null) {
            where.append(" AND MonthlyRent >= ?");
            params.add(minRent);
        }
        if (maxRent != null) {
            where.append(" AND MonthlyRent <= ?");
            params.add(maxRent);
        }
        String whereClause = where.toString();

        // 查询总数
        long total = 0;
        String countSql = "SELECT COUNT(*) FROM House" + whereClause;
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
        List<House> list = new ArrayList<>();
        String listSql = "SELECT * FROM House" + whereClause + " ORDER BY HouseID DESC LIMIT ?, ?";
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
     * 将结果集映射为房源对象（支持JOIN查询中的额外字段）
     *
     * @param rs 结果集
     * @return 房源对象
     * @throws SQLException SQL异常
     */
    private House mapRow(ResultSet rs) throws SQLException {
        House house = new House();
        house.setHouseID(rs.getInt("HouseID"));
        house.setOwnerID(rs.getInt("OwnerID"));
        house.setAddress(rs.getString("Address"));
        house.setHouseType(rs.getString("HouseType"));
        house.setArea(rs.getBigDecimal("Area"));
        house.setMonthlyRent(rs.getBigDecimal("MonthlyRent"));
        house.setStatus(rs.getString("Status"));
        house.setFacilities(rs.getString("Facilities"));
        house.setDescription(rs.getString("Description"));
        Timestamp createTime = rs.getTimestamp("CreateTime");
        if (createTime != null) {
            house.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("UpdateTime");
        if (updateTime != null) {
            house.setUpdateTime(updateTime.toLocalDateTime());
        }
        // 尝试读取JOIN查询中的房东姓名（非所有查询都有此字段）
        try {
            house.setOwnerName(rs.getString("OwnerName"));
        } catch (SQLException ignored) {
            // 非JOIN查询时忽略
        }
        return house;
    }
}
