/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.dto;

import java.io.Serializable;

public abstract class AbstractDataEntry<T> {

    public abstract int getVersion();

    public abstract T getValue();

    public abstract int getExpireTime();

    public abstract long getCreateTime();

    public abstract Serializable getKey();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractDataEntry [getVersion()=");
        builder.append(getVersion());
        builder.append(", getValue()=");
        builder.append(getValue());
        builder.append(", getExpireTime()=");
        builder.append(getExpireTime());
        builder.append(", getCreateTime()=");
        builder.append(getCreateTime());
        builder.append(", getKey()=");
        builder.append(getKey());
        builder.append("]");
        return builder.toString();
    }
}

