package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.view.Window;

import com.feimeng.fdroid.mvp.FDLazyFragment;
import com.feimeng.fdroiddemo.R;

/**
 * Fragment基类
 * Created by feimeng on 2016/3/18.
 */
public abstract class BaseFragment<V extends BaseView<D>, P extends BasePresenter<V, D>, D> extends FDLazyFragment<V, P, D> {
    @Override
    protected Dialog createLoadingDialog(String message) {
        Dialog dialog = new Dialog(requireActivity(), R.style.DialogTransparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }
}
