package com.feimeng.fdroiddemo;

import android.content.Context;

import com.feimeng.fdroid.mvp.FDApp;
import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.T;
import com.squareup.leakcanary.LeakCanary;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by feimeng on 2018/3/13.
 */
public class BaseApp extends FDApp {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d("Application启动" + Math.random());
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    @Override
    protected void config() {
        FDConfig.SHOW_LOG = true;
        FDConfig.SHOW_HTTP_LOG = true;
        FDConfig.SHOW_HTTP_EXCEPTION_INFO = true;
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Thread thread = Thread.currentThread();
                String name = thread.getName();
                L.d("nodawang", "未捕获的异常" + name);
                throwable.printStackTrace();
            }
        });
        T.init(true);
    }

    @Override
    protected void configAsync() {

    }
}
