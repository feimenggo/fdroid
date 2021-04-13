package com.feimeng.fdroiddemo.mvp.view.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseActivity;
import com.feimeng.fdroiddemo.base.BasePresenter;
import com.feimeng.fdroiddemo.base.BaseView;
import com.feimeng.fdroiddemo.mvp.view.gate.LoginActivity;

public class MainActivity extends BaseActivity<BaseView<Object>, BasePresenter<BaseView<Object>, Object>, Object> implements View.OnClickListener {
    @Override
    protected BasePresenter<BaseView<Object>, Object> initPresenter() {
        return new BasePresenter<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateActivity(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.mvp).setOnClickListener(this);
        findViewById(R.id.loading).setOnClickListener(this);
        findViewById(R.id.rxBus).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LoginActivity.start(this);
                break;
            case R.id.mvp:
                MvpSampleActivity.start(this);
                break;
            case R.id.loading:
                LoadingTestActivity.start(this);
                break;
            case R.id.rxBus:
                RxBusTestActivity.start(this);
                break;
        }
    }
}
