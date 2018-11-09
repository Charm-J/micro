package com.jeff.api.model.bo;

/**
 * @author DJ
 * @date 2018/11/1 15:39
 */
public class MsgTemplateModel {

    private String from;
    private String to;
    private String subject;
    private String content;

    public MsgTemplateModel() {
    }

    public MsgTemplateModel(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
