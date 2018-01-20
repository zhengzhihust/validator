/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.context;

import com.welink.hub.core.log.LogUtils;
import com.welink.hub.core.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractRunnableWithContext implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRunnableWithContext.class);

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

    /**
     * run
     */
    public abstract void doRun();

    public String getLoggerName() {
        return null;
    }

    public List<String> getLogFieldNames() {
        return null;
    }
}
