package com.xyz.elasticsearchplus.operator7.annotation;

import com.xyz.elasticsearchplus.operator7.config.AutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sxl
 * @since 2020/7/30 11:28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AutoConfig.class})
public @interface EnableElasticsearchPlus {
}
