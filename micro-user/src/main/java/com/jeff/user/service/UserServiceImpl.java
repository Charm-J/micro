package com.jeff.user.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.jeff.api.common.Result;
import com.jeff.api.common.ResultHelper;
import com.jeff.api.model.vo.LoginReq;
import com.jeff.api.exception.ExceptionEnum;
import com.jeff.api.service.UserService;
import com.jeff.api.utils.MD5Util;
import com.jeff.user.dao.UserMapper;
import com.jeff.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 用户实现类
 *
 * @author DJ
 * @date 2018/10/30 14:13
 */
@Service(version = "1.0.0")
@org.springframework.stereotype.Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result login(LoginReq loginReq) {
        try {
            Example example = new Example(User.class);
            example.createCriteria().andEqualTo("name", loginReq.getUsername())
                    .andEqualTo("pwd", MD5Util.MD5(loginReq.getPassword()));
            List<User> users = userMapper.selectByExample(example);
            if (!users.isEmpty()) {
                return ResultHelper.success();
            } else {
                return ResultHelper.error(ExceptionEnum.USER_PWD_NOT_RIGHT);
            }
        } catch (Exception e) {
            return ResultHelper.error(ExceptionEnum.INTERNAL_ERROR);
        }
    }


}