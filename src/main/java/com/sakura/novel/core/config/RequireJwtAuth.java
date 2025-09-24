package com.sakura.novel.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JWT认证注解
 * 用于标识需要JWT token验证的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireJwtAuth {
    /**
     * 是否验证userId与路径参数一致
     */
    boolean validateUserId() default true;

    /**
     * 描述信息
     */
    String value() default "";
}
