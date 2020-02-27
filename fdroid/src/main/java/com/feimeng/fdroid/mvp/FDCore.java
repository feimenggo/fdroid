package com.feimeng.fdroid.mvp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

/**
 * Author: Feimeng
 * Time:   2020/2/27
 * Description: FDroid核心类
 */
public abstract class FDCore {
    private static Application mApplication;
    private static FDCoreThread mCoreThread; // 用于初始化的子线程

    public static void init(Application application, FDCore fdCore) {
        fdCore.onInit(application);
    }

    public static Application getApplication() {
        return mApplication;
    }

    /**
     * 等待核心线程执行完毕
     */
    public static void waitConfigFinish() {
        try {
            if (mCoreThread.getState() != Thread.State.TERMINATED) {
                mCoreThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
     * 配置 在子线程调用
     */
    protected void configAsync(Application application) {
    }

    private class FDCoreThread extends Thread {
        @Override
        public void run() {
            configAsync(mApplication);
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
