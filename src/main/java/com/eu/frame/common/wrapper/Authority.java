package com.eu.frame.common.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authority {

    /**
     * 标识
     * 多个逗号分割
     *
     * @return
     */
    String mark() default "";

    /**
     * 描述信息
     *
     * @return
     */
    String name() default "";

}
