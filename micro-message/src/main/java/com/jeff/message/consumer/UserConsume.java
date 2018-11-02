package com.jeff.message.consumer;


import com.jeff.message.common.UserMessageProcess;
import com.jeff.message.constant.QueueName;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConsume {
    @Autowired
    private UserMessageProcess userMessageProcess;

    /**
     * 登录通知
     */
    @RabbitListener(queues = QueueName.LOGIN_SUCCESS_QUEUE)
    @RabbitHandler
    public void login(Message message) {
        userMessageProcess.process(message);
    }

}
