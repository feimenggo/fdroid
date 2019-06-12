package com.feimeng.fdroiddemo;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dovar.dtoast.DToast;
import com.feimeng.fdroid.mvp.FDApp;
import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.T;

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
        T.init(true, new T.Wrapper() {
            @Override
            public void showS(@NonNull Context context, @NonNull String message) {
                DToast.make(context).setText(R.id.toast, message).show();
            }

            @Override
            public void showL(@NonNull Context context, @NonNull String message) {
                DToast.make(context).setText(R.id.toast, message).showLong();
            }
        });
    }

    @Override
    protected void configAsync() {

    }
}
