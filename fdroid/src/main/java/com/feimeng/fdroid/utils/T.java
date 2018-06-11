package com.feimeng.fdroid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Toast统一管理类
 * Created by feimeng on 2017/1/20.
 */
public class T {

    private T() {
        throw new UnsupportedOperationException("T cannot be instantiated");
    }

    private static boolean mEnable = true;// 是否显示Toast
    private static Toast mToast;

    /**
     * 初始化
     *
     * @param isShow 是否显示Toast
     */
    public static void init(boolean isShow) {
        mEnable = isShow;
    }

    /**
     * 短时间显示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showS(Context context, String message) {
        if (mEnable) show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showL(Context context, String message) {
        if (mEnable) show(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast
     */
    private static void show(Context context, String message, int length) {
        if (TextUtils.isEmpty(message)) return;
        if (mToast == null) {
            createToast(context.getApplicationContext(), message, length);
        } else {
            mToast.setText(message);
            mToast.setDuration(length);
        }
        mToast.show();
    }

    public static void setView(Context context, @LayoutRes int resourceId) {
        if (mToast == null) {
            createToast(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setView(LayoutInflater.from(context).inflate(resourceId, null));
    }

    public static void setGravityView(Context context, int gravity, int xOffset, int yOffset) {
        if (mToast == null) {
            createToast(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
        mToast.setGravity(gravity, xOffset, yOffset); // Toast位置居中
    }

    @SuppressLint("ShowToast")
    private static void createToast(Context context, String message, int length) {
        mToast = Toast.makeText(context, message, length);
    }
}
