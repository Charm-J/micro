package com.jeff.controller.user;

import com.jeff.api.common.Result;
import com.jeff.api.model.vo.LoginReq;
import com.jeff.api.service.UserService;
import com.jeff.api.utils.ParamUtil;
import com.jeff.controller.annotation.Auth;
import com.jeff.controller.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 用户控制类
 * @author DJ
 * @date 2018/10/30 14:13
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //@Auth
    @Log
    @GetMapping("/login")
    public Result login(@RequestBody LoginReq loginReq, BindingResult result){
        ParamUtil.valid(result);
        return userService.login(loginReq);
    };
}
