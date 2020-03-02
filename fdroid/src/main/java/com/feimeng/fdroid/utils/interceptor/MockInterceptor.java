package com.feimeng.fdroid.utils.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by feimeng on 2017/2/24.
 */
public class MockInterceptor implements Interceptor {
    private Map<String, String> mMock;

    public MockInterceptor(Map<String, String> mock) {
        mMock = mock;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        Response.Builder responseBuilder = new Response.Builder()
                .code(200)
                .message("")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .addHeader("content-type", "application/json");
        Request request = chain.request();
        // 拦截指定地址
        String api = request.url().encodedPath();
        if (mMock.containsKey(api)) {
            // 模拟数据返回Body
            // 将数据设置到Body中
            responseBuilder.body(ResponseBody.create(
                    MediaType.parse("application/json"),
                    mMock.get(api).getBytes()));
            // builder模式构建response
            response = responseBuilder.build();
        } else {
            response = chain.proceed(request);
        }
        return response;
    }
}
