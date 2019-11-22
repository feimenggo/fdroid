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
public class PromptDialog extends BaseDialog implements View.OnClickListener {
    private static final String PARAM_TITLE = "t";
    private static final String PARAM_CONTENT = "c";
    private static final String PARAM_BTN = "b";
    private Callback mCallback;
    private boolean mSavable = true;

    public static PromptDialog newInstance(String title, String content) {
        return newInstance(title, content, "知道了", true);
    }

    public static PromptDialog newInstance(String title, String content, String btn, boolean cancelable) {
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putString(PARAM_CONTENT, content);
        args.putString(PARAM_BTN, btn);
        PromptDialog fragment = new PromptDialog(cancelable);
        fragment.setArguments(args);
        return fragment;
    }

    public PromptDialog(boolean cancelable) {
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
        return R.layout.dialog_prompt;
    }

    @Override
    public void initView(Context context, View view) {
        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        TextView btn = view.findViewById(R.id.ok);
        btn.setOnClickListener(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            title.setText(arguments.getString(PARAM_TITLE));
            content.setText(arguments.getString(PARAM_CONTENT));
            btn.setText(arguments.getString(PARAM_BTN));
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        if (Views.isFastDoubleClick(v)) return;
        if (mCallback != null) mCallback.onPrompt(getTag());
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallback = null;
    }

    public interface Callback {
        void onPrompt(String tag);
    }
}