package com.jeff.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jeff.api.common.Result;
import com.jeff.api.model.vo.LoginReq;
import com.jeff.api.service.UserService;
import com.jeff.api.utils.ParamUtil;
import com.jeff.controller.annotation.Log;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制类
 *
 * @author DJ
 * @date 2018/10/30 14:13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    //@Autowired -- 此处不能使用Autowired
    @Reference(version = "1.0.0")
    private UserService userService;


    /**
     * 用户登录
     *
     * @param loginReq
     * @param result
     * @return
     */
    //@Auth
    @Log
    @GetMapping("/login")
    public Result login(@RequestBody LoginReq loginReq, BindingResult result) {
        ParamUtil.valid(result);
        return userService.login(loginReq);
    }

    ;
}
