package com.feimeng.fdroiddemo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.feimeng.fdroid.base.FDActivity;
import com.feimeng.fdroid.bean.Ignore;
import com.feimeng.fdroid.exception.ApiException;
import com.feimeng.fdroid.mvp.model.api.bean.ApiFinish2;
import com.feimeng.fdroid.mvp.model.api.bean.Optional;
import com.feimeng.fdroid.utils.FastTask;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.RxJavas;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroid.widget.LoadingDialog;
import com.feimeng.fdroiddemo.api.ApiWrapper;
import com.feimeng.fdroiddemo.login.LoginActivity;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class MainActivity extends FDActivity<MainContract.View, MainContract.Presenter> implements MainContract.View, View.OnClickListener {
    public static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.getUserInfo).setOnClickListener(this);
        findViewById(R.id.showLoading).setOnClickListener(this);
        findViewById(R.id.hideLoading).setOnClickListener(this);
    }

    @Override
    protected Dialog createLoadingDialog(@Nullable String message) {
        return new LoadingDialog(this, message == null ? "" : message);
    }

    @Override
    protected void updateLoadingDialog(@Nullable Dialog dialog, @Nullable String message) {
        if (dialog != null) ((LoadingDialog) dialog).setMessage(message);
    }

    @Override
    protected MainContract.Presenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LoginActivity.start(this);
                break;
            case R.id.register:
                testChoose();
//                register();
                break;
            case R.id.getUserInfo:
                getUserInfoSync();
                break;
            case R.id.showLoading:
//                new FastTask<Ignore>() {
//                    @Override
//                    public Ignore task() throws Exception {
//                        Thread.sleep(2000);
//                        return Ignore.instance;
//                    }
//                }.runIO(new FastTask.Result<Ignore>() {
//                    @Override
//                    public void success(Ignore ignore) {
//                        L.d("nodawang", "hello");
//                    }
//                }, this);
                showLoadingDialog("拼命加载中...", true);
//                showLoadingDialog("拼命加载中2...");
                break;
            case R.id.hideLoading:
                hideLoadingDialog();
                break;
        }
    }

    private void getUserInfoSync() {
        new FastTask<Ignore>() {
            @Override
            public Ignore task() throws Exception {
                try {
                    Integer aVoid = ApiWrapper.getInstance().getUserInfo("", "");
                    L.d("nodawang", aVoid);
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Ignore.instance;
            }
        }.runIO();
    }

    private void testChoose() {
        Optional<Void> con = Optional.empty();
        RxJavas.choose(con, ApiWrapper.getInstance().register("", ""))
                .flatMap(new Function<Optional<Void>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Optional<Void> optional) throws Exception {
                        return ApiWrapper.getInstance().login("10086", "123456");
                    }
                })
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
//        ApiWrapper.getInstance().register("10086", "123456")
//                .subscribe(ApiWrapper.subscriber(new ApiFinish0<Void>() {
//                    @Override
//                    public void start() {
//                    }
//
//                    @Override
//                    public void success(Void ignore) {
//                        T.showS(getApplicationContext(), "注册成功");
//                    }
//
//                    @Override
//                    public void fail(Throwable error, String info) {
//                        T.showS(getApplicationContext(), "注册出错");
//                        L.d(TAG, info);
//                    }
//                }));
    }
}
