/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
