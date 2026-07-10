package com.rental.dto;

/**
 * 分页请求参数
 */
public class PageRequest {

    /** 当前页码（从 1 开始） */
    private int page = 1;

    /** 每页大小 */
    private int pageSize = 10;

    /**
     * 获取数据库偏移量（用于 LIMIT offset, size）
     *
     * @return 偏移量
     */
    public int getOffset() {
        return (page - 1) * pageSize;
    }

    // ========== Getter / Setter ==========

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if (page < 1) {
            page = 1;
        }
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
        this.pageSize = pageSize;
    }
}
