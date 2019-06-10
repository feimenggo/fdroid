package com.feimeng.fdroiddemo.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.base.FDActivity;
import com.feimeng.fdroid.widget.LoadingDialog;
import com.feimeng.fdroiddemo.R;

public class LoginActivity extends FDActivity<LoginContract.View, LoginContract.Presenter> implements LoginContract.View, View.OnClickListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login).setOnClickListener(this);
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void onClick(View v) {
        mPresenter.login();
    }

    @Override
    protected void updateLoadingDialog(@Nullable Dialog dialog, @Nullable String message) {
        if (dialog != null) {
            if (dialog instanceof LoadingDialog) {
                ((LoadingDialog) dialog).setMessage(message);
            }
        }
    }
}