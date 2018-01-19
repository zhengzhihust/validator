/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.dal.exception;

public class IncrOutOfBoundsException extends Exception {

    private static final long serialVersionUID = 3326475484590983213L;

    public IncrOutOfBoundsException() {
        super();
    }

    public IncrOutOfBoundsException(String message) {
        super(message);
    }

    public IncrOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncrOutOfBoundsException(Throwable cause) {
        super(cause);
    }
}
