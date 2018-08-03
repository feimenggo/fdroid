package com.feimeng.fdroid.utils;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * 基于RxJava的事件总线
 * Created by feimeng on 2017/3/9.
 */
public class RxBus {
    private final FlowableProcessor<Object> mBus; // 相当于RxJava1.x中的Subject
    private static volatile RxBus sRxBus;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized(); // 调用toSerialized()方法，保证线程安全
    }

    public static synchronized RxBus getDefault() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus();
                }
            }
        }
        return sRxBus;
    }

    /**
     * 发送消息 * @param o
     */
    public void post(Object o) {
        new SerializedSubscriber<>(mBus).onNext(o);
    }

    /**
     * 确定接收消息的类型
     */
    public <R> Flowable<R> get(Class<R> aClass) {
        return mBus.ofType(aClass);
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    public static void disposable(Disposable disposable) {
        // 预留
    }
}
