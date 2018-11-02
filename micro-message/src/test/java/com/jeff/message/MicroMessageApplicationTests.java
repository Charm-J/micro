package com.jeff.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MicroMessageApplicationTests {

    @Autowired
    private JavaMailSender javaMailSender;
    @Test
    public void contextLoads() {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo("1843950742@qq.com");
        simpleMailMessage.setSubject("来自项目Mirco的信息");
        simpleMailMessage.setText("您刚刚成功登录了Mirco！");
        javaMailSender.send(simpleMailMessage);


    }

}
