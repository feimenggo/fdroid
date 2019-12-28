package com.feimeng.fdroiddemo.mvp.view.gate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseActivity;
import com.feimeng.fdroiddemo.mvp.presenter.gate.LoginContract;
import com.feimeng.fdroiddemo.mvp.presenter.gate.LoginPresenter;
import com.feimeng.fdroiddemo.util.Views;

public class LoginActivity extends BaseActivity<LoginContract.View, LoginContract.Presenter, String> implements LoginContract.View, View.OnClickListener {
    private TextView mInfo;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter().asyncInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        findViewById(R.id.loginSync).setOnClickListener(this);
        findViewById(R.id.loginAsync).setOnClickListener(this);
        mInfo = findViewById(R.id.info);
    }

    @Override
    public void init(String initData, Throwable e) {
        T.showL(this, initData);
    }

    @Override
    public void login(String result, String error) {
        if (showError(error)) return;
        mInfo.setText(result);
    }

    @Override
    public void onClick(View view) {
        if (Views.isFastDoubleClick(view)) return;
        switch (view.getId()) {
            case R.id.loginSync: // 发起同步请求
                mPresenter.loginBySync("xxx", "xxx");
                break;
            case R.id.loginAsync: // 发起异步请求
                mPresenter.loginByAsync("xxx", "xxx");
                break;
        }
        mInfo.setText(null);
    }
}