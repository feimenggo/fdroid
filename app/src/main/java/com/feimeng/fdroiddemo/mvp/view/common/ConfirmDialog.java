package com.feimeng.fdroiddemo.mvp.view.common;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseDialog;
import com.feimeng.fdroiddemo.util.Views;

/**
 * Author: Feimeng
 * Time:   2018/9/18 20:18
 * Description: 提示框
 */
public class ConfirmDialog extends BaseDialog implements View.OnClickListener {
    private static final String PARAM_TITLE = "t";
    private static final String PARAM_CONTENT = "c";
    private static final String PARAM_BTN_LEFT = "l";
    private static final String PARAM_BTN_RIGHT = "r";
    private Callback mCallback;
    private boolean mSavable = true;

    public static ConfirmDialog newInstance(String title, String content) {
        return newInstance(title, content, "取消", "确定", true);
    }

    public static ConfirmDialog newInstance(String title, String content, String btnLeft, String btnRight, boolean cancelable) {
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putString(PARAM_CONTENT, content);
        args.putString(PARAM_BTN_LEFT, btnLeft);
        args.putString(PARAM_BTN_RIGHT, btnRight);
        ConfirmDialog fragment = new ConfirmDialog(cancelable);
        fragment.setArguments(args);
        return fragment;
    }

    public ConfirmDialog(boolean cancelable) {
        super(Gravity.CENTER_HORIZONTAL, false, cancelable);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mSavable && savedInstanceState != null) dismissAllowingStateLoss();
    }

    public void setCallback(Callback callback) {
        mSavable = false;
        mCallback = callback;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mCallback == null) {
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof Callback) {
                mCallback = (Callback) parentFragment;
            } else if (context instanceof Callback) {
                mCallback = (Callback) context;
            }
        }
    }

    @Override
    public int layoutResId() {
        return R.layout.dialog_confirm;
    }

    @Override
    public void initView(Context context, View view) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            dismissAllowingStateLoss();
            return;
        }
        view.findViewById(R.id.close).setOnClickListener(this);
        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        TextView btnLeft = view.findViewById(R.id.cancel);
        btnLeft.setOnClickListener(this);
        TextView btnRight = view.findViewById(R.id.ok);
        btnRight.setOnClickListener(this);

        title.setText(arguments.getString(PARAM_TITLE));
        content.setText(arguments.getString(PARAM_CONTENT));
        btnLeft.setText(arguments.getString(PARAM_BTN_LEFT));
        btnRight.setText(arguments.getString(PARAM_BTN_RIGHT));
    }

    @Override
    public void onClick(View v) {
        if (Views.isFastDoubleClick(v)) return;
        if (mCallback != null) {
            switch (v.getId()) {
                case R.id.ok:
                    mCallback.onConfirm(getTag(), true);
                    break;
                case R.id.cancel:
                    mCallback.onConfirm(getTag(), false);
                    break;
            }
        }
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallback = null;
    }

    public interface Callback {
        void onConfirm(String tag, boolean clickBtnRight);
    }
}
