package com.feimeng.fdroid.mvp.model.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.config.RxJavaConfig;
import com.feimeng.fdroid.exception.ApiCallException;
import com.feimeng.fdroid.exception.ApiException;
import com.feimeng.fdroid.exception.Info;
import com.feimeng.fdroid.mvp.model.api.bean.FDApiFinish;
import com.feimeng.fdroid.mvp.model.api.bean.FDResponse;
import com.feimeng.fdroid.mvp.model.api.bean.Optional;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.interceptor.HeaderInterceptor;
import com.feimeng.fdroid.utils.interceptor.MockInterceptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.io.EOFException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.feimeng.fdroid.config.FDConfig.SHOW_HTTP_LOG;

/**
 * Author: Feimeng
 * Time:   2017/1/20
 * Description: API操作类
 */
public class FDApi {
    protected final Map<String, Disposable> mApiTags = new HashMap<>(); // 请求列表
    protected int[] mResponseCodes; // 拦截API响应码
    protected List<HeaderParam> mHeaderParam; // 自定义请求头
    protected Map<String, String> mMockData; // 模拟请求
    protected ResponseCodeInterceptorListener mResponseCodeInterceptorListener; // 拦截API响应码 拦截器
    protected Gson mGson;
    /**
     * 请求的线程池(RxJava2)
     */
    protected Executor mExecutor;
    protected Scheduler mScheduler;

    public FDApi() {
        this(new Gson());
    }

    /**
     * 使用特定的Gson进行初始化
     *
     * @param gson Gson对象
     */
    public FDApi(@NonNull Gson gson) {
        mGson = gson;
    }

    /**
     * 获取构造时传入的Gson对象
     *
     * @return gson
     */
    public Gson getGson() {
        return mGson;
    }

    /**
     * 获取当前的执行的线程池
     */
    @Nullable
    public Executor getExecutor() {
        return mExecutor;
    }

    /**
     * 设置请求执行的线程池
     *
     * @param executor 线程池
     */
    public void setExecutor(@Nullable Executor executor) {
        if (executor == null) {
            mExecutor = null;
            mScheduler = null;
        } else {
            mExecutor = executor;
            mScheduler = Schedulers.from(mExecutor);
        }
    }

    public Retrofit getRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(createOkHttpClient())
                .addConverterFactory(createConverterFactory())
                .addCallAdapterFactory(createCallAdapterFactory())
                .build();
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

    public OkHttpClient startMock(OkHttpClient.Builder clientBuilder) {
        if (mMockData != null && !mMockData.isEmpty()) {
            clientBuilder.addInterceptor(new MockInterceptor(mMockData));
        }
        return clientBuilder.build();
    }

    /**
     * 创建application/json数据体
     *
     * @param params 参数键值对 key1,value1,key2,value2...
     * @return RequestBody
     */
    public RequestBody json(Object... params) {
        return json(mGson, params);
    }

    /**
     * 创建application/json数据体
     *
     * @param gson   使用指定的Gson进行序列化
     * @param params 参数键值对 key1,value1,key2,value2...
     * @return RequestBody
     */
    public RequestBody json(Gson gson, Object... params) {
        JsonRequestBody body = JsonRequestBody.getInstance();
        Map<String, Object> map = body.getMap();
        String key = null;
        for (Object param : params) {
            if (key == null) {
                key = (String) param;
            } else {
                map.put(key, param);
                key = null;
            }
        }
        return body.build(gson);
    }

    /**
     * 创建application/json数据体
     *
     * @param requestObj 数据对象
     * @return RequestBody
     */
    public RequestBody json(Object requestObj) {
        return json(mGson, requestObj);
    }

    /**
     * 创建application/json数据体
     *
     * @param gson       使用指定的Gson进行序列化
     * @param requestObj 数据对象
     * @return RequestBody
     */
    public RequestBody json(Gson gson, Object requestObj) {
        return RequestBody.create(JsonRequestBody.getInstance().getJsonType(), gson.toJson(requestObj));
    }

