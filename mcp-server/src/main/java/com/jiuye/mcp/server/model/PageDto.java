package com.jiuye.mcp.server.model;

import java.util.List;

/**
 * 分页对象
 *
 * @author kevin
 * @date 2018-08-23
 */
public class PageDto {

    private int currentPage = 1;
    private int pageSize = 5;
    private long total = 0;
    private List<?> list = null;

    public PageDto() {
    }

    public PageDto(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PageDto(int currentPage, int pageSize, long total, List<?> list) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
