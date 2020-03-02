package com.feimeng.fdroid.mvp;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.feimeng.fdroid.utils.L;

/**
 * Author: Feimeng
 * Time:   2020/2/27
 * Description: Application类
 */
public abstract class FDApp extends Application {
    private static FDApp sInstance;
    private Handler mHandler; // UI Handler

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends FDApp> T getInstance() {
        return (T) sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mHandler = new Handler(Looper.getMainLooper());
        FDCore.init(this, new FDCore() {
            @Override
            protected void config(Application application) {
                FDApp.this.config(application);
            }

            @Override
            protected void configAsync(Application application) {
                FDApp.this.configAsync(application);
            }
        });
    }

    /**
     * 配置 在UI线程调用
     */
    protected void config(Application application) {
        // 初始化 Log
        L.init(true, L.V);
    }

    /**
     * 配置 在子线程调用
     */
    protected abstract void configAsync(Application application);

    /**
     * 获取UI线程 Handler
     *
     * @return Handler
     */
    @NonNull
    public Handler getHandler() {
        return mHandler;
    }

    /**
     * UI线程执行Runnable
     *
     * @param runnable Runnable
     */
    public void post(@NonNull Runnable runnable) {
        mHandler.post(runnable);
    }
}
