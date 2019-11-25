package com.feimeng.fdroiddemo.mvp.model.api;


import com.feimeng.fdroid.mvp.model.api.bean.Response;
import com.feimeng.fdroiddemo.data.dto.LoginDto;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API接口
 * Created by feimeng on 2018/3/17.
 */
public interface ApiService {
    @POST("user/login")
    Observable<Response<LoginDto>> login(@Body RequestBody requestBody);

    @POST("user/login")
    Call<Response<LoginDto>> login_(@Body RequestBody requestBody);

    @POST("user/register")
    Observable<Response<Void>> register(@Body RequestBody requestBody);

    @POST("user/info")
    Call<Response<Integer>> getUserInfo(@Body RequestBody requestBody);
}
