package com.feimeng.fdroid.utils;

import com.trello.rxlifecycle3.LifecycleTransformer;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * Author: Feimeng
 * Time:   2017/3/9
 * Description: 基于RxJava的事件总线
 */
public class RxBus {
    private static volatile RxBus sRxBus;
    private final FlowableProcessor<Object> mBus; // 相当于RxJava1.x中的Subject

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized(); // 调用toSerialized()方法，保证线程安全
    }

    public static synchronized RxBus get() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) sRxBus = new RxBus();
            }
        }
        return sRxBus;
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasBinder() {
        return mBus.hasSubscribers();
    }

    /**
     * 发送事件
     *
     * @param event 事件类型
     */
    public void post(Object event) {
        new SerializedSubscriber<>(mBus).onNext(event);
    }

    /**
     * 订阅事件
     */
    public <R> void bind(final BindEvent<R> bindEvent) {
        Flowable<R> flow = mBus.ofType(bindEvent.clazz);
        if (bindEvent.lifecycle != null) {
            flow = flow.compose(bindEvent.lifecycle);
        }
        if (bindEvent.scheduler != null) {
            flow = flow.observeOn(bindEvent.scheduler);
        }
        bindEvent.disposable = flow.subscribe(bindEvent, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                if (bindEvent.onError(throwable)) {
                    bind(bindEvent); // 继续订阅事件
                }
            }
        });
    }

    /**
     * 订阅事件
     *
     * @param <T> 事件类型
     */
    public static abstract class BindEvent<T> implements Consumer<T> {
        private Class<T> clazz;
        private Scheduler scheduler;
        private LifecycleTransformer<T> lifecycle;

        private Disposable disposable;

        public BindEvent(Class<T> clazz) {
            this.clazz = clazz;
        }

        public BindEvent(Class<T> clazz, Scheduler scheduler) {
            this.clazz = clazz;
            this.scheduler = scheduler;
        }

        public BindEvent(Class<T> clazz, LifecycleTransformer<T> lifecycle) {
            this.clazz = clazz;
            this.lifecycle = lifecycle;
        }

        public BindEvent(Class<T> clazz, LifecycleTransformer<T> lifecycle, Scheduler scheduler) {
            this.clazz = clazz;
            this.scheduler = scheduler;
            this.lifecycle = lifecycle;
        }

        /**
         * 订阅
         */
        public BindEvent<T> subscribe() {
            RxBus.get().bind(this);
            return this;
        }

        /**
         * 注销
         */
        public void dispose() {
            if (!disposable.isDisposed()) disposable.dispose();
        }

        /**
         * 遇到订阅错误
         *
         * @param throwable 错误
         * @return 是否继续订阅事件
         */
        protected boolean onError(Throwable throwable) {
            return true;
        }
    }
}
