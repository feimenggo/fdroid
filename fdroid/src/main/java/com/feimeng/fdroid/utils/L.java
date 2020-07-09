package com.feimeng.fdroid.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Log日志框架
 * Created by feimeng on 2017/1/20.
 */
public final class L {
    private static final String TAG = "TAG";

    public static final int V = 5; // VERBOSE
    public static final int D = 4; // DEBUG
    public static final int I = 3; // INFO
    public static final int W = 2; // WARN
    public static final int E = 1; // ERROR
    public static final int NOTHING = 1; // Nothing

    private static int level = I; // 显示等级
    private static LogMonitor monitor; // 日志监听器

    private L() {
        throw new UnsupportedOperationException("Log cannot be instantiated!");
    }

    /**
     * 初始化
     *
     * @param isShowLog 是否显示日志
     * @param level     显示等级
     */
    public static void init(boolean isShowLog, int level) {
        setLevel(isShowLog ? level : NOTHING);
    }

    /**
     * 设置日志等级
     *
     * @param level 显示等级
     */
    public static void setLevel(int level) {
        L.level = level;
    }

    /**
     * 设置日志监听器
     *
     * @param monitor 日志监听器
     */
    public static void setMonitor(LogMonitor monitor) {
        L.monitor = monitor;
    }

    public static void v(Object obj) {
        v(TAG, obj);
    }

    public static void d(Object obj) {
        d(TAG, obj);
    }

    public static void i(Object obj) {
        i(TAG, obj);
    }

    public static void w(Object obj) {
        w(TAG, obj);
    }

    public static void e(Object obj) {
        e(TAG, obj);
    }

    // 下面是传入自定义tag的函数
    public static void v(String tag, Object obj) {
        if (level >= 5) print(V, tag, obj);
    }

    public static void d(String tag, Object obj) {
        if (level >= 4) print(D, tag, obj);
    }

    public static void i(String tag, Object obj) {
        if (level >= 3) print(I, tag, obj);
    }

    public static void w(String tag, Object obj) {
        if (level >= 2) print(W, tag, obj);
    }

    public static void e(String tag, Object obj) {
        if (level >= 1) print(E, tag, obj);
    }

    /**
     * 执行打印方法
     *
     * @param type   类型
     * @param tagStr Tag
     * @param obj    信息
     */
    public static void print(int type, String tagStr, Object obj) {
        String msg;
        int index = 4;// 调用的深度
        if (tagStr.equals(TAG)) index = 5;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        methodName = methodName.substring(0, 1).toUpperCase(Locale.CHINA) + methodName.substring(1);
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(className).append(":").append(lineNumber).append(")#").append(methodName).append("\n");
        if (obj == null) {
            msg = "Log with null Object";
        } else {
            msg = obj.toString();
        }
        sb.append(msg);
        String logStr = sb.append("\n").toString();
        // 打印日志
        printLog(type, tagStr, logStr);
    }

    private static void printLog(int type, String tag, String info) {
        if (monitor != null) {
            monitor.onPrintLog(type, tag, info);
        } else {
            switch (type) {
                case V:
                    Log.v(tag, info);
                    break;
                case D:
                    Log.d(tag, info);
                    break;
                case I:
                    Log.i(tag, info);
                    break;
                case W:
                    Log.w(tag, info);
                    break;
                case E:
                    Log.e(tag, info);
                    break;
            }
        }
    }

    /**
     * 日志监听器
     */
    public interface LogMonitor {
        /**
         * 打印日志
         *
         * @param type 类型
         * @param tag  标签
         * @param info 信息
         */
        void onPrintLog(int type, String tag, String info);
    }
}