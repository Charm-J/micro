package com.jeff.message.common;

import com.alibaba.fastjson.JSON;
import com.jeff.api.common.Result;
import com.jeff.api.exception.ExceptionEnum;
import com.jeff.message.constant.QueueName;
import com.jeff.message.service.MessageProducer;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQAccessBuilder {
    private final Logger log = LoggerFactory.getLogger(MQAccessBuilder.class);

    private static final String DIRECT = "direct";
    private static final String TOPIC = "topic";

    private ConnectionFactory connectionFactory;

    public MQAccessBuilder(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public MessageProducer buildDirectMessageProducer(final String exchange, final String routingKey, final String queue) throws IOException {
        return buildMessageProducer(exchange, routingKey, queue, DIRECT);
    }

    public MessageProducer buildMessageProducer(final String exchange, final String routingKey,
                                                final String queue, final String type) throws IOException {
        Connection connection = connectionFactory.createConnection();
        //1 构造template, exchange, routingkey等
        if (type.equals(DIRECT)) {
            buildQueue(exchange, routingKey, queue, connection, DIRECT);
        } else if (type.equals(TOPIC)) {
            buildTopic(exchange, connection);
        }
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey(routingKey);
        //2 设置message序列化方法
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        //4 构造sender方法
        return message -> {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, message);
            } catch (Exception e) {
                log.error("MessageProducer >> send message failed: ", e);
                return new Result(ExceptionEnum.MQ_PRODUCT_ERROR.getCode(), ExceptionEnum.MQ_PRODUCT_ERROR.getMsg());
            }
            log.info("MessageProducer >> send message success: {}", JSON.toJSONString(message));
            return new Result("send message success!");
        };
    }

    private void buildQueue(String exchange, String routingKey,
                            final String queue, Connection connection, String type) throws IOException {
        Channel channel = connection.createChannel(false);
        try {
            if (type.equals(DIRECT)) {
                // b:durable--持久化 b1:autoDelete--没有消费者时，服务器是否可以删除该Exchange
                channel.exchangeDeclare(exchange, DIRECT, true, false, null);
            } else if (type.equals(TOPIC)) {
                channel.exchangeDeclare(exchange, TOPIC, true, false, null);
            }
            // b:durable--持久化 b1--是否排外 b2:autoDelete--是否自动删除
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);
        } catch (Exception e) {
            log.error("buildQueue failed ", e);
        } finally {
            try {
                channel.close();
            } catch (TimeoutException e) {
                log.error("channel close timeout ", e);
            }
        }
    }

    public void buildQueues() {
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(false);
        try {
            // b:durable--持久化 b1--是否排外 b2:autoDelete--是否自动删除
            channel.queueDeclare(QueueName.LOGIN_SUCCESS_QUEUE, true, false, false, null);
        } catch (Exception e) {
            log.error("buildQueues err: ", e);
        } finally {
            try {
                channel.close();
            } catch (Exception e) {
            }
        }
    }

    private void buildTopic(String exchange, Connection connection) throws IOException {
        Channel channel = connection.createChannel(false);
        channel.exchangeDeclare(exchange, TOPIC, true, false, null);
    }
}
