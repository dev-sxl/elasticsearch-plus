package com.xyz.elasticsearchplus.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * @author sxl
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticNested {

    String path() default StringUtils.EMPTY;

    ElasticFiled.Method method() default ElasticFiled.Method.MUST;


}
