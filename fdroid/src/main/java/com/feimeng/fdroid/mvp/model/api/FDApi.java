package com.feimeng.fdroid.mvp.model.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.mvp.model.api.bean.ApiError;
import com.feimeng.fdroid.mvp.model.api.bean.FDApiFinish;
import com.feimeng.fdroid.mvp.model.api.bean.FDResponse;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.interceptor.HeaderInterceptor;
import com.feimeng.fdroid.utils.interceptor.MockInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.io.EOFException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.feimeng.fdroid.config.FDConfig.INFO_MALFORMED_JSON_EMPTY;
import static com.feimeng.fdroid.config.FDConfig.SHOW_HTTP_LOG;
import static com.feimeng.fdroid.mvp.model.api.FDApi.APIException.JSON_EMPTY;

/**
 * API操作类
 * Created by feimeng on 2017/1/20.
 */
public class FDApi {
    private List<HeaderParam> mHeaderParam;// 自定义请求头
    private Map<String, String> mMockData;// 模拟请求
    private ResponseCodeInterceptorListener mResponseCodeInterceptorListener;
    private int[] mResponseCodes = new int[]{};
    private static Retrofit retrofit;

    protected Retrofit getRetrofit(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient())
                    .addConverterFactory(getGsonConverterFactory())
                    .addCallAdapterFactory(getRxJavaCallAdapterFactory())
                    .build();
        }
        return retrofit;
    }

    /**
     * 添加自定义请求头
     *
     * @param headerParam 请求头
     */
    public void addHttpHeaderParam(HeaderParam headerParam) {
        if (mHeaderParam == null) {
            mHeaderParam = new ArrayList<>();
        }
        mHeaderParam.add(headerParam);
    }

    /**
     * 添加模拟数据 必须在getRetrofit()方法前调用
     *
     * @param api  拦截的API
     * @param data 返回的数据
     */
    public void addHttpMockData(String api, String data) {
        if (mMockData == null) mMockData = new HashMap<>();
        mMockData.put("/" + api, data);
    }

    protected OkHttpClient startMock(OkHttpClient.Builder clientBuilder) {
        if (mMockData != null && !mMockData.isEmpty())
            clientBuilder.addInterceptor(new MockInterceptor(mMockData));
        return clientBuilder.build();
    }

    protected RequestBody json(Object... params) {
        JsonRequestBody body = JsonRequestBody.getInstance();
        String key = null;
        for (Object param : params) {
            if (key == null) {
                key = (String) param;
            } else {
                body.put(key, param);
                key = null;
            }
        }
        return body.build();
    }

    public RequestBody json(Object requestObj) {
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(requestObj));
    }

    protected OkHttpClient getOkHttpClient() {
        // Log拦截器  打印所有的Log
        HttpLoggingInterceptor logInterceptor = null;
        if (SHOW_HTTP_LOG) {
            logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(FDConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)// 连接超时时间为15秒
                .writeTimeout(FDConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)// 写入超时时间
                .readTimeout(FDConfig.READ_TIMEOUT, TimeUnit.SECONDS);// 读取超时时间
        if (mHeaderParam != null && !mHeaderParam.isEmpty())
            clientBuilder.addInterceptor(new HeaderInterceptor(mHeaderParam));
        if (logInterceptor != null) clientBuilder.addInterceptor(logInterceptor);
        startMock(clientBuilder);
        return clientBuilder.build();
    }

    protected GsonConverterFactory getGsonConverterFactory() {
        return GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create());
    }

    protected RxJavaCallAdapterFactory getRxJavaCallAdapterFactory() {
        return RxJavaCallAdapterFactory.create();
    }

    /**
     * 自定义异常，当接口返回的{@link FDResponse#isSuccess()}为false时，需要抛出此异常
     * eg：请求参数不全、用户令牌错误等
     */
    public static class APIException extends Exception {
        static final int JSON_EMPTY = 90;
        int code;
        String message;

        public APIException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    /**
     * 设置响应结果监听
     *
     * @param responseCodeInterceptorListener 响应结果码监听器
     * @param responseCodes                   需要监听的结果码
     */
    public void setResponseCodeListener(ResponseCodeInterceptorListener responseCodeInterceptorListener, @NonNull int... responseCodes) {
        mResponseCodeInterceptorListener = responseCodeInterceptorListener;
        mResponseCodes = responseCodes;
    }

    /**
     * 响应码拦截器
     *
     * @param code 响应码
     * @return true 拦截 false 不拦截
     */
    private boolean responseCodeInterceptor(int code) {
        if (mResponseCodeInterceptorListener == null || mResponseCodes.length == 0) return false;
        for (int responseCode : mResponseCodes) {
            if (responseCode == code) {
                return mResponseCodeInterceptorListener.onResponse(code);
            }
        }
        return false;
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response 网络请求结果
     * @param <T>      实体数据
     * @return Observable
     */
    private <T> Observable<T> flatResponse(final FDResponse<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (SHOW_HTTP_LOG) L.s(response);
                if (subscriber.isUnsubscribed()) return;
                // 请求结果
                if (response.isSuccess()) {
                    subscriber.onNext(response.getData());
                    // 请求完成
                    subscriber.onCompleted();
                } else {
                    // 响应监听
                    if (responseCodeInterceptor(response.getCode())) return;
                    if (TextUtils.isEmpty(response.getInfo())) {
                        subscriber.onError(new APIException(JSON_EMPTY, INFO_MALFORMED_JSON_EMPTY));
                    } else {
                        subscriber.onError(new APIException(response.getCode(), response.getInfo()));
                    }
                }
            }
        });
    }

    /**
     * 在IO线程中执行
     */
    protected <T> Observable.Transformer<FDResponse<T>, T> applySchedulers() {
        return new Observable.Transformer<FDResponse<T>, T>() {
            @Override
            public Observable<T> call(Observable<FDResponse<T>> responseObservable) {
                return responseObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<FDResponse<T>, Observable<T>>() {
                            @Override
                            public Observable<T> call(FDResponse<T> tResponse) {
                                return flatResponse(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在调用线程中执行
     */
    protected <T> Observable.Transformer<FDResponse<T>, T> applySchedulersFixed() {
        return new Observable.Transformer<FDResponse<T>, T>() {
            @Override
            public Observable<T> call(Observable<FDResponse<T>> responseObservable) {
                return responseObservable.flatMap(new Func1<FDResponse<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(FDResponse<T> tResponse) {
                        return flatResponse(tResponse);
                    }
                });
            }
        };
    }

    public static <T> Subscriber<T> subscriber(final FDApiFinish<T> fdApiFinish) {
        return new Subscriber<T>() {
            @Override
            public void onStart() {
                if (SHOW_HTTP_LOG) L.d("请求开始 线程：" + Thread.currentThread().getName());
                fdApiFinish.start();
            }

            @Override
            public void onNext(T t) {
                fdApiFinish.success(t);
            }

            @Override
            public void onError(Throwable e) {
                if (e != null) {
                    if (e instanceof APIException) {
                        APIException exception = (APIException) e;
                        ApiError error;
                        switch (exception.code) {
                            case JSON_EMPTY:
                                error = ApiError.CLIENT;
                                break;
                            default:
                                error = ApiError.ACTION;
                        }
                        if (fdApiFinish.apiFail((APIException) e))
                            fdApiFinish.fail(error, exception.message);
                    } else if (e instanceof SocketTimeoutException) {
                        fdApiFinish.fail(ApiError.CLIENT, FDConfig.INFO_TIMEOUT_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                    } else if (e instanceof ConnectException) {
                        fdApiFinish.fail(ApiError.CLIENT, FDConfig.INFO_CONNECT_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                    } else if (e instanceof HttpException) {
                        fdApiFinish.fail(ApiError.SERVER, FDConfig.INFO_HTTP_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                    } else if (e instanceof JsonSyntaxException) {
                        fdApiFinish.fail(ApiError.CLIENT, FDConfig.INFO_JSON_SYNTAX_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                    } else if (e instanceof MalformedJsonException) {
                        fdApiFinish.fail(ApiError.CLIENT, FDConfig.INFO_MALFORMED_JSON_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                    } else if (e instanceof EOFException) {
                        fdApiFinish.fail(ApiError.CLIENT, FDConfig.INFO_EOF_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                    } else {
                        fdApiFinish.fail(ApiError.UNKNOWN, FDConfig.INFO_UNKNOWN_EXCEPTION + (FDConfig.SHOW_HTTP_EXCEPTION_INFO ? e.getMessage() : ""));
                        e.printStackTrace();
                    }
                }
                fdApiFinish.stop();
            }

            @Override
            public void onCompleted() {
                if (SHOW_HTTP_LOG) L.d("请求结束 线程：" + Thread.currentThread().getName());
                fdApiFinish.stop();
            }
        };
    }
}
