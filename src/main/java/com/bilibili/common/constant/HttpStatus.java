package com.bilibili.common.constant;

public enum HttpStatus {
    TOKEN_EXPIRE(177,"token失效");
    private Integer code;

    private String message;

    HttpStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
