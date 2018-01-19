/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.dao;

public interface BaseDAO<Pk, DO> {

    DO select(Pk pk);

    Object insert(DO d);

    int update(DO d);

    int delete(Pk pk);
}

