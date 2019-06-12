package com.feimeng.fdroid.mvp;

import androidx.annotation.Nullable;

/**
 * 视图基类
 * Created by feimeng on 2017/1/20.
 */
public interface FDView {
    /**
     * 控制器初始化完成后回调
     *
     * @param initData 初始化的结果
     * @param e        初始化执行时抛出的异常
     */
    void init(@Nullable Object initData, @Nullable Throwable e);
}
