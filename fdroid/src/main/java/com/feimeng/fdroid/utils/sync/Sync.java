package com.feimeng.fdroid.utils.sync;

/**
 * Author: Feimeng
 * Time:   2019/9/26
 * Description: 同步操作
 */
public interface Sync<T> {
    /**
     * 异步操作结束，设置数据
     *
     * @param data 数据
     */
    void sync(T data);

    /**
     * 异步操作期间产生异常
     *
     * @param exception 异常
     */
    void error(Exception exception);
}
