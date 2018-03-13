package com.feimeng.fdroid.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import com.feimeng.fdroid.base.FDApp;
import com.feimeng.fdroid.config.FDConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 异常处理器，支持存储和日志上传
 * Created by feimeng on 2018/3/13.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "AppCrash" + File.separator;// 异常信息文件存储路径，以 / 结尾
    private String fileName = "crash";// 异常信息文件名
    private String fileNameSuffix = ".trace";// 异常信息文件名后缀
    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public String getPath() {
        return path;
    }

    /**
     * 设置异常信息文件存储路径，以 / 结尾
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNameSuffix() {
        return fileNameSuffix;
    }

    public void setFileNameSuffix(String fileNameSuffix) {
        this.fileNameSuffix = fileNameSuffix;
    }

    private Context getContext() {
        return FDApp.getInstance().getApplicationContext();
    }

    public void init() {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当程序中有未捕获的异常，系统将自动调用此方法
     *
     * @param thread    为出现未捕获异常的线程
     * @param exception 为未捕获的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        try {
            // 导出异常信息到SD卡中
            // 建议：上传异常信息到服务器，便于开发人员分析日志从而解决bug
            dumpExceptionToSDCard(exception);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果设置了监听器就执行监听器
        if (mInterceptor != null) {
            mInterceptor.uncaughtException(this, getContext(), thread, exception);
            kill();
        } else {
            // 如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
            if (mDefaultCrashHandler != null) {
                mDefaultCrashHandler.uncaughtException(thread, exception);
            } else {
                kill();
            }
        }
    }

    private void dumpExceptionToSDCard(Throwable exception) throws Exception {
        // 如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (FDConfig.DEBUG) {
                L.w(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        File dir = new File(path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                L.w(TAG, "crash dir can't make,skip dump exception");
                return;
            }
        }
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        File file = new File(path + fileName + "_" + time + fileNameSuffix);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        pw.println(time);
        dumpDeviceInfo(pw);
        pw.println();
        exception.printStackTrace(pw);
        pw.close();
    }

    private void dumpDeviceInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = getContext().getPackageManager();
        PackageInfo pi = pm.getPackageInfo(getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App version：");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);
        // Android版本号
        pw.print("OS version：");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        // 手机制造商
        pw.print("Vendor：");
        pw.println(Build.MANUFACTURER);
        // 手机型号
        pw.print("Model：");
        pw.println(Build.MODEL);
        // CPU架构
        pw.print("CPU ABI：");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            for (int i = 0; i < Build.SUPPORTED_ABIS.length; i++) {
                pw.print(Build.SUPPORTED_ABIS[i]);
                if (i != Build.SUPPORTED_ABIS.length - 1) {
                    pw.print(",");
                }
            }
            pw.println();
        } else {
            pw.println(Build.CPU_ABI);
        }
    }

    private CrashInterceptor mInterceptor;

    public void setCrashInterceptor(CrashInterceptor interceptor) {
        this.mInterceptor = interceptor;
    }

    public interface CrashInterceptor {
        void uncaughtException(CrashHandler crashHandler, Context context, Thread thread, Throwable exception);
    }

    private void kill() {
        Process.killProcess(Process.myPid());
    }

    public void restartActivity() {
        restartActivity(null);
    }

    public void restartActivity(Intent intent) {
        if (intent == null) {
            intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
        }
        if (intent == null) return;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
