package com.feimeng.fdroid.utils;

import android.app.Activity;
import android.os.Bundle;

import com.feimeng.fdroid.bean.Ignore;
import com.feimeng.fdroid.mvp.FDCore;

/**
 * Author: Feimeng
 * Time:   2021/3/31
 * Description: Activity恢复现场
 */
public class RecoverInstanceState {
    private static final long mStartupTime = System.currentTimeMillis();
    private static long mLastStartupId = -1L;
    private static SplashTask mSplashTask;

    public static void store(Bundle outState) {
        outState.clear();
        outState.putLong("AppStartupTime", mStartupTime);
    }

    /**
     * APP销毁重建，适合因为内存使用不足，应用被系统杀死的场景
     */
    public static void restore(Bundle savedInstanceState, SplashTask splashTask) {
        long savedStartupId = savedInstanceState.getLong("AppStartupTime"); // 获取之前的启动ID
        if (savedStartupId != mStartupTime) { // 启动ID不一致，进程销毁重建了，需要执行进程恢复操作
            if (savedStartupId != mLastStartupId) { // 执行进程恢复操作
                L.d("nodawang", "APP恢复 进入启动页 mStartupTime:" + mStartupTime + " mLastStartupId:" + mLastStartupId + " savedStartupId:" + savedStartupId);
                mLastStartupId = savedStartupId;
                mSplashTask = splashTask;
                splashTask.startSplash();
            } else { // 等待进程恢复操作完成
                L.d("nodawang", "APP恢复 等待核心线程 mStartupTime:" + mStartupTime + " mLastStartupId:" + mLastStartupId + " savedStartupId:" + savedStartupId);
                new FastTask<Ignore>() {
                    @Override
                    public Ignore task() throws Exception {
                        try {
                            FDCore.waitConfigFinish();
                        } catch (Throwable throwable) {
                            throw new Exception((throwable));
                        }
                        return Ignore.instance;
                    }
                }.runIO(new FastTask.Result<Ignore>() {
                    @Override
                    public void stop() {
                        splashTask.splashFinish(); // 进程已恢复，执行后续流程
                    }
                });
            }
        } else {
            splashTask.splashFinish();
        }
    }

    /**
     * 尝试恢复
     *
     * @return 是否恢复成功
     */
    public static boolean tryRecover(Activity splashActivity) {
        if (mSplashTask == null) return true;
        // 进程已恢复，进入后续流程
        mSplashTask.splashFinish();
        mSplashTask = null;
        if (splashActivity != null) splashActivity.finish();
        return false;
    }

    public abstract static class SplashTask {
        private final Runnable mRunnable;

        public SplashTask(Runnable runnable) {
            mRunnable = runnable;
        }

        public abstract void startSplash();

        public void splashFinish() {
            mRunnable.run();
        }
    }
}
