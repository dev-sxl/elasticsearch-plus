package com.xyz.elasticsearchplus.core.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author xuli
 * @version 2019/11/15
 */
@Data
public class BaseElasticPo {
    /**
     * 唯一主键
     */
    private String id;
    /**
     * 创建时间戳,毫秒
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间戳, 毫秒
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 是否有效, 1:有效, 0:无效
     */
    private Integer valid;
    /**
     * sort数据
     */
    private List<Object> sortList;


}
