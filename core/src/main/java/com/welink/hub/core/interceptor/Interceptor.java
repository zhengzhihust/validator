/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.welink.hub.common.log.LogUtils;
import com.welink.hub.common.utils.CommonUtil;
import com.welink.hub.core.config.Result;
import com.welink.hub.core.context.ContextManager;
import com.welink.hub.core.exception.ValidatorException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Interceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Interceptor.class);
    private String loggerName;
    private List<String> logFieldNames;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ContextManager.put(LogUtils.Attributes.TRACE_ID, CommonUtil.getTraceId());
        Object obj = null;
        String methodName = invocation.getMethod().getName();
        if (LOG.isInfoEnabled()) {
            LOG.info("in args {} method {}", JSON.toJSONString(invocation.getArguments()), methodName);
        }
        try {
            obj = invocation.proceed();
            if (loggerName != null) {
                LogUtils.logAsTextFile(loggerName, logFieldNames);
            }
        } catch (ValidatorException e) {
            LOG.error("catch expected exception", e);
            obj = Result.result(e.getCode(), e.getMessage());
        } finally {
            if (LOG.isInfoEnabled()) {
                LOG.info("return obj {}", JSON.toJSONString(obj));
            }
        }
        return obj;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public void setLogFieldNames(List<String> logFieldNames) {
        this.logFieldNames = logFieldNames;
    }
}
