package com.jeff.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author DJ
 * @date 2018/10/31 14:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log {
    enum type{
        IN_PARAM,//入参
        OUT_PARAM,//出参
        ELAPSE,//耗时
        NULL//都要
    }
    type exclude() default type.NULL;//不要哪个参数
}