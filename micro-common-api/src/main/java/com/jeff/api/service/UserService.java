package com.jeff.api.service;

import com.jeff.api.common.Result;
import com.jeff.api.model.vo.LoginReq;

/**
 *
 * 用户接口
 * @author DJ
 * @date 2018/10/30 14:13
 *
 */
public interface UserService {

    /**
     * 登录接口
     * @param loginReq
     * @return Result
     */
    Result login(LoginReq loginReq);
}
