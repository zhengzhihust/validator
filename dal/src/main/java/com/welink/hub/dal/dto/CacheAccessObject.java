/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.dto;

import com.welink.hub.dal.exception.CacheVersionException;
import com.welink.hub.dal.exception.IncrOutOfBoundsException;

import java.io.Serializable;

public interface CacheAccessObject<T> {

    /**
     * without version
     *
     * @param key
     * @return
     */
    T getValue(Serializable key);

    T getValueNoEx(Serializable key);

    AbstractDataEntry<T> get(Serializable key);

    void cas(Serializable key, T t, int version) throws CacheVersionException;

    void put(Serializable key, T t);

    boolean putNoEx(Serializable key, T t);

    void cas(Serializable key, T t, int version, int expireTime) throws CacheVersionException;

    void put(Serializable key, T t, int expireTime);

    boolean putNoEx(Serializable key, T t, int expireTime);

    void delete(Serializable key);

    boolean deleteNoEx(Serializable key);

    Integer incr(Serializable key, int expireTime, int upperBound) throws IncrOutOfBoundsException;

    Integer incr(Serializable key, int upperBound) throws IncrOutOfBoundsException;
}

