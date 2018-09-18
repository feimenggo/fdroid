package com.feimeng.fdroid.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.feimeng.fdroid.mvp.base.FDPresenter;
import com.feimeng.fdroid.mvp.base.FDView;
import com.feimeng.fdroid.utils.ActivityPageManager;
import com.feimeng.fdroid.widget.FDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * Activity基类
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDActivity<V extends FDView, P extends FDPresenter<V>> extends RxAppCompatActivity {
    protected P mPresenter;

    /**
     * 页面布局的 根view
     */
    protected View mContentView;

    /**
     * 对话框
     */
    private Dialog mLoading;
    private boolean mStarted;
    private int mLoadTimes = 0; // 加载次数

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity管理
        ActivityPageManager.getInstance().addActivity(this);
        // 绑定控制器
        mPresenter = initPresenter();
        if (mPresenter != null && this instanceof FDView)
            mPresenter.attach((V) this);
    }

    /**
     * 实例化控制器
     */
    protected abstract P initPresenter();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
        if (mPresenter != null && mPresenter.isActive()) mPresenter.init();
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mContentView = view;
    }

    /**
     * 绘制对话框
     * 一般用于网络访问时显示(子类可重写，使用自定义对话框)
     *
     * @param message 提示的信息
     * @return Dialog 对话框
     */
    protected Dialog createLoadingDialog(@Nullable String message) {
        return new FDialog(this, message == null ? "" : message);
    }

    protected void updateLoadingDialog(@Nullable Dialog dialog, @Nullable String message) {
    }

    /**
     * 显示对话框
     */
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    public void showLoadingDialog(String message) {
        showLoadingDialog(message, true);
    }

    /**
     * 显示对话框 showLoadingDialog()和hideLoadingDialog()必须成对调用
     */
    public synchronized void showLoadingDialog(String message, boolean cancelable) {
        mLoadTimes++;
        if (mLoading == null) {
            mLoading = createLoadingDialog(message);
            mLoading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLoadTimes = 0;
                    mLoading = null;
                    updateLoadingDialog(null, null);
                }
            });
        } else {
            updateLoadingDialog(mLoading, message);
        }
        mLoading.setCancelable(cancelable);
        if (!mLoading.isShowing()) mLoading.show();
    }

    /**
     * 隐藏对话框
     */
    public synchronized void hideLoadingDialog() {
        mLoadTimes = Math.max(0, mLoadTimes - 1);
        if (mLoadTimes > 0) return;
        if (mLoading != null && mLoading.isShowing()) mLoading.dismiss();
    }

    public synchronized void cancelLoadingDialog() {
        mLoadTimes = 1;
        hideLoadingDialog();
    }


    /**
     * 拿到最新Activity
     *
     * @return BaseActivity
     */
    public static FDActivity getLatestActivity() {
        return ActivityPageManager.getInstance().currentActivity();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAll() {
        ActivityPageManager.getInstance().finishAllActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStarted = false;
    }

    public boolean isStarted() {
        return mStarted;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LoadTimes", mLoadTimes);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoadTimes = savedInstanceState.getInt("LoadTimes");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除Activity管理
        ActivityPageManager.getInstance().removeActivity(this);
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
        // 解绑控制器
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
        // 内容布局置空
        mContentView = null;
    }
}
