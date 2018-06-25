package com.feimeng.fdroid.utils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 方便的执行耗时任务，基于RxJava的封装
 * Created by feimeng on 2017/6/10.
 */
public abstract class FastTask<T> {
    public Observable<T> fast() {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        });
    }

    public Disposable runCalc() {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).subscribe();
    }

    public Disposable runCalc(Consumer<T> consumer) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    public void runCalc(Observer<T> observer) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public Disposable runIO() {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public Disposable runIO(Consumer<T> consumer) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    public void runIO(Observer<T> observer) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public abstract T task();

    public abstract static class Result<R> implements Observer<R> {
        @Override
        public void onSubscribe(Disposable d) {
            start();
        }

        @Override
        public void onNext(R r) {
            success(r);
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof NullPointerException && e.getMessage().contains("onNext called with null")) {
                success(null);
            } else {
                fail(e);
            }
            stop();
        }

        @Override
        public void onComplete() {
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
        public void fail(Throwable throwable) {
        }

        /**
         * 任务结束
         */
        public void stop() {

        }
    }
}
