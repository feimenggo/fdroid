package com.feimeng.fdroid.mvp.model.api.bean;

/**
 * 接收成功结果
 * Created by feimeng on 2017/1/20.
 */
public abstract class ApiFinish1<T> extends ApiFinish2<T> {
    @Override
    public void fail(Throwable error, String info) {

    }
}
