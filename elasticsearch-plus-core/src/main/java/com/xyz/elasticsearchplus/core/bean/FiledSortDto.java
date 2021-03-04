package com.xyz.elasticsearchplus.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author xuli
 * @version 2019/11/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiledSortDto {
    private Class type;
    private SortOrder orderBy;
    private String fieldName;
    private DistanceUnit unit;
    private Object value;
}
