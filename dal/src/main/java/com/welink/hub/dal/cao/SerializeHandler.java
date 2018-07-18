/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.cao;

import java.io.Serializable;

/**
 * @author zhengzhi
 */
public interface SerializeHandler<T> {

    Serializable serialize(T obj);

    T deserialize(Object in);
}