/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.context;

import com.welink.hub.validator.log.LogUtils;
import com.welink.hub.validator.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class RunnableWithContext implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RunnableWithContext.class);

    @Override
    public void run() {
        try {
            buildContext();
            doRun();
            if (getLogFieldNames() != null) {
                LogUtils.logAsTextFile(getLoggerName(), getLogFieldNames());
            }
        } catch (Throwable t) {
            LOG.error("unexpected exception", t);
        } finally {
            ContextManager.clear();
        }
    }

    public void buildContext() {
        ContextManager.put(LogUtils.Attributes.TRACE_ID, CommonUtil.getTraceId());
    }

    public abstract void doRun();

    public String getLoggerName() {
        return null;
    }

    public List<String> getLogFieldNames() {
        return null;
    }
}
