/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ValidatorAnno {
    @AliasFor("chain")
    String value() default "";

    @AliasFor("value")
    String chain() default "";

    int order() default 1;

    boolean exception() default false;
}
