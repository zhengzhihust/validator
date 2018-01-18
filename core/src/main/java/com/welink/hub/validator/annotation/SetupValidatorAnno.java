/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ValidatorAnno(order = -1)
public @interface SetupValidatorAnno {
    @AliasFor("chain")
    String value() default "";

    @AliasFor("value")
    String chain() default "";
}
