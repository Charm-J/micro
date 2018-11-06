package com.jeff.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.jeff.api.common.Const;
import com.jeff.api.exception.AppException;
import com.jeff.api.exception.ExceptionEnum;
import com.jeff.api.model.User;
import com.jeff.controller.annotation.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Interceptor implements HandlerInterceptor {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    private static final String ORIGIN = "origin";
    private static final String TRUE = "true";
    private static final String METHODS = "GET, POST, PUT, DELETE, OPTIONS";
    private static final String HEADERS = "X-Requested-With,Origin, Content-Type, Cookie, Accept";

    @Autowired
    private Redis redis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 允许跨域
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(ORIGIN));
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE);
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, METHODS);
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, HEADERS);
        // 权限校验
        if (handler instanceof HandlerMethod) {
            // 类级别
            if (null != ((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(Auth.class)) {
                validateToken(request);
                // 方法级别
            } else if (((HandlerMethod) handler).hasMethodAnnotation(Auth.class)) {
                validateToken(request);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){}


    /**
     * 校验token
     */
    private void validateToken(HttpServletRequest request) {
        String token = request.getHeader(Const.TOKEN);
        if (StringUtils.isBlank(token)) {
            // 鉴权失败
            throw new AppException(ExceptionEnum.TOKEN_INVALID);
        }
        // 为空则需要重新登录-场景：时效内未操作
        if (redis.hasKey(Const.RedisKeys.TOKEN_NAMESPACE + token)) {
            throw new AppException(ExceptionEnum.UNAUTHORIZED);
        } else {
            // key还存在则每次请求重新刷新失效时间
            redis.expire(Const.RedisKeys.TOKEN_NAMESPACE + token, Const.EXPIRED);
        }
        String user = redis.get(Const.RedisKeys.TOKEN_NAMESPACE + token);
        request.setAttribute(Const.USER, JSONObject.parseObject(user, User.class));
        request.setAttribute(Const.TOKEN, token);
    }
}
