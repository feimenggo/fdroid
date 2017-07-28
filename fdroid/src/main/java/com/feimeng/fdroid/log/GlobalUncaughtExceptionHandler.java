package com.feimeng.fdroid.log;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import com.feimeng.fdroid.utils.ActivityPageManager;

/**
 * Created by feimeng on 2017/7/28.
 */
public class GlobalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static GlobalUncaughtExceptionHandler mInstance;
    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的 UncaughtException 处理类
    private Class mRestartActivity;
    private Context mContext;
    private String mTips;

    private GlobalUncaughtExceptionHandler() {
    }

    public static GlobalUncaughtExceptionHandler getInstance() {
        if (mInstance == null) {
            synchronized (GlobalUncaughtExceptionHandler.class) {
                if (mInstance == null) {
                    mInstance = new GlobalUncaughtExceptionHandler();
                }
            }
        }
        return mInstance;
    }

    /**
     * @param context 上下文
     */
    public void init(Context context) {
        init(context, "很抱歉，程序出现异常，即将退出...", null);
    }

    /**
     * @param context         上下文
     * @param tips            崩溃提示
     * @param restartActivity 重启Activity，不重启为null
     */
    public void init(Context context, String tips, Class restartActivity) {
        mContext = context;
        mTips = tips;
        mRestartActivity = restartActivity;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    @SuppressWarnings("WrongConstant")
    public void uncaughtException(Thread thread, Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, e);
        } else {
            if (mRestartActivity != null) { // 如果需要重启
                Intent intent = new Intent(mContext.getApplicationContext(), mRestartActivity);
                AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                //重启应用，得使用PendingIntent
                PendingIntent restartIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent);
            }
            ActivityPageManager.getInstance().finishAllActivity();
        }
    }

    private boolean handleException(final Throwable e) {
        if (e == null) return false;
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, getTips(e), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }

    private String getTips(Throwable e) {
        if (e instanceof SecurityException) {
            if (e.getMessage().contains("android.permission.CAMERA")) {
                mTips = "请授予应用相机权限，程序出现异常，即将退出.";
            } else if (e.getMessage().contains("android.permission.RECORD_AUDIO")) {
                mTips = "请授予应用麦克风权限，程序出现异常，即将退出。";
            } else if (e.getMessage().contains("android.permission.WRITE_EXTERNAL_STORAGE")) {
                mTips = "请授予应用存储权限，程序出现异常，即将退出。";
            } else if (e.getMessage().contains("android.permission.READ_PHONE_STATE")) {
                mTips = "请授予应用电话权限，程序出现异常，即将退出。";
            } else if (e.getMessage().contains("android.permission.ACCESS_COARSE_LOCATION") || e.getMessage().contains("android.permission.ACCESS_FINE_LOCATION")) {
                mTips = "请授予应用位置信息权，很抱歉，程序出现异常，即将退出。";
            } else {
                mTips = "很抱歉，程序出现异常，即将退出，请检查应用权限设置。";
            }
        }
        return mTips;
    }
}

