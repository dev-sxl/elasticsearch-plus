package com.xyz.elasticsearchplus.core.bean;

import com.xyz.elasticsearchplus.annotation.FieldSource;
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
    @FieldSource
    private List<String> sourceList;
    /**
     * 排除字段列表
     */
    @FieldSource(type = FieldSource.SourceType.EXCLUDE)
    private List<String> sourceExcludeList;
}
