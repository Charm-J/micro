package com.jeff.message.service;

import com.jeff.api.common.Result;
import org.springframework.amqp.core.Message;

public interface MessageProcess<T> {

    void process(Message message);

    Result process(T message);
}
