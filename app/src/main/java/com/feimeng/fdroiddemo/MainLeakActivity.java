package com.feimeng.fdroiddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.feimeng.fdroid.mvp.FDActivity;
import com.feimeng.fdroid.mvp.FDPresenter;

public class MainLeakActivity extends FDActivity {
    public static final String TAG = MainLeakActivity.class.getName();
    private Handler mHandler = new Handler();
    private TextView mTextView;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainLeakActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_leak);
        mTextView = findViewById(R.id.text); // 模拟内存泄露
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("啦啦啦");
            }
        }, 3 * 60 * 1000);
        finish();
    }

    @Override
    protected FDPresenter initPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
