package com.xyz.elasticsearchplus.annotation;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticFiledSort {
    Class type() default FieldSortBuilder.class;

    String fieldName() default StringUtils.EMPTY;

    SortOrder orderBy() default SortOrder.ASC;

    DistanceUnit unit() default DistanceUnit.METERS;
}

