package com.feimeng.fdroid.mvp.model.api.bean;

import com.feimeng.fdroid.exception.ApiException;

/**
 * API结束回调
 * Created by feimeng on 2017/3/9.
 */
public abstract class ApiFinish<T> implements FDApiFinish<T> {
    public boolean apiFail(ApiException exception) {
        // 返回true，将会调用 fail(ApiError error, String info);
        return true;
    }

    @Override
    public void info(String message) {
    }
}
