/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.core.context;

import com.google.common.collect.Maps;
import com.welink.hub.core.utils.log.LogUtils;

import java.util.Map;

public class ContextManager {

    private static final ThreadLocal<Context> CACHED_CONTEXT = new ThreadLocal<>();

    public static void clear() {
        // do something
        LogUtils.clearContext();
        remove();
    }

    public static Context build() {
        Context current = CACHED_CONTEXT.get();
        if (current == null) {
            CACHED_CONTEXT.set(current = new Context());
        }
        return current;
    }

    public static void build(Context context) {
        CACHED_CONTEXT.set(context);
        for (Map.Entry<String, Object> entry : context.getContextMap().entrySet()) {
            LogUtils.putToContext(entry.getKey(), entry.getValue());
        }
    }

    private static void remove() {
        CACHED_CONTEXT.remove();
    }

    public static void put(String key, Object value) {
        build().put(key, value);
        LogUtils.putToContext(key, value);
    }

    public static Map<String, String> getCopyOfContextMap() {
        return LogUtils.getCopyOfContextMap();
    }

    public static Context getContext() {
        return CACHED_CONTEXT.get();
    }

    public static Object get(String key) {
        return build().get(key);
    }

    public static String getString(String key) {
        return get(key, String.class);
    }

    public static <T> T get(String key, Class<T> clazz) {
        return build().get(key, clazz);
    }

    public static class Context {
        private Map<String, Object> context = Maps.newHashMap();

        public void put(String key, Object value) {
            context.put(key, value);
        }

        public Object get(String key) {
            return context.get(key);
        }

        public <T> T get(String key, Class<T> clazz) {
            return clazz.cast((get(key)));
        }

        public Map<String, Object> getContextMap() {
            return context;
        }
    }
}
