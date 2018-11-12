package com.jeff.message.common;

import com.alibaba.fastjson.JSON;
import com.jeff.api.common.Result;
import com.jeff.api.model.bo.MsgTemplateModel;
import com.jeff.message.service.MessageProcess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class UserMessageProcess implements MessageProcess<MsgTemplateModel> {

    private static final Logger log = LoggerFactory.getLogger(UserMessageProcess.class);

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public void process(Message message) {
        try {
            MsgTemplateModel msgTemplateModel = JSON.parseObject(new String(message.getBody(), "UTF-8"), MsgTemplateModel.class);
            if (null == msgTemplateModel) {
                log.info("======msgTemplateModel: null");
                return;
            }
            process(msgTemplateModel);
        } catch (Exception e) {
            log.error("!!!!!UserMessageProcess process err: ", e);
        }
    }

    @Override
    public Result process(MsgTemplateModel message) {
        Boolean success = sendMail(message);
        if (success) {
            return new Result("consume message success!");
        } else {
            return new Result("consume message fail!");
        }

    }


    /**
     * 发邮件
     */
    private Boolean sendMail(MsgTemplateModel message) {
        Boolean success = false;
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            if (StringUtils.isNotBlank(message.getFrom())) {
                emailFrom = message.getFrom();
            }
            simpleMailMessage.setFrom(emailFrom);
            simpleMailMessage.setTo(message.getTo());
            simpleMailMessage.setSubject(message.getSubject());
            simpleMailMessage.setText(message.getContent());
            javaMailSender.send(simpleMailMessage);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("consume message >> send email result = " + (success ? "ok" : "error"));
        return success;
    }


}
