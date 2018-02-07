/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.impl;

import com.welink.hub.core.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ThreadPoolServiceImpl implements ThreadPoolService {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolServiceImpl.class);

    private ThreadPoolExecutor threadPoolExecutor;
    @Value("${thread.pool.size}")
    private int threadPoolSize;
    @Value("${queue.size}")
    private int queueSize;
    @Value("${project.name}")
    private String appName;

    @PostConstruct
    public void init() {
        this.threadPoolExecutor = new ThreadPoolExecutor(0, threadPoolSize, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueSize),
                new DefaultThreadFactory(getThreadPoolName(), appName), getRejectedExecutionHandler());
    }

    @PreDestroy
    public void destroy() {
        this.threadPoolExecutor.shutdown();
    }

    @Override
    public void execute(Runnable task, int retryTimes) {
        if (retryTimes == 0) {
            try {
                threadPoolExecutor.execute(task);
            } catch (RejectedExecutionException e) {
                LOG.error("reject task:{}", task);
            }
            return;
        }
        for (int i = 0; i < retryTimes; i++) {
            try {
                threadPoolExecutor.execute(task);
            } catch (RejectedExecutionException e) {
                LOG.error("reject task:{}", task);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
    }

    @Override
    public void execute(Runnable task) {
        execute(task, 0);
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public String getThreadPoolName() {
        return "default";
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return new ThreadPoolExecutor.AbortPolicy();
    }

    public static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public DefaultThreadFactory(String poolName, String appName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = appName + "-" + poolName + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
