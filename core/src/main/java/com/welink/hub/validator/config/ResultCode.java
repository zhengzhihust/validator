/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.config;

public class ResultCode {
    public static final ResultCode SUCCESS = new ResultCode(1, "SUCCESS");
    public static final ResultCode SERVER_ERROR = new ResultCode(10000, "SERVER_ERROR");

    private int code;
    private String message;

    public ResultCode() {

    }

    public ResultCode(int code) {
        this(code, null);
    }

    public ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
