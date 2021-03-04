package com.xyz.elasticsearchplus.core.bean;

import com.xyz.elasticsearchplus.annotation.ElasticFiled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private ElasticFiled.Model model;
    private ElasticFiled.Method method;
    private Operator matchOperator;
    private MultiMatchQueryBuilder.Type multiMatchType;
    private String key;
    private String fieldName;
    private Object value;
    private boolean combined;
    private boolean exists;
    private String path;
    private Class subType;

    public FiledDto(ElasticFiled.Method method) {
        this.method = method;
    }
}
