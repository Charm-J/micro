package com.jeff.controller.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author DJ
 * @date 2018/10/31 14:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Auth {
}

