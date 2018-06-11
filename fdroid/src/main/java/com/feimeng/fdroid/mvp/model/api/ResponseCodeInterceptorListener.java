package com.feimeng.fdroid.mvp.model.api;

import com.feimeng.fdroid.mvp.model.api.bean.FDResponse;

/**
 * 响应码拦截器监听
 * Created by feimeng on 2017/3/15.
 */
public interface ResponseCodeInterceptorListener {
    /**
     * @param response 响应结果
     * @return true 拦截 false 不拦截
     */
    boolean onResponse(FDResponse response);
}
