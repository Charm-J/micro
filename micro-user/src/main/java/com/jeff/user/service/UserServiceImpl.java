package com.jeff.user.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.jeff.api.common.Result;
import com.jeff.api.common.ResultHelper;
import com.jeff.api.exception.ExceptionEnum;
import com.jeff.api.model.bo.MsgTemplateModel;
import com.jeff.api.model.vo.LoginReq;
import com.jeff.api.service.UserService;
import com.jeff.api.utils.MD5Util;
import com.jeff.user.common.MqMsgHandle;
import com.jeff.user.dao.UserMapper;
import com.jeff.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 用户实现类
 *
 * @author DJ
 * @date 2018/10/30 14:13
 */
@Service(version = "${my.service.version}")
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MqMsgHandle mqMsgHandle;

    @Value("${my.message.url.loginSuccess}")
    private String LOGIN_SUCCESS;

    @Override
    public Result login(LoginReq loginReq) {
        try {
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("name", loginReq.getUsername())
                    .andEqualTo("pwd", MD5Util.MD5(loginReq.getPassword()));
            List<User> users = userMapper.selectByExample(example);
            if (!users.isEmpty()) {
                if (StringUtils.isNotBlank(users.get(0).getEmail())) {
                    // 登录成功-发送邮件消息提醒
                    MsgTemplateModel ms = new MsgTemplateModel(users.get(0).getEmail(), "登录成功提示信息", "登录成功啦！");
                    mqMsgHandle.send(ms, LOGIN_SUCCESS);
                }
                return ResultHelper.success();
            } else {
                return ResultHelper.error(ExceptionEnum.USER_PWD_NOT_RIGHT);
            }
        } catch (Exception e) {
            return ResultHelper.error(ExceptionEnum.INTERNAL_ERROR);
        }
    }

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }


}