package com.feimeng.fdroiddemo.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.feimeng.fdroid.mvp.FDActivity;
import com.feimeng.fdroiddemo.R;

public class TestActivity extends FDActivity<TestContract.View, TestContract.Presenter> implements TestContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // 调用Presenter层，获取用户名
        mPresenter.getUserName();
    }

    @Override
    protected TestContract.Presenter initPresenter() {
        return new TestPresenter();
    }

    @Override
    public void getUserName(String username) {
        // 步骤三
        Toast.makeText(this, "用户名是" + username, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void init(@Nullable Object initData, @Nullable Throwable e) {

    }
}