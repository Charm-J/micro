package com.jeff.controller;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableDubboConfiguration
public class MicroControllerApplication extends SpringBootServletInitializer {


    /**
     * 通过继承SpringBootServletInitializer并覆盖configure方式
     * 解决springboot打成war包，部署tomcat后访问404问题
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MicroControllerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MicroControllerApplication.class, args);
    }
}
