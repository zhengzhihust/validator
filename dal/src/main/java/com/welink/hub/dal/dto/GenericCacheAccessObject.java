/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.dto;

import com.welink.hub.dal.cao.Cache;
import com.welink.hub.dal.cao.SerializeHandler;
import com.welink.hub.dal.exception.CacheVersionException;
import com.welink.hub.dal.exception.IncrOutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author zhengzhi
 */
public class GenericCacheAccessObject<T> implements CacheAccessObject<T> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericCacheAccessObject.class);
    private SerializeHandler<T> serializeHandler;
    private Cache cache;
    private String prefix;
    private Integer expireTime;

    @Override
    public void cas(Serializable key, T t, int version) throws CacheVersionException {
        cas(key, t, version, expireTime);
    }

    @Override
    public AbstractDataEntry<T> get(Serializable key) {
        key = addPrefix(key);
        AbstractDataEntry<Object> abstractDataEntry = cache.get(key);
        return transDataEntry(abstractDataEntry);
    }

    @Override
    public void delete(Serializable key) {
        key = addPrefix(key);
        cache.delete(key);
    }

    @Override
    public boolean deleteNoEx(Serializable key) {
        key = addPrefix(key);
        try {
            cache.delete(key);
            return true;
        } catch (Throwable t) {
            LOG.error("server error key: " + key, t);
            return false;
        }
    }

    @Override
    public T getValue(Serializable key) {
        AbstractDataEntry<T> abstractDataEntry = get(key);
        if (abstractDataEntry == null) {
            return null;
        } else {
            return abstractDataEntry.getValue();
        }
    }

    @Override
    public void put(Serializable key, T t) {
        put(key, t, expireTime);
    }

    @Override
    public T getValueNoEx(Serializable key) {
        try {
            return getValue(key);
        } catch (Throwable t) {
            LOG.error("get value exception key " + key, t);
            return null;
        }
    }

    @Override
    public boolean putNoEx(Serializable key, T t) {
        return putNoEx(key, t, expireTime);
    }

    @Override
    public void cas(Serializable key, T t, int version, int expireTime) throws CacheVersionException {
        key = addPrefix(key);
        Serializable value = null;
        if (serializeHandler == null) {
            value = (Serializable) t;
        } else {
            value = serializeHandler.serialize(t);
        }
        cache.cas(key, value, version, expireTime);
    }

    private Serializable addPrefix(Serializable key) {
        if (prefix != null) {
            return prefix + key;
        } else {
            return key;
        }
    }

    @Override
    public void put(Serializable key, T t, int expireTime) {
        try {
            cas(key, t, 0, expireTime);
        } catch (CacheVersionException e) {
            LOG.error("unreachable code");
        }
    }

    @Override
    public boolean putNoEx(Serializable key, T t, int expireTime) {
        try {
            put(key, t, expireTime);
            return true;
        } catch (Throwable ex) {
            LOG.error("put has exception", t);
            return false;
        }
    }

    @Override
    public Integer incr(Serializable key, int expireTime, int upperBound) throws IncrOutOfBoundsException {
        key = addPrefix(key);
        return cache.incr(key, 1, 0, expireTime, 0, upperBound);
    }

    @Override
    public Integer incr(Serializable key, int upperBound) throws IncrOutOfBoundsException {
        key = addPrefix(key);
        return cache.incr(key, 1, 0, expireTime, 0, upperBound);
    }

    private AbstractDataEntry<T> transDataEntry(final AbstractDataEntry<Object> abstractDataEntry) {
        if (abstractDataEntry == null) {
            return null;
        }
        return new AbstractDataEntry<T>() {

            @Override
            public int getVersion() {
                return abstractDataEntry.getVersion();
            }

            @Override
            public T getValue() {
                Object value = abstractDataEntry.getValue();
                if (serializeHandler == null) {
                    return (T) value;
                } else {
                    return serializeHandler.deserialize(value);
                }
            }

            @Override
            public Serializable getKey() {
                return (Serializable) abstractDataEntry.getKey();
            }

            @Override
            public int getExpireTime() {
                return abstractDataEntry.getExpireTime();
            }

            @Override
            public long getCreateTime() {
                return abstractDataEntry.getCreateTime();
            }
        };
    }

    public void setSerializeHandler(SerializeHandler<T> serializeHandler) {
        this.serializeHandler = serializeHandler;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

}

