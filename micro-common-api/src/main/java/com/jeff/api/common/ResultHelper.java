package com.jeff.api.common;


import com.jeff.api.exception.ExceptionEnum;

public class ResultHelper {

    public static Result ok(Object object) {
        return new Result(0, Const.MSG_OK, object);
    }

    public static Result ok(int code, Object object) {
        return new Result(code, Const.MSG_OK, object);
    }

    public static Result ok(int code) {
        return new Result(code, Const.MSG_OK, null);
    }

    public static Result success() {
        return ok(null);
    }

    public static Result error(Integer code, String msg) {
        return new Result(code, msg);
    }

    public static Result error(Integer code) {
        return new Result(code);
    }

    public static Result error(ExceptionEnum ee) {
        return new Result(ee.getCode(), ee.getMsg());
    }
}
