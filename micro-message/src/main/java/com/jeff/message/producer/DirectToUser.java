package com.jeff.message.producer;

import com.jeff.api.common.Result;
import com.jeff.api.exception.ExceptionEnum;
import com.jeff.api.model.bo.MsgTemplateModel;
import com.jeff.api.utils.JsonUtil;
import com.jeff.message.common.MQAccessBuilder;
import com.jeff.message.constant.QueueName;
import com.jeff.message.constant.RouterKey;
import com.jeff.message.service.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 用户通知
 */
@RestController
@RequestMapping("/toUser")
public class DirectToUser {

    @Autowired
    private MQAccessBuilder mqAccessBuilder;

    private static final String EX_USER = "EX_USER";

    /**
     * 登录通知
     */
    @PostMapping("/login")
    public Result login(@RequestBody MsgTemplateModel content) {
        Result result = new Result(ExceptionEnum.DETAIL_MESSAGE_PUSH_ERR.getCode(), ExceptionEnum.DETAIL_MESSAGE_PUSH_ERR.getMsg());
        return commonWay(content, RouterKey.LOGIN_SUCCESS_QUEUE.name(), QueueName.LOGIN_SUCCESS_QUEUE, result);
    }

    /**
     * 向mq服务端发送消息公共方法
     *
     * @param msgTemplateModel
     * @param routerKey
     * @param queue
     * @param Result
     * @return
     */
    private Result commonWay(MsgTemplateModel msgTemplateModel, String routerKey, String queue, Result Result) {
        String content = JsonUtil.serialize(msgTemplateModel);
        MessageProducer messageProducer;
        try {
            //向mq服务端发送消息，exchange为EX_USER，routingkey为LOGIN_SUCCESS_QUEUE
            messageProducer = mqAccessBuilder.buildDirectMessageProducer(EX_USER, routerKey, queue);
        } catch (IOException e) {
            e.printStackTrace();
            return Result;
        }
        Result = messageProducer.send(msgTemplateModel);
        return Result;
    }
}
