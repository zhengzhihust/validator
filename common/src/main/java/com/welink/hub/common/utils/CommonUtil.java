/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.common.utils;

import com.fasterxml.uuid.Generators;
import com.welink.hub.common.log.LogUtils;
import org.slf4j.MDC;

/**
 * @author zhengzhi
 */
public class CommonUtil {

    public static String getTraceId() {
        return Generators.randomBasedGenerator().generate().toString();
    }

    public static String getTraceIdAndPutToMdc() {
        String traceId = getTraceId();
        MDC.put(LogUtils.Attributes.TRACE_ID, traceId);
        return traceId;
    }
}
