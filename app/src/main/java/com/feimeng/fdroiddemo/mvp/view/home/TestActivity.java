package com.feimeng.fdroiddemo.mvp.view.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseActivity;
import com.feimeng.fdroiddemo.mvp.presenter.home.TestContract;
import com.feimeng.fdroiddemo.mvp.presenter.home.TestPresenter;
import com.feimeng.fdroiddemo.util.Views;

public class TestActivity extends BaseActivity<TestContract.View, TestContract.Presenter, Object> implements TestContract.View, View.OnClickListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    @Override
    protected TestContract.Presenter initPresenter() {
        return new TestPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void getUserName(String username, String error) {
        // 步骤三
        T.showS(this, "用户名是" + username);
    }

    @Override
    public void onClick(View view) {
        if (Views.isFastDoubleClick(view)) return;
        // 调用Presenter层，获取用户名
        mPresenter.getUserName();
    }
}