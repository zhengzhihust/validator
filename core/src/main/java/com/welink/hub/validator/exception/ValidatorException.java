/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.exception;

import com.welink.hub.validator.config.ResultCode;

public class ValidatorException extends RuntimeException {
    private static final long serialVersionUID = -3376094911380142870L;
    private int               code;
    private String            message;
    private Object            data;

    public ValidatorException() {
        this(ResultCode.SERVER_ERROR);
    }

    public ValidatorException(int code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ValidatorException(int code) {
        super();
        this.code = code;
    }

    public ValidatorException(int code, String message) {
        this(code, message, null);
    }

    public ValidatorException(ResultCode resultCode) {
        this(resultCode, resultCode.getMessage());
    }

    public ValidatorException(ResultCode resultCode, Object data) {
        this(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public ValidatorException(ResultCode resultCode, String messsage) {
        this(resultCode.getCode(), messsage, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ValidatorException [code=");
        builder.append(code);
        builder.append(", message=");
        builder.append(message);
        builder.append(", data=");
        builder.append(data);
        builder.append("]");
        return builder.toString();
    }
}
