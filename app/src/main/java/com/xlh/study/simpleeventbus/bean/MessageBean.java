package com.xlh.study.simpleeventbus.bean;

/**
 * @author: Watler Xu
 * time:2020/4/27
 * description:
 * version:0.0.1
 */
public class MessageBean {

    private String title;
    private String content;

    public MessageBean() {
    }

    public MessageBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
