package com.xlh.study.eventbuslibraray;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Watler Xu
 * time:2020/4/26
 * description:
 * version:0.0.1
 */
public class WxEventBus {

    /**
     * 缓存Map
     * key是obj(注册类，含有@WxSubscribe注解的方法的类)
     * value是这些注解方法封装类的list集合
     */
    private Map<Object, List<WxSubscriberMethod>> cacheMap;

    private Handler mHandler;

    private static volatile WxEventBus instance;

    private ExecutorService mExecutorService;

    private WxEventBus() {
        this.cacheMap = new HashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static WxEventBus getDefault() {
        if (instance == null) {
            synchronized (WxEventBus.class) {
                if (instance == null) {
                    instance = new WxEventBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册
     * 寻找subscriber中所有带有@WxSubscribe注解的方法，根据“消息类型”来接收事件
     *
     * @param subscriber 注册类，Object类型是因为需要满足既可以传Actiivty,也可以传Fragment
     */
    public void register(Object subscriber) {
        // 先去缓存Map中查找是否有，有就代表已经注册，不需要再注册
        List<WxSubscriberMethod> list = cacheMap.get(subscriber);

        if (list == null) {
            // 如果缓存Map中没有，则去查找
            list = findWxSubscribeMethods(subscriber);
            // 查找完后，存入缓存Map
            cacheMap.put(subscriber, list);
        }
    }

    /**
     * 遍历注册类及其所有父类（不包含系统类），找到能接受消息的方法
     *
     * @param subscriber
     * @return
     */
    private List<WxSubscriberMethod> findWxSubscribeMethods(Object subscriber) {
        // 初始化注解方法的封装类的list集合
        List<WxSubscriberMethod> list = new ArrayList<>();
        // 获得subscriber实例的类型类，通过clazz获得该类的类名、方法、父类等等消息
        Class<?> clazz = subscriber.getClass();
        // 还要考虑其父类是否也有WxSubscribe注解的方法，while循环其父类查找
        while (clazz != null) {
            // 系统类肯定没有WxSubscribe注解的方法，直接忽略
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.") || name.startsWith("androidx.")) {
                break;
            }

            // 通过getDeclaredMethods找该类的所有方法，返回一个Method数组
            Method[] methods = clazz.getDeclaredMethods();
            // for循环判断每个方式是否被@WxSubscribe注解
            // 如果有，则将被WxThreadMode注解的方法、线程模式、方法中的唯一参数等封装，并添加到list集合
            for (Method method : methods) {
                WxSubscribe mWxSubscribe = method.getAnnotation(WxSubscribe.class);
                // 注解值为null,则跳出本次循环，继续下一个循环
                if (mWxSubscribe == null) {
                    continue;
                }
                // 得到方法的参数,getParameterTypes
                Class<?>[] types = method.getParameterTypes();
                // 判断方法中的参数是否唯一
                if (types.length != 1) {
                    // 如果方法中的参数不唯一，则抛出异常
                    throw new RuntimeException("WxEventBus只能接收一个参数");
                }
                // 获取WxThreadMode注解上的threadMode值
                WxThreadMode threadMode = mWxSubscribe.threadMode();
                // 将被WxThreadMode注解的方法、线程模式、方法中的唯一参数等封装
                WxSubscriberMethod wxSubscriberMethod = new WxSubscriberMethod(method, threadMode, types[0]);
                // 将注解方法的封装类添加去list集合
                list.add(wxSubscriberMethod);
            }
            // 找到其父类
            clazz = clazz.getSuperclass();

        }
        return list;
    }

    /**
     * 取消注册
     *
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        List<WxSubscriberMethod> list = cacheMap.get(subscriber);
        if (list != null) {
            cacheMap.remove(subscriber);
        }
    }

    /**
     * 发送
     *
     * @param obj
     */
    public void post(final Object obj) {
        // 直接循环cacheMap里的方法，找到对应
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            // 拿到注册类
            final Object objNext = iterator.next();
            // 获取类中所有添加@WxSubscribe注解的方法
            List<WxSubscriberMethod> list = cacheMap.get(objNext);
            for (final WxSubscriberMethod wxSubscriberMethod : list) {
                // 判断这个方法是否应该接受事件
                // 通过isAssignableFrom()判断wxSubscriberMethod.getType是否是obj.getClass()的父类或父接口
                if (wxSubscriberMethod.getType().isAssignableFrom(obj.getClass())) {
                    // 判断线程模式
                    switch (wxSubscriberMethod.getThreadMode()) {
                        case MAIN:
                            // 接收消息在主线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                // 消息从主线程发送到主线程
                                invoke(wxSubscriberMethod, objNext, obj);
                            } else {
                                // 消息从子线程发送到主线程
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(wxSubscriberMethod, objNext, obj);
                                    }
                                });
                            }
                            break;
                        case BACKGROUND:
                            break;
                        case MAIN_ORDERED:
                            break;
                        case ASYNC:
                            // 接收消息在子线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                // 消息从主线程发送到子线程
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(wxSubscriberMethod, objNext, obj);
                                    }
                                });
                            } else {
                                // 消息从子线程发送到子线程
                                invoke(wxSubscriberMethod, objNext, obj);
                            }
                            break;
                        default:
                            break;

                    }
                }

            }
        }
    }

    private void invoke(WxSubscriberMethod wxSubscriberMethod, Object objNext, Object obj) {
        Method method = wxSubscriberMethod.getMethod();
        try {
            method.invoke(objNext,obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
