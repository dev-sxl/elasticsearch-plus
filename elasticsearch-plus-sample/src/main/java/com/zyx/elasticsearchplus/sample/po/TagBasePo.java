package com.zyx.elasticsearchplus.sample.po;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;
import com.xyz.elasticsearchplus.core.bean.BaseElasticPo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 基础标签库
 *
 * @author sxl
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class TagBasePo extends BaseElasticPo {

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

    public String getMatchDsl() {
        if (StringUtils.isNotEmpty(matchDsl)) {
            return matchDsl.trim();
        }
        return matchDsl;
    }
}
