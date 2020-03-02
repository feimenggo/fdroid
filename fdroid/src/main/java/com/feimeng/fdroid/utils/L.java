package com.feimeng.fdroid.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Log日志统一操作类
 * Created by feimeng on 2017/1/20.
 */
public final class L {
    private static final String TAG = "tag";

    private static boolean isShowLog;// 是否显示Log
    private static int level;// 显示等级

    public static final int V = 5;
    public static final int D = 4;
    public static final int I = 3;
    public static final int W = 2;
    public static final int E = 1;
    public static final int S = 0;

    /**
     * 初始化
     *
     * @param isShowLog 是否显示日志
     * @param level     显示等级
     */
    public static void init(boolean isShowLog, int level) {
        L.isShowLog = isShowLog;
        L.level = level;
    }

    private L() {
        throw new UnsupportedOperationException("L cannot be instantiated!");
    }

    // 下面是传入自定义tag的函数
    public static void v(String tag, Object obj) {
        if (isShowLog && level >= 5) print(V, tag, obj);
    }

    public static void d(String tag, Object obj) {
        if (isShowLog && level >= 4) print(D, tag, obj);
    }

    public static void i(String tag, Object obj) {
        if (isShowLog && level >= 3) print(I, tag, obj);
    }

    public static void w(String tag, Object obj) {
        if (isShowLog && level >= 2) print(W, tag, obj);
    }

    public static void e(String tag, Object obj) {
        if (isShowLog && level >= 1) print(E, tag, obj);
    }

    public static void s(String tag, Object obj) {
        if (isShowLog && level >= 0) print(S, tag, obj);
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

    public static void s(Object obj) {
        s(TAG, obj);
    }

    /**
     * 执行打印方法
     *
     * @param type   类型
     * @param tagStr Tag
     * @param obj    信息
     */
    public static void print(int type, String tagStr, Object obj) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String msg;

        int index = 4;// 调用的深度
        if (tagStr.equals(TAG))
            index = 5;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        methodName = methodName.substring(0, 1).toUpperCase(Locale.CHINA) + methodName.substring(1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(className).append(":").append(lineNumber).append(")#").append(methodName).append("\n");

        if (obj == null) {
            msg = "Log with null Object";
        } else {
            msg = obj.toString();
        }
        stringBuilder.append(msg);

        String logStr = stringBuilder.append("\n").toString();

        switch (type) {
            case V:
                Log.v(tagStr, logStr);
                break;
            case D:
                Log.d(tagStr, logStr);
                break;
            case I:
                Log.i(tagStr, logStr);
                break;
            case W:
                Log.w(tagStr, logStr);
                break;
            case E:
                Log.e(tagStr, logStr);
                break;
            case S:
                System.out.print(logStr);
                break;
        }
    }
}