package com.feimeng.fdroiddemo.api;

import com.feimeng.fdroid.mvp.model.api.FDApi;

import rx.Observable;

/**
 * Api类的包装
 */
public class ApiWrapper extends FDApi {
    private static final ApiWrapper instance = new ApiWrapper();// 单例模式
    private ApiService api;

    private ApiWrapper() {
        addHttpMockData("user/login", "{\"code\":200,\"info\":\"成功\",\"data\":\"Hello Feimeng\"}");
        api = getRetrofit("http://www.baidu.com/").create(ApiService.class);
    }

    public static ApiWrapper getInstance() {
        return instance;
    }

    public Observable<String> login(String phone, String password) {
        return api.login(json("phone", phone, "password", password)).compose(this.<String>applySchedulers());
    }
}
