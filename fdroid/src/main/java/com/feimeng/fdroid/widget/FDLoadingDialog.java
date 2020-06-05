package com.feimeng.fdroid.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.feimeng.fdroid.R;

/**
 * Author: Feimeng
 * Time:   2020/6/5
 * Description: Loading对话框，通过延时显示和隐藏对话框，避免UI视图的“闪烁”现象。参考ContentLoadingProgressBar代码
 */
public class FDLoadingDialog extends Dialog {
    private static final int SHOW_DELAY = 500; // 显示延时 ms
    private static final int MIN_SHOW_TIME = 500; // 最短显示时长 ms

    private TextView mMessageView;
    private int mShowDelay; // 显示延时 ms
    private int mMinShowTime; // 最短显示时长 ms
    private long mStartTime = -1;  // 开始显示时的时间
    private boolean mPostedHide = false;
    private boolean mPostedShow = false;
    private boolean mDismissed = false;

    private Handler mHandler = new Handler();

    private final Runnable mDelayedShow = new Runnable() {
        @Override
        public void run() {
            mPostedShow = false;
            if (!mDismissed) {
                mStartTime = System.currentTimeMillis();
                FDLoadingDialog.super.show();
            }
        }
    };

    private final Runnable mDelayedHide = new Runnable() {
        @Override
        public void run() {
            mPostedHide = false;
            mStartTime = -1;
            FDLoadingDialog.super.dismiss();
        }
    };

    @Override
    public void show() {
        showDialog();
    }

    @Override
    public void dismiss() {
        hideDialog();
    }

    /**
     * Loading对话框
     *
     * @param context    上下文
     * @param themeResId 主题ID
     * @param message    消息
     */
    public FDLoadingDialog(@NonNull Context context, @StyleRes int themeResId, @Nullable String message) {
        this(context, themeResId, message, SHOW_DELAY, MIN_SHOW_TIME);
    }

    /**
     * Loading对话框
     *
     * @param context     上下文
     * @param themeResId  主题ID
     * @param message     消息
     * @param showDelay   显示延时 ms
     * @param minShowTime 最短显示时长 ms
     */
    public FDLoadingDialog(@NonNull Context context, @StyleRes int themeResId, @Nullable String message, int showDelay, int minShowTime) {
        super(context, themeResId);
        mShowDelay = showDelay;
        mMinShowTime = minShowTime;
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        @SuppressLint("InflateParams") ViewGroup loadView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.fd_dialog_loading, null);
        setContentView(loadView);
        mMessageView = (TextView) loadView.getChildAt(1);
        updateLoadingDialog(message);
    }

    public void updateLoadingDialog(@Nullable String message) {
        if (TextUtils.isEmpty(message)) {
            mMessageView.setVisibility(View.GONE);
        } else {
            mMessageView.setVisibility(View.VISIBLE);
            mMessageView.setText(message);
        }
    }

    public void showDialog() {
        mStartTime = -1;
        mDismissed = false;
        mHandler.removeCallbacks(mDelayedHide);
        mPostedHide = false;
        if (!mPostedShow) {
            mHandler.postDelayed(mDelayedShow, mShowDelay);
            mPostedShow = true;
        }
    }

    public void hideDialog() {
        mDismissed = true;
        mHandler.removeCallbacks(mDelayedShow);
        mPostedShow = false;
        long diff = System.currentTimeMillis() - mStartTime;
        if (diff >= mMinShowTime || mStartTime == -1) {
            super.dismiss();
        } else {
            if (!mPostedHide) {
                mHandler.postDelayed(mDelayedHide, mMinShowTime - diff);
                mPostedHide = true;
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mDelayedHide);
        mHandler.removeCallbacks(mDelayedShow);
    }
}
