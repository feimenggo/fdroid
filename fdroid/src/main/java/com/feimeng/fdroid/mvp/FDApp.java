package com.feimeng.fdroid.mvp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.SP;

import java.util.List;

/**
 * 全局Application
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDApp extends Application {
    private static FDApp sInstance;
    private Handler mHandler; // UI Handler
    private FDCoreThread mCoreThread; // 用于初始化的子线程

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends FDApp> T getInstance() {
        return (T) sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (getPackageName().equals(getProcessName(this, android.os.Process.myPid()))) {
            sInstance = this;
            mHandler = new Handler(Looper.getMainLooper());
            config();
            (mCoreThread = new FDCoreThread()).start();
        }
    }

    /**
     * 配置 在UI线程调用
     */
    protected void config() {
        // 初始化 Log
        L.init(true, L.V);
        // 初始化 SharedPreferences
        SP.init(this, "fdroid");
    }

    /**
     * 配置 在子线程调用
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

    public String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) return null;
        for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

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

    private class FDCoreThread extends Thread {
        @Override
        public void run() {
            configAsync();
        }
    }
}
