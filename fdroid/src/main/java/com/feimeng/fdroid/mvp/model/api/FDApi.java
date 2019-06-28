package com.feimeng.fdroid.mvp.model.api;

import android.support.annotation.NonNull;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.exception.ApiException;
import com.feimeng.fdroid.exception.Info;
import com.feimeng.fdroid.mvp.model.api.bean.FDApiFinish;
import com.feimeng.fdroid.mvp.model.api.bean.FDResponse;
import com.feimeng.fdroid.mvp.model.api.bean.Optional;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
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
 * API操作类
 * Created by feimeng on 2017/1/20.
 */
public class FDApi {
    private static final Map<String, Disposable> mApiTags = new HashMap<>(); // 请求列表
    private List<HeaderParam> mHeaderParam; // 自定义请求头
    private Map<String, String> mMockData; // 模拟请求
    private ResponseCodeInterceptorListener mResponseCodeInterceptorListener;
    private int[] mResponseCodes = new int[]{};
    private Retrofit retrofit;
    private Gson mGson;

    public FDApi() {
        this(new Gson());
    }

    public FDApi(Gson gson) {
        mGson = gson;
    }

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
        if (mMockData != null && !mMockData.isEmpty()) {
            clientBuilder.addInterceptor(new MockInterceptor(mMockData));
        }
        return clientBuilder.build();
    }

    public RequestBody json(Object... params) {
        return json(mGson, params);
    }

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

    public RequestBody json(Object requestObj) {
        return json(mGson, requestObj);
    }

    public RequestBody json(Gson gson, Object requestObj) {
        return RequestBody.create(JsonRequestBody.getInstance().getJsonType(), gson.toJson(requestObj));
    }

    protected OkHttpClient getOkHttpClient() {
        // Log拦截器  打印所有的Log
        HttpLoggingInterceptor logInterceptor = null;
        if (SHOW_HTTP_LOG) {
            logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(FDConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 连接超时时间为15秒
                .writeTimeout(FDConfig.WRITE_TIMEOUT, TimeUnit.SECONDS) // 写入超时时间
                .readTimeout(FDConfig.READ_TIMEOUT, TimeUnit.SECONDS); // 读取超时时间
        if (mHeaderParam != null && !mHeaderParam.isEmpty())
            clientBuilder.addInterceptor(new HeaderInterceptor(mHeaderParam));
        if (logInterceptor != null) clientBuilder.addInterceptor(logInterceptor);
        startMock(clientBuilder);
        return clientBuilder.build();
    }

    protected Converter.Factory getGsonConverterFactory() {
        return GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create());
    }

    protected CallAdapter.Factory getRxJavaCallAdapterFactory() {
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
     * @return true 拦截 false 不拦截，执行往后流程
     */
    private boolean responseCodeInterceptor(FDResponse response) {
        if (mResponseCodeInterceptorListener == null || mResponseCodes.length == 0) return false;
        for (int responseCode : mResponseCodes) {
            if (responseCode == response.getCode()) {
                return mResponseCodeInterceptorListener.onResponse(response);
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
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                if (SHOW_HTTP_LOG) L.s(response);
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
                    emitter.onError(new ApiException(response.getCode(), response.getInfo()));
                }
            }
        });
    }

    private <T> Observable<Optional<T>> flatResponseOptional(final FDResponse<T> response) {
        return Observable.create(new ObservableOnSubscribe<Optional<T>>() {
            @Override
            public void subscribe(ObservableEmitter<Optional<T>> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                if (SHOW_HTTP_LOG) L.s(response);
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
                    emitter.onError(new ApiException(response.getCode(), response.getInfo()));
                }
            }
        });
    }

    /**
     * 在子线程中执行，主线程中回调
     */
    protected <T> ObservableTransformer<FDResponse<T>, T> applySchedulers() {
        return new ObservableTransformer<FDResponse<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply(FDResponse<T> tResponse) throws Exception {
                                return flatResponse(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在子线程中执行，主线程中回调 包装结果
     */
    protected <T> ObservableTransformer<FDResponse<T>, Optional<T>> applySchedulersOptional() {
        return new ObservableTransformer<FDResponse<T>, Optional<T>>() {
            @Override
            public ObservableSource<Optional<T>> apply(Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<Optional<T>>>() {
                            @Override
                            public ObservableSource<Optional<T>> apply(FDResponse<T> tResponse) throws Exception {
                                return flatResponseOptional(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在子线程中执行并回调
     */
    protected <T> ObservableTransformer<FDResponse<T>, T> applySchedulersNew() {
        return new ObservableTransformer<FDResponse<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply(FDResponse<T> tResponse) throws Exception {
                                return flatResponse(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在子线程中执行并回调 包装结果
     */
    protected <T> ObservableTransformer<FDResponse<T>, Optional<T>> applySchedulersNewOptional() {
        return new ObservableTransformer<FDResponse<T>, Optional<T>>() {
            @Override
            public ObservableSource<Optional<T>> apply(Observable<FDResponse<T>> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .flatMap(new Function<FDResponse<T>, ObservableSource<Optional<T>>>() {
                            @Override
                            public ObservableSource<Optional<T>> apply(FDResponse<T> tResponse) throws Exception {
                                return flatResponseOptional(tResponse);
                            }
                        });
            }
        };
    }

    /**
     * 在调用线程中执行并回调
     */
    protected <T> ObservableTransformer<FDResponse<T>, T> applySchedulersFixed() {
        return new ObservableTransformer<FDResponse<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<FDResponse<T>> upstream) {
                return upstream.flatMap(new Function<FDResponse<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(FDResponse<T> tResponse) throws Exception {
                        return flatResponse(tResponse);
                    }
                });
            }
        };
    }

    /**
     * 在调用线程中执行并回调 包装结果
     */
    protected <T> ObservableTransformer<FDResponse<T>, Optional<T>> applySchedulersFixedOptional() {
        return new ObservableTransformer<FDResponse<T>, Optional<T>>() {
            @Override
            public ObservableSource<Optional<T>> apply(Observable<FDResponse<T>> upstream) {
                return upstream.flatMap(new Function<FDResponse<T>, ObservableSource<Optional<T>>>() {
                    @Override
                    public ObservableSource<Optional<T>> apply(FDResponse<T> tResponse) throws Exception {
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
    public static <T> Observer<T> subscriber(final FDApiFinish<T> fdApiFinish) {
        return subscriber(null, fdApiFinish);
    }

    /**
     * 订阅请求
     *
     * @param apiTag      给本次请求添加标签，用于手动取消该请求
     * @param fdApiFinish 响应结果
     * @param <T>         响应数据
     */
    public static <T> Observer<T> subscriber(final String apiTag, final FDApiFinish<T> fdApiFinish) {
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                if (SHOW_HTTP_LOG) L.d("请求开始 线程：" + Thread.currentThread().getName());
                if (apiTag != null) requestApi(apiTag, disposable);
                fdApiFinish.start();
            }

            @Override
            public void onNext(T t) {
                fdApiFinish.success(t);
            }

            @Override
            public void onError(Throwable e) {
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
                    if (e instanceof ApiException) {
                        if (fdApiFinish.apiFail((ApiException) e))
                            fdApiFinish.fail(e, e.getMessage());
                    } else if (e instanceof WithoutNetworkException) {
                        // 可以结束请求，因为会回调FDPresenter.OnWithoutNetwork.withoutNetwork()方法
                    } else if (e instanceof Info) {
                        fdApiFinish.info(e.getMessage());
                    } else if (e instanceof NullPointerException && e.getMessage().contains("onNext called with null")) {
                        fdApiFinish.success(null);
                    } else {
                        String error;
                        if (e instanceof SocketTimeoutException) {
                            error = FDConfig.INFO_TIMEOUT_EXCEPTION;
                        } else if (e instanceof ConnectException) {
                            error = FDConfig.INFO_CONNECT_EXCEPTION;
                        } else if (e instanceof HttpException) {
                            error = FDConfig.INFO_HTTP_EXCEPTION;
                        } else if (e instanceof JsonSyntaxException) {
                            error = FDConfig.INFO_JSON_SYNTAX_EXCEPTION;
                        } else if (e instanceof MalformedJsonException) {
                            error = FDConfig.INFO_MALFORMED_JSON_EXCEPTION;
                        } else if (e instanceof EOFException) {
                            error = FDConfig.INFO_EOF_EXCEPTION;
                        } else {
                            error = FDConfig.INFO_UNKNOWN_EXCEPTION;
                        }
                        if (FDConfig.SHOW_HTTP_EXCEPTION_INFO) {
                            error += " " + e.getMessage();
                            e.printStackTrace();
                        }
                        fdApiFinish.fail(e, error);
                    }
                }
                removeApi(apiTag);
                fdApiFinish.stop();
            }

            @Override
            public void onComplete() {
                if (SHOW_HTTP_LOG) L.d("请求结束 线程：" + Thread.currentThread().getName());
                removeApi(apiTag);
                fdApiFinish.stop();
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <T> T call(Call<? extends FDResponse<T>> call) throws Exception {
        retrofit2.Response<FDResponse<T>> callResponse = (Response<FDResponse<T>>) call.execute();
        if (!callResponse.isSuccessful()) {
            throw new ApiException(ApiException.CODE_REQUEST_UNSUCCESSFUL, "Request unsuccessful");
        }
        FDResponse<T> response = callResponse.body();
        if (response == null) {
            throw new ApiException(ApiException.CODE_CONTENT_NULL, "Content is null");
        }
        if (response.isSuccess()) return response.getData();
        if (responseCodeInterceptor(response)) {
            throw new ApiException(ApiException.CODE_RESPONSE_INTERCEPTOR, "Response interceptor");
        }
        throw new ApiException(response.getCode(), response.getInfo());
    }

    public static void requestApi(String apiTag, Disposable disposable) {
        mApiTags.put(apiTag, disposable);
    }

    public static void cancelApi() {
        if (mApiTags.isEmpty()) return;
        Set<String> apis = mApiTags.keySet();
        for (String api : apis) cancelApi(api);
    }

    public static void cancelApi(String apiTag) {
        if (mApiTags.isEmpty()) return;
        Disposable disposable = mApiTags.get(apiTag);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            removeApi(apiTag);
        }
    }

    public static void removeApi(String apiTag) {
        mApiTags.remove(apiTag);
    }
}
