package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.feimeng.fdroid.mvp.FDActivity;
import com.feimeng.fdroid.mvp.FDView;
import com.feimeng.fdroid.utils.FastTask;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.mvp.view.common.ConfirmDialog;
import com.feimeng.fdroiddemo.mvp.view.common.PromptDialog;
import com.feimeng.fdroiddemo.util.Views;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle3.android.ActivityEvent;

/**
 * Activity基类
 * Created by feimeng on 2016/3/18.
 */
public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>> extends FDActivity<V, P> implements FDView {
    protected Integer statusBarColor; // 状态栏颜色
    protected Boolean statusBarDarkMode = true; // 状态栏深色模式

    public void setContentView(@LayoutRes int layoutResID, @ColorInt int bgColor) {
        setBackgroundColor(bgColor);
        setContentView(layoutResID);
    }

    /**
     * 设置Activity背景颜色
     *
     * @param color 颜色值
     */
    public void setBackgroundColor(@ColorInt int color) {
        getWindow().getDecorView().setBackgroundColor(color);
    }

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
     * 显示成功提示
     *
     * @param info 提示内容
     */
    public void showSuccess(String info) {
        Toast toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.view_toast, null);
        TextView msg = (TextView) viewGroup.getChildAt(0);
        Views.drawableTop(msg, R.drawable.icon_toast_success);
        msg.setText(info);
        toast.setView(viewGroup);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示警告提示
     *
     * @param info 提示内容
     */
    public void showWarning(String info) {
        T.showS(this, info);
    }

    /**
     * 显示错误提示
     *
     * @param error 提示内容
     * @return 是否拦截后续操作
     */
    public boolean showError(String error) {
        if (error != null) {
            Toast toast = Toast.makeText(this, error, Toast.LENGTH_SHORT);
            ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.view_toast, null);
            TextView msg = (TextView) viewGroup.getChildAt(0);
            Views.drawableTop(msg, R.drawable.icon_toast_error);
            msg.setText(error);
            toast.setView(viewGroup);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return !isDestroyed();
    }

    public void prompt(String content) {
        PromptDialog.newInstance("温馨提示", content).show(getSupportFragmentManager(), "prompt");
    }

    private Runnable mPermission;

    public void requestPermission(String title, String content, String tag, boolean force, String... permissions) {
        new RxPermissions(this).request(permissions).compose(bindUntilEvent(ActivityEvent.DESTROY)).subscribe(new FastTask.Result<Boolean>() {
            @Override
            public void success(Boolean granted) {
                if (granted) {
                    grantedPermission(tag);
                } else {
                    needPermission(title, content, tag, force, permissions);
                }
            }
        });
    }

    public void needPermission(String title, String content, String tag, boolean force, String... permissions) {
        ConfirmDialog confirm = ConfirmDialog.newInstance(title, content, "取消", "好的", !force);
        confirm.setCallback((t, r) -> {
            if (r) {
                mPermission = () -> {
                    requestPermission(title, content, tag, force, permissions);
                    mPermission = null;
                };
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(intent, 9999);
            } else {
                deniedPermission(tag);
            }
        });
        confirm.show(getSupportFragmentManager(), tag);
    }

    public void grantedPermission(String tag) {
    }

    public void deniedPermission(String tag) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPermission != null && requestCode == 9999) {
            mPermission.run();
        }
    }
}
