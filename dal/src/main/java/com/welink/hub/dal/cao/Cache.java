/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.cao;

import com.welink.hub.dal.dto.AbstractDataEntry;
import com.welink.hub.dal.exception.CacheVersionException;
import com.welink.hub.dal.exception.IncrOutOfBoundsException;

import java.io.Serializable;

public interface Cache {

    AbstractDataEntry<Object> get(Serializable key);

    void cas(Serializable key, Serializable value, int version, int expireTime)
            throws CacheVersionException;

    void delete(Serializable key);

    Integer incr(Serializable key, int value, int defaultValue, int expireTime, int lowBound,
                 int upperBound) throws IncrOutOfBoundsException;

}
