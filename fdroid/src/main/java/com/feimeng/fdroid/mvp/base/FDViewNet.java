package com.feimeng.fdroid.mvp.base;

/**
 * 视图基类
 * Created by feimeng on 2017/1/20.
 */
public interface FDViewNet extends FDView {
    /**
     * 没有网络连接
     */
    void withoutNetwork();
}
