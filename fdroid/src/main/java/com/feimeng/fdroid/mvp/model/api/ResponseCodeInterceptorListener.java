package com.feimeng.fdroid.mvp.model.api;

/**
 * 响应码拦截器监听
 * Created by feimeng on 2017/3/15.
 */
public interface ResponseCodeInterceptorListener {
    /**
     * @param code 响应码
     * @return true 拦截 false 不拦截
     */
    boolean onResponse(int code);
}
