package com.feimeng.fdroid.mvp.model.api.bean;

/**
 * 接收成功、失败结果
 * Created by feimeng on 2017/1/20.
 */
public abstract class ApiFinish2<T> implements FDApiFinish<T> {
    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
