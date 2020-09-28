package com.feimeng.fdroid.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.support.RxDialogFragment;

/**
 * Author: Feimeng
 * Time:   2018/11/3 10:43
 * Description: DialogFragment基类
 */
public abstract class FDDialog<V extends FDView<D>, P extends FDPresenter<V, D>, D> extends RxDialogFragment implements FDView<D> {
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
        if (mPresenter != null) {
            if (mPresenter.isActive()) mPresenter.afterContentView();
        } else {
            view.post(new Runnable() {
                @Override
                public void run() {
                    init(null, null);
                }
            });
        }
    }

    @Override
    public void init(D initData, Throwable e) {
    }

    /**
     * 获取关联的RxLifecycle
     */
    public <T> LifecycleTransformer<T> getLifecycle(@NonNull FragmentEvent event) {
        return bindUntilEvent(event);
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
    @SuppressWarnings("rawtypes")
    public synchronized void showLoadingDialog(String message, boolean cancelable) {
        FragmentActivity activity = getActivity();
        if (activity instanceof FDActivity) {
            ((FDActivity) activity).showLoadingDialog(message, cancelable);
        }
    }

    /**
     * 隐藏对话框
     */
    @SuppressWarnings("rawtypes")
    public synchronized void hideLoadingDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof FDActivity) {
            ((FDActivity) activity).hideLoadingDialog();
        }
    }

    /**
     * 取消对话框
     */
    @SuppressWarnings("rawtypes")
    public synchronized void cancelLoadingDialog() {
        FragmentActivity activity = getActivity();
        if (activity instanceof FDActivity) {
            ((FDActivity) activity).cancelLoadingDialog();
        }
    }

    @Override
    public void onDestroy() {
        // 解绑控制器
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
        super.onDestroy();
    }
}
