package com.feimeng.fdroiddemo.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.feimeng.fdroid.base.FDActivity;
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
}