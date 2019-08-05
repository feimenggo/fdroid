package com.feimeng.fdroiddemo;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.mvp.FDApp;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.T;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by feimeng on 2018/3/13.
 */
public class BaseApp extends FDApp {
    @Override
    protected void config() {
        FDConfig.SHOW_LOG = true;
        FDConfig.SHOW_HTTP_LOG = true;
        FDConfig.SHOW_HTTP_EXCEPTION_INFO = true;
        // 处理RxJava取消订阅后抛出的异常
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
