package com.feimeng.fdroid.utils.sync;

import java.util.concurrent.CountDownLatch;

/**
 * Author: Feimeng
 * Time:   2019/9/25
 * Description: 异步转同步，解决回调地狱
 */
public final class SyncFun<T> extends CountDownLatch implements Sync<T> {
    private T mData;
    private Exception mException;

    private SyncFun() {
        super(1);
    }

    @Override
    public void sync(T data) {
        this.mData = data;
        countDown();
    }

    @Override
    public void error(Exception exception) {
        this.mException = exception;
        countDown();
    }

    /**
     * 同步返回数据
     *
     * @return 数据
     */
    private T finish() {
        try {
            await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mData;
    }

    /**
     * 一行代码实现异步转同步
     *
     * @param go  添加数据
     * @param <T> 数据
     * @return 同步返回数据
     */
    public static <T> T fast(SyncFun.Go<T> go) {
        SyncFun<T> fun = new SyncFun<>();
        go.go(fun);
        return fun.finish();
    }

    /**
     * 一行代码实现异步转同步，支持调用{@link Sync#error(Exception)}抛出异常
     *
     * @param go  添加数据
     * @param <T> 数据
     * @return 同步返回数据
     */
    public static <T> T fastErr(SyncFun.Go<T> go) throws Exception {
        SyncFun<T> fun = new SyncFun<>();
        go.go(fun);
        T data = fun.finish();
        if (fun.mException != null) throw fun.mException;
        return data;
    }

    public interface Go<T> {
        /**
         * 异步回调结束后，调用{@link Sync#sync(T)}设置数据
         *
         * @param sync {@link Sync}
         */
        void go(Sync<T> sync);
    }
}
