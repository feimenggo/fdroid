package com.feimeng.fdroiddemo.base;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.feimeng.fdroid.mvp.FDLazyFragment;
import com.feimeng.fdroid.mvp.FDView;
import com.feimeng.fdroid.utils.FastTask;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.mvp.view.common.ConfirmDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle3.android.FragmentEvent;

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

    /**
     * 显示成功提示
     *
     * @param info 提示内容
     */
    public void showSuccess(String info) {
        FragmentActivity activity = requireActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).showSuccess(info);
        } else {
            T.showS(requireContext(), info);
        }
    }

    /**
     * 显示警告提示
     *
     * @param info 提示内容
     */
    public void showWarning(String info) {
        T.showS(requireContext(), info);
    }

    /**
     * 显示错误提示
     *
     * @param error 提示内容
     * @return 是否拦截后续操作
     */
    public boolean showError(String error) {
        FragmentActivity activity = requireActivity();
        if (activity instanceof BaseActivity) {
            return ((BaseActivity) activity).showError(error);
        }
        if (error != null) {
            T.showL(requireContext(), error);
            return true;
        }
        return false;
    }

    @Override
    public void init(@Nullable Object initData, @Nullable Throwable e) {
    }

    private Runnable mPermission;

    public void requestPermission(String title, String content, String tag, boolean force, String... permissions) {
        new RxPermissions(this).request(permissions).compose(bindUntilEvent(FragmentEvent.DESTROY)).subscribe(new FastTask.Result<Boolean>() {
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
                intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                startActivityForResult(intent, 9999);
            } else {
                deniedPermission(tag);
            }
        });
        confirm.showOne(getChildFragmentManager(), tag);
    }

    public void grantedPermission(String tag) {
    }

    public void deniedPermission(String tag) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPermission != null && requestCode == 9999) {
            mPermission.run();
        }
    }
}
