package com.feimeng.fdroiddemo.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.feimeng.fdroid.mvp.FDLazyFragment;
import com.feimeng.fdroid.mvp.FDPresenter;

/**
 * Author: Feimeng
 * Time:   2018/4/25 21:24
 * Description:
 */
public class TestFragment extends FDLazyFragment {
    @Override
    public FDPresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected void initView(View rootView, @Nullable Bundle savedInstanceState) {

    }
}
