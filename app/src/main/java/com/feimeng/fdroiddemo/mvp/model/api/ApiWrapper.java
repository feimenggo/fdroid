package com.feimeng.fdroiddemo.mvp.model.api;

import androidx.annotation.NonNull;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.mvp.model.api.FDApi;
import com.feimeng.fdroid.mvp.model.api.bean.Optional;
import com.feimeng.fdroiddemo.data.dto.LoginDto;

import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.feimeng.fdroid.config.FDConfig.SHOW_HTTP_LOG;

/**
 * Api类的包装
 */
public class ApiWrapper extends FDApi {
    private static final ApiWrapper instance = new ApiWrapper(); // 单例模式
    private final ApiService api;

    private ApiWrapper() {
        addHttpMockData("user/login", "{\"code\":200,\"info\":\"成功\",\"data\":{\"id\":\"1000\",\"nickname\":\"小飞\",\"token\":\"xxx\"}}");
        addHttpMockData("user/register", "{\"code\":200,\"info\":\"成功\",\"data\":null}");
        addHttpMockData("user/info", "{\"code\":210,\"info\":\"模拟后端登录失败\",\"data\":null}");
        api = getRetrofit("http://www.baidu.com/").create(ApiService.class);
        // 限制App中并行网络请求的数量
        setExecutor(Executors.newFixedThreadPool(1));
    }

    public static ApiWrapper getInstance() {
        return instance;
    }

    @Override
    public OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(FDConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS) // 连接超时时间为15秒
                .writeTimeout(FDConfig.WRITE_TIMEOUT, TimeUnit.SECONDS) // 写入超时时间
                .readTimeout(FDConfig.READ_TIMEOUT, TimeUnit.SECONDS); // 读取超时时间
        // Log拦截器 打印所有的Log
        if (SHOW_HTTP_LOG) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logInterceptor);
        }
        clientBuilder.addInterceptor(chain -> {
            try { // 模拟耗时的网络请求
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return chain.proceed(chain.request());
        });
        startMock(clientBuilder);
        return clientBuilder.build();
    }

    @NonNull
    @Override
    public String translateException(Throwable e) {
        if (e instanceof UnknownHostException) {
            return "网络异常，请重试";
        } else {
            return super.translateException(e);
        }
    }

    /**
     * 通过异步的方式进行登录
     */
    public Observable<LoginDto> login(String phone, String password) {
        return api.login(json("phone", phone, "password", password)).compose(this.<LoginDto>applySchedulers());
    }

    /**
     * 通过同步的方式进行登录
     */
    public LoginDto login_(String phone, String password) throws Exception {
        return call(api.login_(json("phone", phone, "password", password)));
    }

    public Observable<Optional<Void>> register(String phone, String password) {
        return api.register(json("phone", phone, "password", password)).compose(this.<Void>applySchedulersNewOptional());
    }

    public Integer getUserInfo(String phone, String password) throws Exception {
        return call(api.getUserInfo(json("phone", phone, "password", password)));
    }
}
