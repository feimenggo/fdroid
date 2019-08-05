package com.feimeng.fdroid.mvp.model.api;

/**
 * Author: Feimeng
 * Time:   2018/7/14 11:00
 * Description:
 */
public class WithoutNetworkException extends Exception {
    public WithoutNetworkException() {
        super("当前网络不可用，请检查网络设置。");
    }
}
