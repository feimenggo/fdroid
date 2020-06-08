package com.feimeng.fdroid.utils;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.exception.ApiException;
import com.feimeng.fdroid.exception.ApiCallException;
import com.feimeng.fdroid.exception.Info;
import com.feimeng.fdroid.mvp.FDActivity;
import com.feimeng.fdroid.mvp.FDDialog;
import com.feimeng.fdroid.mvp.FDFragment;
import com.feimeng.fdroid.mvp.FDView;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;

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
    /**
     * 快速创建Observable对象
     */
    public Observable<T> fast() {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                emitter.onNext(task());
                emitter.onComplete();
            }
        });
    }

    /**
     * 在计算线程执行
     */
    public void runCalc() {
        fast().subscribeOn(Schedulers.computation()).subscribe();
    }

    /**
     * 在计算线程执行
     */
    public Disposable runCalc(Consumer<T> consumer) {
        return fast().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    /**
     * 在计算线程执行
     */
    public void runCalc(Observer<T> observer) {
        fast().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在计算线程执行
     */
    public void runCalc(Observer<T> observer, FDView fdView) {
        Observable<T> observable = fast();
        if (fdView != null) {
            if (fdView instanceof FDActivity) {
                observable = observable.compose(((FDActivity) fdView).<T>bindUntilEvent(ActivityEvent.DESTROY));
            } else if (fdView instanceof FDFragment) {
                observable = observable.compose(((FDFragment) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            }
        }
        observable.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在计算线程执行
     */
    public void runCalc(Observer<T> observer, LifecycleTransformer<T> lifecycleTransformer) {
        fast().compose(lifecycleTransformer).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在IO线程执行
     */
    public void runIO() {
        fast().subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * 在IO线程执行
     */
    public Disposable runIO(Consumer<T> consumer) {
        return fast().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    /**
     * 在IO线程执行
     */
    public void runIO(Observer<T> observer) {
        fast().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在IO线程执行
     */
    public void runIO(Observer<T> observer, FDView fdView) {
        Observable<T> observable = fast();
        if (fdView != null) {
            if (fdView instanceof FDActivity) {
                observable = observable.compose(((FDActivity) fdView).<T>bindUntilEvent(ActivityEvent.DESTROY));
            } else if (fdView instanceof FDFragment) {
                observable = observable.compose(((FDFragment) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            } else if (fdView instanceof FDDialog) {
                observable = observable.compose(((FDDialog) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            }
        }
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在IO线程执行
     */
    public void runIO(Observer<T> observer, LifecycleTransformer<T> lifecycleTransformer) {
        fast().compose(lifecycleTransformer).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在新线程执行
     */
    public void runNew() {
        fast().subscribeOn(Schedulers.newThread()).subscribe();
    }

    /**
     * 在新线程执行
     */
    public Disposable runNew(Consumer<T> consumer) {
        return fast().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    /**
     * 在新线程执行
     */
    public void runNew(Observer<T> observer) {
        fast().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在新线程执行
     */
    public void runNew(Observer<T> observer, FDView fdView) {
        Observable<T> observable = fast();
        if (fdView != null) {
            if (fdView instanceof FDActivity) {
                observable = observable.compose(((FDActivity) fdView).<T>bindUntilEvent(ActivityEvent.DESTROY));
            } else if (fdView instanceof FDFragment) {
                observable = observable.compose(((FDFragment) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            } else if (fdView instanceof FDDialog) {
                observable = observable.compose(((FDDialog) fdView).<T>bindUntilEvent(FragmentEvent.DESTROY));
            }
        }
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    /**
     * 在新线程执行
     */
    public void runNew(Observer<T> observer, LifecycleTransformer<T> lifecycleTransformer) {
        fast().compose(lifecycleTransformer).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public abstract T task() throws Exception;

    public abstract static class Result<R> implements Observer<R> {
        private static ResultFail sFail; // 全局异常监听

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
                onFail(new NullPointerException(FDConfig.INFO_UNKNOWN));
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
            if (throwable instanceof Info) { // 提示信息
                info(throwable.getMessage());
            } else if (throwable instanceof ApiCallException) { // API调用异常
                fail(throwable);
            } else if (throwable instanceof ApiException) { // API响应异常
                // 判断是否请求被{@link ResponseCodeInterceptorListener#onResponse(FDResponse)} 拦截
                if (((ApiException) throwable).getCode() != ApiException.CODE_RESPONSE_INTERCEPTOR) {
                    fail(throwable);
                }
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
        /**
         * 异常监听
         *
         * @param throwable 异常
         * @return 是否继续调用Result.fail(Throwable throwable)方法
         */
        boolean onFail(Throwable throwable);
    }
}
