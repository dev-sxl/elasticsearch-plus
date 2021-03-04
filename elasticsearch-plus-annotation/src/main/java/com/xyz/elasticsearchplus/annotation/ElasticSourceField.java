package com.xyz.elasticsearchplus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author sxl
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticSourceField {

    String[] source() default {};

    SourceType type() default SourceType.INCLUDE;

    enum SourceType {
        INCLUDE,
        EXCLUDE
    }
}

