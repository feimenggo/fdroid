package com.feimeng.fdroid.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * 默认的对话框
 * Created by feimeng on 2017/2/13.
 */
public class LoadingDialog extends AlertDialog {
    private String message;

    public LoadingDialog(Context context, String message) {
        super(context);
        this.message = message;
        init(context);
    }

    private void init(Context context) {
        setMessage(message);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
