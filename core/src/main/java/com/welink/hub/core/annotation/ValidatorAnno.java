/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.annotation;

import org.springframework.core.annotation.AliasFor;
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
public @interface ValidatorAnno {
    /**
     * master or slave
     * @return
     */
    @AliasFor("chain")
    String value() default "";

    /**
     * chain name
     * @return
     */
    @AliasFor("value")
    String chain() default "";

    /**
     * order
     * @return
     */
    int order() default 1;

    /**
     *
     * @return
     */
    boolean exception() default false;
}
