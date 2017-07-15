package com.feimeng.fdroiddemo.login;

import android.os.Bundle;

import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroid.base.FDActivity;

public class LoginActivity extends FDActivity<LoginContract.View, LoginContract.Presenter> implements LoginContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter();
    }
}