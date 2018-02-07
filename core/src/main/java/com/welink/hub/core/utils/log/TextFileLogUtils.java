/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.utils.log;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;

public class TextFileLogUtils {

    public static void logAsTextFile(String loggerName, List<String> fieldNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String val = MDC.get(fieldNames.get(i));
            sb.append(val == null ? StringUtils.EMPTY : val);
            if (i != fieldNames.size() - 1) {
                sb.append("\001");
            }
        }
        LoggerFactory.getLogger(loggerName).info(sb.toString());
    }

    public static void putToContext(String key, Object value) {
        MDC.put(key, value == null ? StringUtils.EMPTY : value.toString());
    }

    public static void putToContext(String key, List<?> values) {
        StringBuilder sb = new StringBuilder();
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                sb.append(values.get(i).toString());
                if (i != values.size() - 1) {
                    sb.append("\002");
                }
            }
        }
        MDC.put(key, sb.toString());
    }

    public static void clearContext() {
        MDC.clear();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        MDC.setContextMap(contextMap);
    }

    public static Map<String, String> getCopyOfContextMap() {
        Map mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap == null) {
            return Maps.newHashMap();
        }
        Map<String, String> result = Maps.newHashMap();
        for (Object key : mdcMap.keySet()) {
            Object value = mdcMap.get(key);
            if (value != null) {
                result.put(key.toString(), value.toString());
            }
        }
        return result;
    }
}
