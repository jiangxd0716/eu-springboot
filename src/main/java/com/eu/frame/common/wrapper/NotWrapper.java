package com.eu.frame.common.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注某个 Controller 的接口不会被包装 , 同样该方法抛出的任何异常也不会被异常拦截重新包装
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotWrapper {

}
