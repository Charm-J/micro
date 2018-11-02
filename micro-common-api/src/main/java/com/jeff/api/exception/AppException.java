package com.jeff.api.exception;

public class AppException extends RuntimeException {
    private Integer code;

    public AppException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
    }

    public AppException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public AppException(ExceptionEnum exceptionEnum, String msg) {
        super(exceptionEnum.getMsg() + ":" + msg);
        this.code = exceptionEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
