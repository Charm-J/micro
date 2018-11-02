package com.jeff.message.service;

import com.jeff.api.common.Result;

public interface MessageProducer {

    Result send(Object message);
}
