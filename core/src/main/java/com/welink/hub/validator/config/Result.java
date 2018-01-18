/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.validator.config;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = -5349777375533512347L;
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> result(T data) {
        return result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static Result<Void> result(ResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMessage());
    }

    public static Result<Void> result(ResultCode resultCode, String message) {
        return result(resultCode.getCode(), message);
    }

    public static Result<Void> result(int code, String message) {
        return result(code, message, null);
    }

    public static <T> Result<T> resultWithClass(ResultCode resultCode, Class<T> clazz) {
        return Result.result(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> resultWithClass(int code, String message, Class<T> clazz) {
        return Result.result(code, message, null);
    }

    public static <T> Result<T> result(int code, String message, T data) {
        Result<T> result = new Result<T>();
        result.code = code;
        result.message = message;
        result.data = data;
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccessful() {
        return code < 10000 && code > 0;
    }

    public boolean isExecuteSuccessful() {
        return !(code == ResultCode.SERVER_ERROR.getCode());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Result [code=");
        builder.append(code);
        builder.append(", message=");
        builder.append(message);
        builder.append(", data=");
        builder.append(data);
        builder.append("]");
        return builder.toString();
    }
}