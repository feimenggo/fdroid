package com.feimeng.fdroid.mvp;

/**
 * 视图基类
 * Created by feimeng on 2017/1/20.
 */
public interface FDView<D> {
    /**
     * 控制器初始化完成后回调
     *
     * @param initData  初始化的结果
     * @param throwable 初始化执行时抛出的异常
     */
    void init(D initData, Throwable throwable);
}
