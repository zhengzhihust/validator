/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.impl;

import com.welink.hub.validator.ValidatorExecutor;
import com.welink.hub.validator.dto.ValidateContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ValidatorExecutorImpl implements ValidatorExecutor, ApplicationContextAware {

    @Override
    public void execute(ValidateContext context, String chain) {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
