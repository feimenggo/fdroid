package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.FDActivity;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroid.widget.FDLoadingDialog;
import com.feimeng.fdroiddemo.R;

/**
 * Activity基类
 * Created by feimeng on 2016/3/18.
 */
public abstract class BaseActivity<V extends BaseView<D>, P extends BasePresenter<V, D>, D> extends FDActivity<V, P, D> {
    @Override
    protected Dialog createLoadingDialog(@Nullable String message) {
        return new FDLoadingDialog(this, R.style.DialogTransparent, message);
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
