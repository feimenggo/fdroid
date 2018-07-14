package com.feimeng.fdroiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.feimeng.fdroid.mvp.model.api.bean.ApiFinish2;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.api.ApiWrapper;
import com.feimeng.fdroiddemo.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.crash).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
    }

    TextView a;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crash:
                a.setText("");
                break;
            case R.id.login:
                LoginActivity.start(this);
                break;
            case R.id.register:
                register();
                break;
        }
    }

    private void login() {
        ApiWrapper.getInstance().login("10086", "123456")
                .subscribe(ApiWrapper.subscriber(new ApiFinish2<Integer>() {
                    @Override
                    public void success(Integer data) {
                        T.showS(getApplicationContext(), "登录成功");
                        L.d(TAG, data);
                    }

                    @Override
                    public void fail(Throwable error, String info) {
                        T.showS(getApplicationContext(), "登录出错");
                        L.d(TAG, info);
                    }
                }));
    }

    private void register() {
        ApiWrapper.getInstance().register("10086", "123456")
                .subscribe(ApiWrapper.subscriber(new ApiFinish2<Object>() {
                    @Override
                    public void success(Object ignore) {
                        T.showS(getApplicationContext(), "注册成功");
                    }

                    @Override
                    public void fail(Throwable error, String info) {
                        T.showS(getApplicationContext(), "注册出错");
                        L.d(TAG, info);
                    }
                }));
    }
}
