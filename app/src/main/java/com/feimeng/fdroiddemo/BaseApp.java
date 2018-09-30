package com.feimeng.fdroiddemo;

import android.content.Context;

import com.feimeng.fdroid.base.FDApp;
import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.utils.L;

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
        // 在这里为应用设置异常处理，然后程序才能获取未处理的异常
//        CrashHandler.getInstance().init();
//        CrashHandler.getInstance().setCrashInterceptor(new CrashHandler.CrashInterceptor() {
//            @Override
//            public void uncaughtException(CrashHandler crashHandler, Context context, Thread thread, Throwable exception) {
//                L.d("我不让你蹦！");
//                crashHandler.restartActivity(new Intent(context, CrashActivity.class).putExtra("feimeng", "ok"));
//            }
//        });
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
    }

    @Override
    protected void configAsync() {

    }
}
