package com.feimeng.fdroid.utils;

import com.feimeng.fdroid.base.FDActivity;
import com.feimeng.fdroid.base.FDFragment;
import com.feimeng.fdroid.exception.Info;
import com.feimeng.fdroid.mvp.base.FDView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Feimeng
 * Time:   2017/6/10
 * Description: 方便的执行耗时任务，基于RxJava的封装
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

    public void runCalc() {
        Observable.create(new ObservableOnSubscribe<T>() {
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

    public void runCalc(Observer<T> observer, FDView fdView) {
        Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        });
        if (fdView != null) {
            if (fdView instanceof FDActivity) {
                observable = observable.compose(((FDActivity) fdView).<T>bindUntilEvent(ActivityEvent.DESTROY));
            } else if (fdView instanceof FDFragment) {
                observable = observable.compose(((FDFragment) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            }
        }
        observable.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void runCalc(Observer<T> observer, LifecycleTransformer<T> lifecycleTransformer) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).compose(lifecycleTransformer).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void runIO() {
        Observable.create(new ObservableOnSubscribe<T>() {
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

    public void runIO(Observer<T> observer, FDView fdView) {
        Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        });
        if (fdView != null) {
            if (fdView instanceof FDActivity) {
                observable = observable.compose(((FDActivity) fdView).<T>bindUntilEvent(ActivityEvent.DESTROY));
            } else if (fdView instanceof FDFragment) {
                observable = observable.compose(((FDFragment) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            }
        }
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void runIO(Observer<T> observer, LifecycleTransformer<T> lifecycleTransformer) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        }).compose(lifecycleTransformer).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public abstract T task() throws Exception;

    public abstract static class Result<R> implements Observer<R> {
        private static ResultFail sFail;

        public static void onFail(ResultFail fail) {
            sFail = fail;
        }

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
            if (e == null || e.getMessage() == null) {
                onFail(new NullPointerException("哎呀！出错了。"));
            } else if (e instanceof NullPointerException && e.getMessage().contains("onNext called with null")) {
                success(null);
            } else {
                onFail(e);
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
        public abstract void success(R truck);

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

        public void info(String message) {
        }

        private void onFail(Throwable throwable) {
            if (throwable instanceof Info) {
                info(throwable.getMessage());
            } else {
                if (sFail != null && !sFail.onFail(throwable)) return;
                fail(throwable);
            }
        }
    }

    public static class Truck<T, X> {
        private T data;
        private X dataExt;

        private Truck(T data, X dataExt) {
            this.data = data;
            this.dataExt = dataExt;
        }

        public static <T, X> Truck<T, X> success(T data, X dataExt) {
            return new Truck<>(data, dataExt);
        }

        public T getData() {
            return data;
        }

        public X getDataExt() {
            return dataExt;
        }
    }

    public static Exception error(String error) throws Exception {
        throw new Exception(error);
    }

    public static Info info(String info) throws Info {
        throw new Info(info);
    }

    public interface ResultFail {
        boolean onFail(Throwable throwable);
    }
}
