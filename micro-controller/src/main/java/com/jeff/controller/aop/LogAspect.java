package com.jeff.controller.aop;

import com.alibaba.fastjson.JSONObject;
import com.jeff.controller.annotation.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 服务日志切面，主要记录接口日志及方法耗时
 * 特别注意：请求参数含有request,不要使用@Log注解
 * --原因是JSONObject.toJSONString受影响
 **/
@Aspect
@Component
public class LogAspect {

    private Logger logger = LoggerFactory.getLogger(getClass());
    ThreadLocal<Long> startTime = new ThreadLocal<Long>();
    ThreadLocal<String> methodName = new ThreadLocal<String>();
    ThreadLocal<Log.type> type = new ThreadLocal<Log.type>();

    @Pointcut("@annotation(com.jeff.controller.annotation.Log)")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        try {
            type.set(getLogExcludeType(joinPoint));
        } catch (Exception e) {
            logger.error("获取注解类型异常", e);
        }
        if (!type.get().equals(Log.type.IN_PARAM)) {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String name = joinPoint.getSignature().getName();
            methodName.set(name);
            // 记录下请求内容
            logger.info("请求信息#URL:" + request.getRequestURL() + " #TYPE:" + request.getMethod() + " #IP:" + request.getRemoteAddr());
            logger.info("方法<" + name + ">入参：" + JSONObject.toJSONString(joinPoint.getArgs()));
        }

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        logger.info("方法<" + methodName.get() + ">耗时:" + (System.currentTimeMillis() - startTime.get()) + "ms");
    }

    private Log.type getLogExcludeType(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        Log.type exclude = Log.type.NULL;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    Log.type temp = method.getAnnotation(Log.class).exclude();
                    if (temp != null) {
                        exclude = temp;
                    }
                    break;
                }
            }
        }
        return exclude;
    }

}
