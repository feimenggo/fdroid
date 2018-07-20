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
        mCoreThread.start();
    }

    /**
     * 核心配置
     */
    protected abstract void config();

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

    public FDCoreThread getCoreThread() {
        return mCoreThread;
    }

    /**
     * 等待核心线程执行完毕
     */
    public void waitCoreThread() {
        if (getCoreThread().getState() != Thread.State.TERMINATED) {
            try {
                getCoreThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class FDCoreThread extends Thread {
        @Override
        public void run() {
            initCore();
            config();
        }
    }
}
