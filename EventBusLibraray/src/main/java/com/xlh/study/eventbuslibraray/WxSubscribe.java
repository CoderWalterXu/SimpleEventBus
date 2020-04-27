package com.xlh.study.eventbuslibraray;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Watler Xu
 * time:2020/4/26
 * description:
 * version:0.0.1
 */


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WxSubscribe {

    WxThreadMode threadMode() default WxThreadMode.MAIN;

}
