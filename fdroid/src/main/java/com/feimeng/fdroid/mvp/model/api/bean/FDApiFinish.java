package com.feimeng.fdroid.mvp.model.api.bean;

import com.feimeng.fdroid.mvp.model.api.FDApi;

/**
 * API结束回调
 * Created by feimeng on 2017/1/20.
 */
public interface FDApiFinish<T> {
    void start();

    void success(T data);

    /**
     * 接口返回异常
     *
     * @param exception 异常对象
     * @return 是否继续调用fail(ApiError error, String info)
     */
    boolean apiFail(FDApi.APIException exception);

    void fail(Throwable error, String info);

    void stop();
}
