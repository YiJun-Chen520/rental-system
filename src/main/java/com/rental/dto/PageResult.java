package com.rental.dto;

import java.util.List;

/**
 * 分页查询结果
 *
 * @param <T> 数据类型
 */
public class PageResult<T> {

    /** 数据列表 */
    private List<T> list;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private int page;

    /** 每页大小 */
    private int pageSize;

    /** 总页数 */
    private int totalPages;

    public PageResult() {
    }

    /**
     * 构造分页结果（自动计算总页数）
     *
     * @param list     数据列表
     * @param total    总记录数
     * @param page     当前页码
     * @param pageSize 每页大小
     */
    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    // ========== Getter / Setter ==========

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
