package com.feimeng.fdroid.mvp;

/**
 * Author: Feimeng
 * Time:   2017/1/20
 * Description: 视图基类
 */
public interface FDView<D> {
    /**
     * 界面初始化完成后回调，可以在此执行数据加载操作
     *
     * @param initData  控制器初始化的结果
     * @param throwable 控制器初始化的异常
     */
    void init(D initData, Throwable throwable);
}
