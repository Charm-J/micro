package com.jeff.api.exception;

public enum ExceptionEnum {
    INTERNAL_ERROR(10001, "系统异常"),
    RES_NOT_FOUND(10002, "资源不存在"),
    UNAUTHORIZED(10003, "登录信息过期，请重新登录"),
    PARAM_INVALID(10004, "参数非法"),
    WRONG_REQ(10005, "请求参数错误"),
    ERR_REQ_TYPE(10006, "请求类型错误"),
    TOKEN_INVALID(10007, "鉴权失败"),

    NOT_A_MULTIPART_REQUEST(20001, "Current request is not a multipart request"),
    USER_PWD_NOT_RIGHT(20002, "用户名密码不正确"),

    MQ_PRODUCT_ERROR(30001, "MQ生产消息失败"),
    MQ_CONSUME_ERROR(30002, "MQ消费消息失败"),
    DETAIL_MESSAGE_PUSH_ERR(30003, "MQ消费消息推送失败");

    private Integer code;
    private String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
