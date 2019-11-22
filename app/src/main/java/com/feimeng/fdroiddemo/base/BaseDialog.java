package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.feimeng.fdroid.mvp.FDDialog;
import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroiddemo.R;

import java.lang.reflect.Field;
import java.util.Objects;


/**
 * Author: Feimeng
 * Time:   2018/4/13 16:14
 * Description: 对话框封装类
 */
public abstract class BaseDialog<V extends BaseView, P extends FDPresenter<V>> extends FDDialog<V, P> {
    protected int mGravity;// 显示位置
    protected boolean mIsMatchWidth;// 是否宽度铺满
    protected boolean mCancelable;// 是否外部点击取消
    protected Integer width;
    protected Integer height = WindowManager.LayoutParams.WRAP_CONTENT;
    private boolean mActive;
    private DialogInterface.OnDismissListener mOnDismissListener;

    public BaseDialog() {
        this(Gravity.BOTTOM, true, true);
    }

    public BaseDialog(int gravity, boolean isMatchWidth, boolean cancelable) {
        mGravity = gravity;
        mIsMatchWidth = isMatchWidth;
        mCancelable = cancelable;
    }

    @LayoutRes
    public abstract int layoutResId();

    public abstract void initView(Context context, View view);

    @Override
    public int getTheme() {
        return R.style.BaseDialog;
    }

    @StyleRes
    protected int getAnim() {
        return R.style.WindowAnimation_Fade;
    }

    public P initPresenter() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActive = true;
        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @NonNull
    @Override
    public Dialog getDialog() {
        return Objects.requireNonNull(super.getDialog());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(layoutResId(), container, false);
        Dialog dialog = getDialog();
        setCancelable(mCancelable);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(getAnim());
            window.getDecorView().setPadding(0, 0, 0, 0);
            // 获得窗体的属性
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = mGravity;
            // 设置Dialog宽度匹配屏幕宽度
            if (width != null) {
                lp.width = width;
            } else {
                lp.width = mIsMatchWidth ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
            }
            // 设置Dialog高度
            lp.height = height;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(getContext(), view);
    }

    protected void loadData(Context context) {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(getContext());
    }

    public BaseDialog show(FragmentManager manager) {
        show(manager, "BaseDialog");
        return this;
    }

    public void show(@NonNull FragmentManager manager, String tag) {
        try {
            // 解决Can not perform this action after onSaveInstanceState异常
            Field field = DialogFragment.class.getDeclaredField("mDismissed");
            field.setAccessible(true);
            field.setBoolean(this, false);
            field = DialogFragment.class.getDeclaredField("mShownByMe");
            field.setAccessible(true);
            field.setBoolean(this, true);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            super.show(manager, tag);
        }
    }

    public void showNow(@NonNull FragmentManager manager, String tag) {
        try {
            // 解决Can not perform this action after onSaveInstanceState异常
            Field field = DialogFragment.class.getDeclaredField("mDismissed");
            field.setAccessible(true);
            field.setBoolean(this, false);
            field = DialogFragment.class.getDeclaredField("mShownByMe");
            field.setAccessible(true);
            field.setBoolean(this, true);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitNowAllowingStateLoss();
        } catch (Exception e) {
            super.showNow(manager, tag);
        }
    }

    public synchronized void showOne(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) showNow(manager, tag);
    }

    public BaseDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
            mOnDismissListener = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setOnDismissListener(null);
        mActive = false;
    }

    public boolean isActive() {
        return mActive;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        Dialog dialog = super.getDialog();
        if (dialog != null) dialog.setCanceledOnTouchOutside(cancelable);
    }
}