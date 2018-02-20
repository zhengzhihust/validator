/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.common.log;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhengzhi
 */
public class LogUtils {

    public static void putToContext(String key, Object value) {
        if (value == null) {
            TextFileLogUtils.putToContext(key, StringUtils.EMPTY);
        } else if (value instanceof List) {
            TextFileLogUtils.putToContext(key, (List<?>) value);
        } else {
            TextFileLogUtils.putToContext(key, value);
        }

    }

    public static void putToContext(String key, List<?> values) {
        TextFileLogUtils.putToContext(key, values);
    }

    public static void clearContext() {
        TextFileLogUtils.clearContext();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        TextFileLogUtils.setContextMap(contextMap);
    }

    public static Map<String, String> getCopyOfContextMap() {
        return TextFileLogUtils.getCopyOfContextMap();
    }

    public static void logAsTextFile(String loggerName, List<String> fieldNames) {
        TextFileLogUtils.putToContext(Attributes.LOG_FDATE, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        List<String> l = Lists.newArrayList();
        l.add(Attributes.TRACE_ID);
        l.add(Attributes.LOG_FDATE);
        TextFileLogUtils.logAsTextFile(loggerName, l);
    }

    public interface Attributes {
        String LOG_FDATE = "log_fdate";
        String TRACE_ID = "trace_id";
    }
}
