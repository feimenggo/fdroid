package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.FDActivity;
import com.feimeng.fdroid.mvp.FDView;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.R;

/**
 * Activity基类
 * Created by feimeng on 2016/3/18.
 */
public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends FDActivity<V, P> implements FDView {
    @Override
    protected Dialog createLoadingDialog(String message) {
        Dialog dialog = new Dialog(this, R.style.DialogTransparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getClass().getName().contains("SplashActivity")))
            BaseApp.getInstance().waitCoreThread();
    }

    @Override
    public void setContentView(int layoutResID) {
        preInitView();
        super.setContentView(layoutResID);
        initView(); // 初始化页面
    }

    @Override
    public void setContentView(View view) {
        preInitView();
        super.setContentView(view);
        initView(); // 初始化页面
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        preInitView();
        super.setContentView(view, params);
        initView(); // 初始化页面
    }

    protected void preInitView() {
    }

    /**
     * 初始化界面控件
     * 子类自己实现
     * 注意：
     * 1、不需要自己调用initView()，因为调用setContentView()时会自动调用initView;
     * 2、initView()里使用的对象，需要保证在setContentView()调用前已经实例化;
     */
    protected abstract void initView();

    @Override
    public void init(@Nullable Object initData, @Nullable Throwable e) {
    }

    /**
     * 显示错误提示
     *
     * @param error 提示内容
     * @return 是否拦截后续操作
     */
    public boolean showError(String error) {
        if (error != null) {
            T.showS(this, error);
            return true;
        }
        return false;
    }
}
