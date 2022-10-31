package com.bilibili.common.constant;

public enum CodeEnum {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    TO_MANY_REQUEST(10002,"请求流量过大，请稍后再试"),
    SMS_CODE_EXCEPTION(10002,"验证码获取频率太高，请稍后再试"),
    USER_EXIST(15001,"存在相同的用户"),
    USER_NOTEXIST(15001,"用户不存在"),
    PHONE_EXIST_EXCEPTION(15002,"存在相同的手机号"),
    NO_VIDEO_EXCEPTION(21000,"视频不存在"),
    LOGINACCT_PASSWORD_EXCEPTION(15003,"账号或密码错误"),
            ;

    private Integer code;

    private String message;

    CodeEnum(Integer code, String message) {
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
