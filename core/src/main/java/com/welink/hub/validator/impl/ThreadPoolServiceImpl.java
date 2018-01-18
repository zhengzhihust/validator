/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.impl;

import com.welink.hub.validator.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("threadPoolService")
public class ThreadPoolServiceImpl implements ThreadPoolService {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolServiceImpl.class);
    @Override
    public void execute(Runnable task, int retryTimes) {

    }

    @Override
    public void execute(Runnable task) {

    }
}
