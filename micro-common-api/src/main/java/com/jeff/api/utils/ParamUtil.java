package com.jeff.api.utils;

import com.jeff.api.exception.AppException;
import com.jeff.api.exception.ExceptionEnum;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class ParamUtil {
    public static void valid(BindingResult result) throws AppException {
        if (result.hasErrors()) {
            throw new AppException(ExceptionEnum.PARAM_INVALID.getMsg() + ":" + result.getFieldError().getDefaultMessage(), ExceptionEnum.PARAM_INVALID.getCode());
        }
    }

    public static Map<String, String> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value == null) {
                    continue;
                }
                map.put(field.getName(), value.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new AppException("对象转map异常！", ExceptionEnum.INTERNAL_ERROR.getCode());
            }
        }
        return map;
    }
}