    public OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(FDConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 连接超时时间为15秒
                .writeTimeout(FDConfig.WRITE_TIMEOUT, TimeUnit.SECONDS) // 写入超时时间
                .readTimeout(FDConfig.READ_TIMEOUT, TimeUnit.SECONDS); // 读取超时时间
        if (mHeaderParam != null && !mHeaderParam.isEmpty())
            clientBuilder.addInterceptor(new HeaderInterceptor(mHeaderParam));
        // Log拦截器 打印所有的Log
        if (SHOW_HTTP_LOG) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logInterceptor);
        }
        startMock(clientBuilder);
        return clientBuilder.build();
    }

    protected Converter.Factory createConverterFactory() {
        return GsonConverterFactory.create(mGson);
    }

    protected CallAdapter.Factory createCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
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
     * 响应结果拦截器，根据响应码拦截
     *
     * @param response 响应结果
     * @return true 拦截，false 不拦截，执行后续流程
     */
    private boolean responseCodeInterceptor(FDResponse<?> response) {
        if (mResponseCodeInterceptorListener == null || mResponseCodes == null || mResponseCodes.length == 0)
            return false;
        for (int responseCode : mResponseCodes) {
            if (responseCode == response.getCode()) {
                return mResponseCodeInterceptorListener.onResponse(response);
            }
        }
        return false;
    }

    /**
     * 针对网络接口返回的Response，进行分割操作
     *
     * @param response 网络请求结果
     * @param <T>      实体数据
     * @return Observable
     */
    private <T> Observable<T> flatResponse(final FDResponse<T> response) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> emitter) {
                if (emitter.isDisposed()) return;
                if (SHOW_HTTP_LOG) L.v(response);
                // 请求结果
                if (response.isSuccess()) {
                    emitter.onNext(response.getData());
                    // 请求完成
                    emitter.onComplete();
                } else {
                    // 响应监听
                    if (responseCodeInterceptor(response)) {
                        emitter.onComplete();
                        return;
                    }
                    emitter.onError(new ApiException(response.getCode(), response.getInfo(), response));
                }
            }
        });
    }

    private <T> Observable<Optional<T>> flatResponseOptional(final FDResponse<T> response) {
        return Observable.create(new ObservableOnSubscribe<Optional<T>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Optional<T>> emitter) {
                if (emitter.isDisposed()) return;
                if (SHOW_HTTP_LOG) L.v(response);
                // 请求结果
                if (response.isSuccess()) {
                    emitter.onNext(new Optional<>(response.getData()));
                    // 请求完成
                    emitter.onComplete();
                } else {
                    // 响应监听
                    if (responseCodeInterceptor(response)) {
                        emitter.onComplete();
                        return;
                    }
                    emitter.onError(new ApiException(response.getCode(), response.getInfo(), response));
                }
            }
        });
    }

    /**
     * 在子线程中执行，主线程中回调
     */
    public <T> ObservableTransformer<FDResponse<T>, T> applySchedulers() {
        return new ObservableTransformer<FDResponse<T>, T>() {
            @NonNull
            @Override
            public ObservableSource<T> apply(@NonNull Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(mExecutor != null ? mScheduler : Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply(@NonNull FDResponse<T> tResponse) {
                                return flatResponse(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在子线程中执行，主线程中回调 包装结果
     */
    public <T> ObservableTransformer<FDResponse<T>, Optional<T>> applySchedulersOptional() {
        return new ObservableTransformer<FDResponse<T>, Optional<T>>() {
            @NonNull
            @Override
            public ObservableSource<Optional<T>> apply(@NonNull Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(mExecutor != null ? mScheduler : Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<Optional<T>>>() {
                            @Override
                            public ObservableSource<Optional<T>> apply(@NonNull FDResponse<T> tResponse) {
                                return flatResponseOptional(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在子线程中执行并回调
     */
    public <T> ObservableTransformer<FDResponse<T>, T> applySchedulersNew() {
        return new ObservableTransformer<FDResponse<T>, T>() {
            @NonNull
            @Override
            public ObservableSource<T> apply(@NonNull Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(mExecutor != null ? mScheduler : Schedulers.io())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply(@NonNull FDResponse<T> tResponse) {
                                return flatResponse(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在子线程中执行并回调 包装结果
     */
    public <T> ObservableTransformer<FDResponse<T>, Optional<T>> applySchedulersNewOptional() {
        return new ObservableTransformer<FDResponse<T>, Optional<T>>() {
            @NonNull
            @Override
            public ObservableSource<Optional<T>> apply(@NonNull Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(mExecutor != null ? mScheduler : Schedulers.io())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<Optional<T>>>() {
                            @Override
                            public ObservableSource<Optional<T>> apply(@NonNull FDResponse<T> tResponse) {
                                return flatResponseOptional(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在调用线程中执行并回调
     */
    public <T> ObservableTransformer<FDResponse<T>, T> applySchedulersFixed() {
        return new ObservableTransformer<FDResponse<T>, T>() {
            @NonNull
            @Override
            public ObservableSource<T> apply(@NonNull Observable<FDResponse<T>> upstream) {
                return upstream.flatMap(new Function<FDResponse<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(@NonNull FDResponse<T> tResponse) {
                        return flatResponse(tResponse);
                    }
                });
            }
        };
    }

    /**
     * 在调用线程中执行并回调 包装结果
     */
    public <T> ObservableTransformer<FDResponse<T>, Optional<T>> applySchedulersFixedOptional() {
        return new ObservableTransformer<FDResponse<T>, Optional<T>>() {
            @NonNull
            @Override
            public ObservableSource<Optional<T>> apply(@NonNull Observable<FDResponse<T>> upstream) {
                return upstream.flatMap(new Function<FDResponse<T>, ObservableSource<Optional<T>>>() {
                    @Override
                    public ObservableSource<Optional<T>> apply(@NonNull FDResponse<T> tResponse) {
                        return flatResponseOptional(tResponse);
                    }
                });
            }
        };
    }

    /**
     * 订阅请求
     *
     * @param fdApiFinish 响应结果
     * @param <T>         响应数据
     */
    public <T> Observer<T> subscriber(final FDApiFinish<T> fdApiFinish) {
        return subscriber(null, fdApiFinish);
    }

    /**
     * 订阅请求
     *
     * @param apiTag      给本次请求添加标签，用于手动取消该请求
     * @param fdApiFinish 响应结果
     * @param <T>         响应数据
     */
    public <T> Observer<T> subscriber(final String apiTag, final FDApiFinish<T> fdApiFinish) {
        return new Observer<T>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {
                start(disposable);
            }

            @Override
            public void onNext(@NonNull T t) {
                try {
                    fdApiFinish.success(t);
                } catch (Exception e) {
                    error(e);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                error(e);
                stop();
            }

            @Override
            public void onComplete() {
                stop();
            }

            private void start(@NonNull Disposable disposable) {
                if (apiTag != null) requestApi(apiTag, disposable);
                fdApiFinish.start();
            }

            private void error(@NonNull Throwable e) {
                if (e != null) {
                    if (e instanceof CompositeException) {
                        List<Throwable> exceptions = ((CompositeException) e).getExceptions();
                        if (exceptions.size() == 2) {
                            for (Throwable throwable : exceptions) {
                                if (!throwable.getClass().getName().contains("RxCacheException")) {
                                    e = throwable;
                                    break;
                                }
                            }
                        }
                    }
                    if (e instanceof ApiException) { // 接口API异常
                        if (fdApiFinish.apiFail((ApiException) e))
                            fdApiFinish.fail(e, translateException(e));
                    } else if (e instanceof WithoutNetworkException) {
                        // 直接结束 会回调FDPresenter.OnWithoutNetwork.withoutNetwork()方法
                    } else if (e instanceof Info) {
                        fdApiFinish.info(e.getMessage());
                    } else if (e instanceof NullPointerException && e.getMessage().contains("onNext called with null")) {
                        fdApiFinish.success(null);
                    } else {
                        if (FDConfig.PRINT_HTTP_EXCEPTION) e.printStackTrace();
                        if (e instanceof HttpException) {
                            onResponseFail(((HttpException) e).response());
                        }
                        if (RxJavaConfig.interceptor == null || RxJavaConfig.interceptor.onError(e)) {
                            fdApiFinish.fail(e, translateException(e));
                        }
                    }
                }
            }

            private void stop() {
                if (apiTag != null) removeApi(apiTag);
                fdApiFinish.stop();
            }
        };
    }

    /**
     * 翻译请求异常
     *
     * @param e 异常
     * @return 翻译结果
     */
    @NonNull
    public String translateException(Throwable e) {
        if (e instanceof UnknownHostException) {
            return FDConfig.INFO_UNKNOWN_HOST_EXCEPTION;
        } else if (e instanceof SocketTimeoutException) {
            return FDConfig.INFO_TIMEOUT_EXCEPTION;
        } else if (e instanceof ConnectException) {
            return FDConfig.INFO_CONNECT_EXCEPTION;
        } else if (e instanceof HttpException) {
            return FDConfig.INFO_HTTP_EXCEPTION;
        } else if (e instanceof JsonSyntaxException) {
            return FDConfig.INFO_JSON_SYNTAX_EXCEPTION;
        } else if (e instanceof MalformedJsonException) {
            return FDConfig.INFO_MALFORMED_JSON_EXCEPTION;
        } else if (e instanceof EOFException) {
            return FDConfig.INFO_EOF_EXCEPTION;
        } else {
            return e.getClass().getSimpleName();
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public <T> T call(Call<? extends FDResponse<T>> call) throws Exception {
        try {
            retrofit2.Response<FDResponse<T>> callResponse = (Response<FDResponse<T>>) call.execute();
            if (!callResponse.isSuccessful()) {
                onResponseFail(callResponse);
                throw new ApiException(ApiException.CODE_REQUEST_UNSUCCESSFUL, "Request unsuccessful");
            }
            FDResponse<T> response = callResponse.body();
            if (response == null) {
                throw new ApiException(ApiException.CODE_RESPONSE_NULL, "Response is null");
            }
            if (response.isSuccess()) return response.getData();
            if (responseCodeInterceptor(response)) {
                throw new ApiException(ApiException.CODE_RESPONSE_INTERCEPTOR, "Response intercepted");
            }
            throw new ApiException(response.getCode(), response.getInfo());
        } catch (ApiException e) {
            throw e;
        } catch (Throwable e) {
            throw new ApiCallException(translateException(e), e);
        }
    }

    private void requestApi(String apiTag, Disposable disposable) {
        mApiTags.put(apiTag, disposable);
    }

    /**
     * 移除指定tag的请求标识
     */
    public void removeApi(String apiTag) {
        mApiTags.remove(apiTag);
    }

    /**
     * 取消所有请求
     */
    public void cancelApi() {
        if (mApiTags.isEmpty()) return;
        Set<String> apis = mApiTags.keySet();
        for (String apiTag : apis) cancelApi(apiTag);
    }

    /**
     * 取消指定tag的请求
     */
    public void cancelApi(String apiTag) {
        if (mApiTags.isEmpty()) return;
        Disposable disposable = mApiTags.get(apiTag);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            removeApi(apiTag);
        }
    }

    /**
     * 当请求返回，响应状态码为非[200-300)时回调
     *
     * @param response 响应数据
     */
    protected <T> void onResponseFail(Response<T> response) {
    }
}
