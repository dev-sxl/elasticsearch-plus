package com.xyz.elasticsearchplus.core.bean;

import com.xyz.elasticsearchplus.annotation.ElasticSourceField;
import lombok.Data;

import java.util.List;

/**
 * @author xuli
 * @version 2020/11/12
 */
@Data
public class SearchDto {
    /**
     * 返回值列表
     */
    @ElasticSourceField
    private List<String> sourceList;
    /**
     * 排除字段列表
     */
    @ElasticSourceField(type = ElasticSourceField.SourceType.EXCLUDE)
    private List<String> sourceExcludeList;
}
