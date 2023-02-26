package com.bilibili.common.constant;

public enum CodeEnum {
    SUCCESS(10000, "ok"),

    USER_EXIST(21001,"该用户已被注册"),
    USER_NOTEXIST(21002,"用户不存在"),
    USER_NAME_ILLEGAL(21003,"用户名或密码不符合规范"),
    USER_UPDATE_EXCEPTION(21003,"用户信息更新失败"),
    PHONE_EXIST_EXCEPTION(21004,"存在相同的手机号"),
    LOGINACCT_PASSWORD_EXCEPTION(21005,"账号或密码错误"),
    LOGINACCT_VERIFACATIONCODE_EXCEPTION(21006,"验证码无效"),


    NO_VIDEO_EXCEPTION(31000,"视频不存在"),
    NO_COMMENT_EXCEPTION(32000,"评论不能为空"),

    UNKNOW_EXCEPTION(50000,"系统未知异常"),
    VAILD_EXCEPTION(50001,"参数格式校验失败"),
    TO_MANY_REQUEST(50002,"请求流量过大，请稍后再试"),
    SMS_CODE_EXCEPTION(50002,"验证码获取频率太高，请稍后再试"),
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
