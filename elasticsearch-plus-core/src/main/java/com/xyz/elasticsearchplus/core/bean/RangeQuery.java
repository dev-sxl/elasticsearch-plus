package com.xyz.elasticsearchplus.core.bean;

import lombok.Data;

/**
 * @author lihongbin
 * @version 2019/12/28
 */
@Data
public class RangeQuery {
    /**
     * 大于等于
     */
    private String gte;
    /**
     * 大于
     */
    private String gt;
    /**
     * 小于等于
     */
    private String lte;
    /**
     * 小于
     */
    private String lt;
}
