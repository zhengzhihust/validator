/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core;

public interface ThreadPoolService {
    void execute(Runnable task, int retryTimes);

    void execute(Runnable task);
}
