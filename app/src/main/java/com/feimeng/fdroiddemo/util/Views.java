package com.feimeng.fdroiddemo.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Author: Feimeng
 * Time:   2019/6/26
 * Description: 控件工具类
 */
public class Views {
    public static boolean isVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    public static boolean isGone(View view) {
        return view != null && view.getVisibility() == View.GONE;
    }

    public static void visibility(View view, boolean visibility) {
        if (view != null) {
            if (visibility) {
                visible(view);
            } else {
                gone(view);
            }
        }
    }

    public static void visible(View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void invisible(View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() != View.INVISIBLE) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void gone(View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() != View.GONE) view.setVisibility(View.GONE);
        }
    }

    public static void enable(View... views) {
        for (View view : views) {
            if (view != null && !view.isEnabled()) view.setEnabled(true);
        }
    }

    public static void textColor(int color, TextView... views) {
        for (TextView view : views) {
            if (view != null) view.setTextColor(color);
        }
    }

    public static void disable(View... views) {
        for (View view : views) {
            if (view != null && view.isEnabled()) view.setEnabled(false);
        }
    }

    public static void imageResource(@DrawableRes int resId, ImageView... views) {
        for (ImageView view : views) {
            if (view != null) view.setImageResource(resId);
        }
    }

    public static void drawableLeft(Context context, TextView tv, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    public static void drawableLeft(TextView tv, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), resId);
        if (drawable != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    public static void drawableRight(Context context, TextView tv, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }

    public static void drawableTop(TextView tv, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(tv.getContext(), resId);
        if (drawable != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    public static void drawableTop(Context context, TextView tv, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable != null) {
            tv.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    public static void clearFocus(View... views) {
        for (View view : views) {
            if (view != null) view.clearFocus();
        }
    }

    public static void clickListener(@Nullable View.OnClickListener l, View... views) {
        for (View view : views) {
            if (view != null) view.setOnClickListener(l);
        }
    }

    public static void foregroundColor(TextView tv, String text, @ColorInt int color, int start, int end) {
        SpannableStringBuilder span = new SpannableStringBuilder(text);
        span.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(span);
    }

    /**
     * 最近一次点击的时间
     */
    private static long mLastClickTime = 0;
    /**
     * 最近一次点击的控件ID
     */
    private static int mLastClickViewId = -1;

    /**
     * 是否是快速重复点击
     *
     * @param view 点击的控件
     * @return true:是，false:不是
     */
    public static boolean isFastDoubleClick(View view) {
        return isFastDoubleClick(view, 600);
    }

    /**
     * 是否是快速重复点击
     *
     * @param view           点击的控件
     * @param intervalMillis 时间间隔（毫秒）
     * @return true:是，false:不是
     */
    public static boolean isFastDoubleClick(View view, long intervalMillis) {
        if (view == null) return true;
        int viewId = view.getId();
        long time = SystemClock.elapsedRealtime();
        long timeInterval = Math.abs(time - mLastClickTime);
        if (timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true;
        } else {
            mLastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }

    public static void clearFast() {
        mLastClickTime = 0;
        mLastClickViewId = -1;
    }
}
