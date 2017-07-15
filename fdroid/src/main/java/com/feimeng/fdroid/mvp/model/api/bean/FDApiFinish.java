package com.feimeng.fdroid.mvp.model.api.bean;

/**
 * API结束回调
 * Created by feimeng on 2017/1/20.
 */
public interface FDApiFinish<T> {
    void start();

    void success(T data);

    void fail(ApiError error, String info);

    void stop();
}
