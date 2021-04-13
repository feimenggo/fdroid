package com.feimeng.fdroid.mvp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Author: Feimeng
 * Time:   2020/2/27
 * Description: FDroid核心类
 */
public abstract class FDCore {
    private static Application mApplication; // App上下文
    private static FDCoreThread mCoreThread; // 用于初始化的子线程
    private static Throwable mConfigThrowable; // 异步初始化出错

    public static void init(Application application, FDCore fdCore) {
        fdCore.onInit(application);
    }

    public static Application getApplication() {
        return mApplication;
    }

    /**
     * 获取异步配置异常
     *
     * @return 异步配置抛出异常
     */
    @Nullable
    public static Throwable getConfigThrowable() {
        return mConfigThrowable;
    }

    /**
     * 是否需要等待异步配置
     *
     * @return true/false
     */
    public static boolean needWaitConfigFinish() {
        return mCoreThread != null && mCoreThread.getState() != Thread.State.TERMINATED;
    }

    /**
     * 等待异步配置执行完毕
     *
     * @throws Throwable 异步配置抛出异常
     */
    public static void waitConfigFinish() throws Throwable {
        if (mCoreThread.getState() != Thread.State.TERMINATED) mCoreThread.join();
        if (mConfigThrowable != null) throw mConfigThrowable;
    }

    private void onInit(Application application) {
        mApplication = application;
        if (application.getPackageName().equals(getProcessName(application, android.os.Process.myPid()))) {
            config(application);
            (mCoreThread = new FDCoreThread()).start();
        }
    }

    /**
     * 配置 在UI线程调用
     */
    protected void config(Application application) {
    }

    /**
     * 异步配置 在子线程调用
     */
    protected void configAsync(Application application) throws Throwable {
    }

    private class FDCoreThread extends Thread {
        @Override
        public void run() {
            try {
                configAsync(mApplication);
            } catch (Throwable throwable) {
                mConfigThrowable = throwable;
            }
        }
    }

    private String getProcessName(Context context, int pid) {
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
}
