package com.feimeng.fdroid.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.base.FDPresenter;
import com.feimeng.fdroid.mvp.base.FDView;
import com.feimeng.fdroid.widget.LoadingDialog;
import com.trello.rxlifecycle3.components.support.RxFragment;

/**
 * Fragment基类
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDFragment<V extends FDView, P extends FDPresenter<V>> extends RxFragment implements DialogInterface.OnDismissListener {
    protected P mPresenter;

    /**
     * 对话框
     */
    private Dialog mLoading;
    private int mLoadTimes = 0; // 加载次数

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定控制器
        mPresenter = initPresenter();
        if (mPresenter != null && this instanceof FDView) {
            mPresenter.attach((V) this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null && mPresenter.isActive()) mPresenter.init();
    }

    /**
     * 实例化presenter
     */
    public abstract P initPresenter();

    /**
     * 绘制对话框
     * 一般用于网络访问时显示(子类可重写，使用自定义对话框)
     *
     * @param message 提示的信息
     * @return Dialog 对话框
     */
    protected Dialog createLoadingDialog(String message) {
        return new LoadingDialog(getActivity(), message == null ? "" : message);
    }

    protected void updateLoadingDialog(@Nullable Dialog dialog, @Nullable String message) {
    }

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
            mLoading.setOnDismissListener(this);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LoadTimes", mLoadTimes);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) mLoadTimes = savedInstanceState.getInt("LoadTimes");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mLoadTimes = 0;
        if (mPresenter != null) mPresenter.onDialogDismiss();
        updateLoadingDialog(null, null);
    }

    @Override
    public void onDestroy() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
        super.onDestroy();
        // 解绑控制器
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
    }
}
