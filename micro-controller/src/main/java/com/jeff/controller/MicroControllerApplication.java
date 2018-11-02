package com.jeff.controller;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
public class MicroControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroControllerApplication.class, args);
    }
}
