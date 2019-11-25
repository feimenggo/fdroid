package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.view.Window;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.FDLazyFragment;
import com.feimeng.fdroid.mvp.FDView;
import com.feimeng.fdroiddemo.R;

/**
 * Fragment基类
 * Created by feimeng on 2016/3/18.
 */
public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends FDLazyFragment<V, P> implements FDView {
    @Override
    protected Dialog createLoadingDialog(String message) {
        Dialog dialog = new Dialog(requireActivity(), R.style.DialogTransparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void init(@Nullable Object initData, @Nullable Throwable e) {
    }
}
