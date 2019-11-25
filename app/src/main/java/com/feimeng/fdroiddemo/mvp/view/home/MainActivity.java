package com.feimeng.fdroiddemo.mvp.view.home;

import android.os.Bundle;
import android.view.View;

import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseActivity;
import com.feimeng.fdroiddemo.mvp.view.gate.LoginActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);
    }

    @Override
    protected FDPresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LoginActivity.start(this);
                break;
            case R.id.test:
                TestActivity.start(this);
                break;
        }
    }
}
