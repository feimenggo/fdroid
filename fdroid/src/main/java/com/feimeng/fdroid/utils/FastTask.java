package com.feimeng.fdroid.utils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 方便的执行耗时任务，基于RxJava的封装
 * Created by feimeng on 2017/6/10.
 */
public abstract class FastTask<T> {
    public Observable<T> fast() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        });
    }

    public Subscription runCalc() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation()).subscribe();
    }

    public Subscription runCalc(Action1<T> action1) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(action1);
    }

    public Subscription runCalc(Subscriber<T> subscriber) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public Subscription runIO() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public Subscription runIO(Action1<T> action1) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(action1);
    }

    public Subscription runIO(Subscriber<T> subscriber) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(task());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public abstract T task();

    public static abstract class Result<R> extends Subscriber<R> {

        @Override
        public void onStart() {
            start();
        }

        @Override
        public void onNext(R r) {
            success(r);
        }

        @Override
        public void onError(Throwable e) {
            fail(e);
            stop();
        }

        @Override
        public void onCompleted() {
            stop();
        }

        /**
         * 任务开始
         */
        public void start() {

        }

        /**
         * 任务执行成功
         */
        public abstract void success(R data);

        /**
         * 任务执行失败
         */
        public abstract void fail(Throwable throwable);

        /**
         * 任务结束
         */
        public void stop() {

        }
    }
}
