package com.feimeng.fdroid.mvp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feimeng.fdroid.widget.LoadingDialog;
import com.trello.rxlifecycle3.components.support.RxFragment;

/**
 * Fragment基类
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDFragment<V extends FDView<D>, P extends FDPresenter<V, D>, D> extends RxFragment implements FDView<D> {
    protected P mPresenter;

    /**
     * 对话框
     */
    private Dialog mLoading;
    private int mLoadCount = 0; // 加载次数

    /**
     * 实例化presenter
     */
    public abstract P initPresenter();

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定控制器
        if ((mPresenter = initPresenter()) != null) mPresenter.attach((V) this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null && mPresenter.isActive()) mPresenter.afterContentView();
    }

    @Override
    public void init(D initData, Throwable e) {

    }

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
        mLoadCount++;
        if (mLoading == null) {
            mLoading = createLoadingDialog(message);
            mLoading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mLoadCount = 0;
                    if (mPresenter != null) mPresenter.onDialogDismiss();
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
        mLoadCount = Math.max(0, mLoadCount - 1);
        if (mLoadCount > 0) return;
        if (mLoading != null) {
            if (mLoading.isShowing()) mLoading.dismiss();
            mLoading = null;
        }
    }

    public synchronized void cancelLoadingDialog() {
        mLoadCount = 1;
        hideLoadingDialog();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LoadTimes", mLoadCount);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) mLoadCount = savedInstanceState.getInt("LoadTimes");
    }

    @Override
    public void onDestroy() {
        if (mLoading != null) {
            if (mLoading.isShowing()) mLoading.dismiss();
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
