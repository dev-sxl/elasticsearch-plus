package com.zyx.elasticsearchplus.sample.po;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 基础标签库
 *
 * @author sxl
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class TagBasePo {

    /**
     * 标签分类
     */
    private String category;
    /**
     * 一级标签
     */
    private String level1;
    /**
     * 二级标签
     */
    private String level2;
    /**
     * 搜索关键字
     */
    private String searchKeywords;
    /**
     * 搜索关键字(IK分词)
     */
    private String ikSearchKeywords;
    /**
     * 标签匹配的查询语句
     */
    private String matchDsl;
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

    public String getMatchDsl() {
        if (StringUtils.isNotEmpty(matchDsl)) {
            return matchDsl.trim();
        }
        return matchDsl;
    }
}
