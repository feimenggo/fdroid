package com.feimeng.fdroid.base;

import android.app.Application;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.SP;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroid.utils.UE;

/**
 * 全局Application
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDApp extends Application {
    private static FDApp sInstance;
    private FDCoreThread mCoreThread = new FDCoreThread();

    public static FDApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        configBeforeCore();
        mCoreThread.start();
        config();
    }

    /**
     * 配置 UI线程
     */
    protected void config() {

    }

    /**
     * 配置 在Core执行之前 UI线程
     */
    protected void configBeforeCore() {

    }

    /**
     * 配置 子线程
     */
    protected abstract void configAsync();

    /**
     * 等待核心线程执行完毕
     */
    public void waitCoreThread() {
        try {
            if (mCoreThread.getState() != Thread.State.TERMINATED) {
                mCoreThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initCore() {
        // Toast
        T.init(FDConfig.SHOW_TOAST);
        // Log
        L.init(FDConfig.SHOW_LOG, L.V);
        // 增强用户体验效果工具
        UE.init(this);
        // 共享参数 SharedPreferences
        SP.init(this, FDConfig.SP_NAME);
    }

    private class FDCoreThread extends Thread {
        @Override
        public void run() {
            initCore();
            configAsync();
        }
    }
}
