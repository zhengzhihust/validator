/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ABTest {
    Class<?> testFor();

    String mainConfigKey();

    String loggerName() default "";

    String[] logFieldNames() default {};
}
