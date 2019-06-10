package com.feimeng.fdroid.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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

    private static Boolean sEnable;// 是否显示Toast
    private static Wrapper sWrapper; // Toast具体实现类
    @LayoutRes
    public static Integer sLayoutResId; // 自定义布局
    public static Integer sGravity, sXOffset, sYOffset; // 自定义显示位置

    /**
     * 初始化
     *
     * @param isShow 是否显示Toast
     */
    public static void init(boolean isShow) {
        sEnable = isShow;
        if (sWrapper == null) sWrapper = new DefaultWrapper();
    }

    /**
     * 初始化，自定义Toast显示
     *
     * @param isShow 是否显示Toast
     */
    public static void init(boolean isShow, Wrapper wrapper) {
        sEnable = isShow;
        sWrapper = wrapper;
    }

    public static void setView(@LayoutRes int resourceId) {
        sLayoutResId = resourceId;
    }

    public static void setGravityView(int gravity, int xOffset, int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    /**
     * 短时间显示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showS(Context context, String message) {
        if (sEnable == null) throw new ExceptionInInitializerError("T.init(Boolean)初始化方法未调用");
        if (context == null || TextUtils.isEmpty(message)) return;
        sWrapper.showS(context, message);
    }

    /**
     * 长时间显示
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void showL(Context context, String message) {
        if (sEnable == null) throw new ExceptionInInitializerError("T.init(Boolean)初始化方法未调用");
        if (context == null || TextUtils.isEmpty(message)) return;
        if (sEnable) sWrapper.showL(context, message);
    }


    public interface Wrapper {
        void showS(@NonNull Context context, @NonNull String message);

        void showL(@NonNull Context context, @NonNull String message);
    }

    static class DefaultWrapper implements Wrapper {
        private static Toast mToast;

        @Override
        public void showS(@NonNull Context context, @NonNull String message) {
            show(context, message, Toast.LENGTH_SHORT);
        }

        @Override
        public void showL(@NonNull Context context, @NonNull String message) {
            show(context, message, Toast.LENGTH_LONG);
        }

        /**
         * 显示Toast
         */
        private void show(Context context, String message, int duration) {
            if (TextUtils.isEmpty(message)) return;
            if (mToast == null) {
                createToast(context.getApplicationContext(), message, duration);
            } else {
                mToast.setText(message);
                mToast.setDuration(duration);
            }
            mToast.show();
        }

        @SuppressLint("ShowToast")
        private static void createToast(Context context, String message, int length) {
            mToast = Toast.makeText(context, message, length);
            if (sLayoutResId != null) {
                mToast.setView(LayoutInflater.from(context).inflate(sLayoutResId, null));
            }
            if (sGravity != null) {
                mToast.setGravity(sGravity, sXOffset, sYOffset);
            }
        }
    }
}
