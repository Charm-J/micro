package com.jeff.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jeff.api.common.Result;
import com.jeff.api.model.vo.LoginReq;
import com.jeff.api.service.UserService;
import com.jeff.api.utils.ParamUtil;
import com.jeff.controller.annotation.Log;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制类
 *
 * @author DJ
 * @date 2018/10/30 14:13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(
            version = "${my.service.version}",
            check = false)
    private UserService userService;


    /**
     * 用户登录
     *
     * @param loginReq
     * @return
     */
    @Log
    @PostMapping("/login")
    public Result login(@RequestBody LoginReq loginReq) {
        return userService.login(loginReq);
    }

    /**
     * 测试
     * @param name
     * @return
     */
    @Log
    @GetMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return userService.sayHello(name);
    }
}
