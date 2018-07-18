/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.exception;

/**
 * @author zhengzhi
 */
public class CacheVersionException extends Exception {

    private static final long serialVersionUID = 7218244504080649560L;

    public CacheVersionException() {
        super();
    }

    public CacheVersionException(String message) {
        super(message);
    }

    public CacheVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheVersionException(Throwable cause) {
        super(cause);
    }
}
