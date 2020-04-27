package com.xlh.study.eventbuslibraray;

import java.lang.reflect.Method;

/**
 * @author: Watler Xu
 * time:2020/4/26
 * description:
 * version:0.0.1
 */
public class WxSubscriberMethod {

    // @WxSubscriber注解的方法
    private Method method;
    // 注解方法的线程模式
    private WxThreadMode threadMode;
    // 注解方法中的参数类型
    private Class<?> type;

    public WxSubscriberMethod(Method method, WxThreadMode threadMode, Class<?> type) {
        this.method = method;
        this.threadMode = threadMode;
        this.type = type;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public WxThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(WxThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
