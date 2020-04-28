package com.feimeng.fdroid.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.trello.rxlifecycle3.components.support.RxFragment;

/**
 * Fragment基类
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDFragment<V extends FDView<D>, P extends FDPresenter<V, D>, D> extends RxFragment implements FDView<D> {
    protected P mPresenter;

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
        FragmentActivity activity = getActivity();
        if (activity instanceof FDActivity) {
            ((FDActivity) activity).showLoadingDialog(message, cancelable);
        }
    }

    /**
     * 隐藏对话框
     */
    public synchronized void hideLoadingDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof FDActivity) {
            ((FDActivity) activity).hideLoadingDialog();
        }
    }

    /**
     * 取消对话框
     */
    public synchronized void cancelLoadingDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof FDActivity) {
            ((FDActivity) activity).cancelLoadingDialog();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解绑控制器
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
    }
}
