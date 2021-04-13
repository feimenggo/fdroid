package com.feimeng.fdroiddemo.mvp.view.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.RxBus;
import com.feimeng.fdroiddemo.R;
import com.feimeng.fdroiddemo.base.BaseActivity;
import com.feimeng.fdroiddemo.base.BasePresenter;
import com.feimeng.fdroiddemo.base.BaseView;
import com.trello.rxlifecycle3.android.ActivityEvent;

import io.reactivex.schedulers.Schedulers;

public class RxBusTestActivity extends BaseActivity<BaseView<Object>, BasePresenter<BaseView<Object>, Object>, Object> implements View.OnClickListener {
    private int mCount = 1;
    private RxBus.BindEvent<Integer> mBindEvent;

    public static void start(Context context) {
        context.startActivity(new Intent(context, RxBusTestActivity.class));
    }

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
        setContentView(R.layout.activity_rxbus_test);
    }

    @Override
    protected void initView() {
        findViewById(R.id.bind).setOnClickListener(this);
        findViewById(R.id.post).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind:
                if (mBindEvent != null) mBindEvent.dispose();
                mBindEvent = new RxBus.BindEvent<Integer>(Integer.class, mPresenter.getLifecycle(ActivityEvent.STOP), Schedulers.newThread()) {
                    @Override
                    public void accept(Integer event) throws Exception {
                        L.d("nodawang", "accept:" + event + " thread:" + Thread.currentThread());
                        if (event == 5 || event == 10) {
                            L.d("nodawang", "accept:模拟订阅事件异常");
                            throw new Exception("模拟订阅事件异常");
                        }
                    }

                    @Override
                    protected boolean onError(Throwable throwable) {
                        throwable.printStackTrace();
                        return true;
                    }
                }.subscribe();
                break;
            case R.id.post:
                if (RxBus.get().hasBinder()) {
                    L.d("nodawang", "post:" + mCount);
                    RxBus.get().post(mCount);
                    mCount++;
                } else {
                    L.d("nodawang", "没有订阅者");
                }
                break;
        }
    }
}
