package com.jeff.user.common;

import com.alibaba.fastjson.JSON;
import com.jeff.api.common.Result;
import com.jeff.api.model.bo.MsgTemplateModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 消息发送模块
 * @author DJ
 * @date 2018/11/9 15:30
 */
@Component
public class MqMsgHandle {

    private static final Logger logger = LoggerFactory.getLogger(MqMsgHandle.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${global.requestMqPrefix}")
    private String requestMqPrefix;

    @Async
    public void send(MsgTemplateModel model, String reqMethod) {
        if (StringUtils.isBlank(reqMethod)) {
            return;
        }
        String reqUrl = requestMqPrefix + reqMethod;
        logger.info(">>>>>>>send, request {} for data: {}", reqUrl, JSON.toJSONString(model));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(model), headers);
        Result result = restTemplate.postForObject(reqUrl, entity, Result.class);
        logger.info("<<<<<<<send, response {} for result: {}", reqUrl, JSON.toJSONString(result));
    }

}
