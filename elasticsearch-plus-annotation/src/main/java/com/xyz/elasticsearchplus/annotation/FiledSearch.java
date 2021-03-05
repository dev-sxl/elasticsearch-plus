package com.xyz.elasticsearchplus.annotation;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author sxl
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FiledSearch {

    Model model() default Model.EQUALS;

    Method method() default Method.MUST;

    String key() default "";

    Operator matchOperator() default Operator.OR;

    MultiMatchQueryBuilder.Type multiMatchType() default MultiMatchQueryBuilder.Type.PHRASE;

    boolean combined() default false;

    boolean exists() default false;

    enum Model {
        /**
         * ==
         */
        EQUALS,
        /**
         * %filed%
         */
        LIKE,
        /**
         * filed >= value
         */
        GTE,
        /**
         * filed <= value
         */
        LTE,
        MATCH_PHRASE,
        /**
         * filed%
         */
        START_WITH,
        MATCH,
        MULTI_MATCH
    }

    enum Method {
        MUST,
        FILTER,
        SHOULD,
        MUST_NOT
    }
}

