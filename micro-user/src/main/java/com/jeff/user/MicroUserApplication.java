package com.jeff.user;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubboConfiguration
@EnableTransactionManagement
// 此处要特别注意-MapperScan引入的是tk.mybatis.spring.annotation.MapperScan而不是org.mybatis.spring.annotation.MapperScan
@MapperScan(basePackages = "com.jeff.user.dao")
public class MicroUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroUserApplication.class, args);
    }
}