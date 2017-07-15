package com.feimeng.fdroid.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 * Created by feimeng on 2017/1/20.
 */
public class T {

    private T() {
        throw new UnsupportedOperationException("T cannot be instantiated");
    }

    private static boolean isShow = true;// 是否显示Toast
    private static Toast mToast;

    /**
     * 初始化
     *
     * @param isShow 是否显示Toast
     */
    public static void init(boolean isShow) {
        T.isShow = isShow;
    }

    /**
     * 短时间显示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showS(Context context, String message) {
        if (isShow)
            show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showL(Context context, String message) {
        if (isShow)
            show(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast
     */
    private static void show(Context context, String message, int length) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, length);
//            mToast.setGravity(Gravity.CENTER, 0, 0); // Toast位置居中
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }
}
