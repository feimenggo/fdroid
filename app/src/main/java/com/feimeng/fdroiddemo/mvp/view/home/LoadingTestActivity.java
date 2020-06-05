package com.feimeng.fdroiddemo.mvp.view.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseActivity;

public class LoadingTestActivity extends BaseActivity implements View.OnClickListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoadingTestActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_test);
    }

    @Override
    protected void initView() {
        findViewById(R.id.loading300).setOnClickListener(this);
        findViewById(R.id.loading600).setOnClickListener(this);
    }

    @Override
    protected FDPresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loading300:
                showLoadingDialog("测试300MS");
                v.postDelayed(this::hideLoadingDialog, 300);
                break;
            case R.id.loading600:
                showLoadingDialog("测试600MS");
                v.postDelayed(this::hideLoadingDialog, 600);
                break;
        }
    }
}
