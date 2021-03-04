package com.xyz.elasticsearchplus.core.bean;

import lombok.Data;

import java.util.List;

/**
 * @author sxl
 * @since 2019-12-13 17:23
 */
@Data
public class PageResult<T> {
    /** 总记录数 */
    private Long total;
    /** 总页数 */
    private int totalPage;
    /** 页码 */
    private int pageNo = 1;
    /** 每页大小 */
    private int pageSize;
    /** 本页数据 */
    private List<T> data;

    public PageResult(long total, int totalPage, Integer pageNo, int pageSize, List<T> data) {
        this.total = total;
        this.totalPage = totalPage;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.data = data;
    }

    public PageResult(long total, Integer pageNo, int pageSize, List<T> data) {
        this.total = total;
        this.totalPage = (int) (total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.data = data;
    }

    public PageResult(PageResult<Object> pageDto, List<T> data) {
        this.data = data;
        this.total = pageDto.getTotal();
        this.totalPage = pageDto.getTotalPage();
        this.pageNo = pageDto.getPageNo();
        this.pageSize = pageDto.getPageSize();
    }
}
