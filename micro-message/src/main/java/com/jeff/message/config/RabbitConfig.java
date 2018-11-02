package com.jeff.message.config;

import com.jeff.message.common.MQAccessBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
    private int port;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${rabbitmq.publisher-confirms}")
    private Boolean publisherConfirms;
    @Value("${rabbitmq.virtual-host}")
    private String virtualHost;
    @Value("${rabbitmq.channel-cache-size}")
    private int channelCacheSize;

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms); // 开启消息确认
        connectionFactory.setChannelCacheSize(channelCacheSize); // 信道缓存大小
        return connectionFactory;
    }

    @Bean
    MQAccessBuilder newMQAccessBuilder(ConnectionFactory connectionFactory){
        MQAccessBuilder mqAccessBuilder = new MQAccessBuilder(connectionFactory);
        mqAccessBuilder.buildQueues();
        return mqAccessBuilder;
    }

}