package com.feimeng.fdroiddemo.api;

import com.feimeng.fdroid.mvp.model.api.FDApi;
import com.feimeng.fdroid.mvp.model.api.bean.Optional;

import io.reactivex.Observable;

/**
 * Api类的包装
 */
public class ApiWrapper extends FDApi {
    private static final ApiWrapper instance = new ApiWrapper();// 单例模式
    private ApiService api;

    private ApiWrapper() {
//        addHttpMockData("user/login", "{\"code\":200,\"info\":\"成功\",\"data\":1}");
        addHttpMockData("user/register", "{\"code\":200,\"info\":\"成功\",\"data\":null}");
        addHttpMockData("user/info", "{\"code\":210,\"info\":\"失败\",\"data\":null}");
        api = getRetrofit("http://www.baidu.com/").create(ApiService.class);
    }

    public static ApiWrapper getInstance() {
        return instance;
    }

    public Observable<Integer> login(String phone, String password) {
        return api.login(json("phone", phone, "password", password)).compose(this.<Integer>applySchedulers());
    }

    public Observable<Optional<Void>> register(String phone, String password) {
        return api.register(json("phone", phone, "password", password)).compose(this.<Void>applySchedulersNewOptional());
    }

    public Integer getUserInfo(String phone, String password) throws Exception {
        return call(api.getUserInfo(json("phone", phone, "password", password)));
    }
}
