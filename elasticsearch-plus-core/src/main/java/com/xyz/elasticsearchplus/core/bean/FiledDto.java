package com.xyz.elasticsearchplus.core.bean;

import com.google.common.base.CaseFormat;
import com.xyz.elasticsearchplus.core.annotation.FiledSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;

/**
 * @author xuli
 * @version 2019/11/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiledDto {
    private Class type;
    private FiledSearch.Model model;
    private FiledSearch.Method method;
    private Operator matchOperator;
    private MultiMatchQueryBuilder.Type multiMatchType;
    private String key;
    private String fieldName;
    private Object value;
    private boolean combined;
    private boolean exists;
    private String path;
    private Class subType;

    public FiledDto(FiledSearch.Method method) {
        this.method = method;
    }

    public String queryField() {
        String field = this.getKey();
        if (StringUtils.isBlank(field)) {
            field = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getFieldName());
        }
        if (StringUtils.isNotBlank(this.getPath())) {
            field = this.getPath() + "." + field;
        }
        return field;
    }
}
