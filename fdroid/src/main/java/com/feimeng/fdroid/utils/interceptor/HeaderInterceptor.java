package com.feimeng.fdroid.utils.interceptor;

import com.feimeng.fdroid.mvp.model.api.HeaderParam;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by feimeng on 2017/2/27.
 */
public class HeaderInterceptor implements Interceptor {
    private List<HeaderParam> mHeader;

    public HeaderInterceptor(List<HeaderParam> header) {
        mHeader = header;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        for (HeaderParam headerParam : mHeader) {
            if (headerParam.isRepeat())
                builder.addHeader(headerParam.getKey(), headerParam.getValue());
            else
                builder.header(headerParam.getKey(), headerParam.getValue());
        }
        return chain.proceed(builder.build());
    }
}
