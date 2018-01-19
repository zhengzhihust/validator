/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.config;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:META-INF/spring/core.xml")
public class InterceptorConfig {

    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setBeanNames("threadPoolService");
        beanNameAutoProxyCreator.setInterceptorNames("interceptor");
        return beanNameAutoProxyCreator;
    }
}
