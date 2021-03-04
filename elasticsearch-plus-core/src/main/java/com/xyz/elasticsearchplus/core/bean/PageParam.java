package com.xyz.elasticsearchplus.core.bean;


/**
 * @author sxl
 * @since 2019-12-13 17:33
 */
public class PageParam {
    /** 页码 从1开始 */
    private int pageNo = 1;
    /** 每页大小 */
    private int pageSize = 20;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
