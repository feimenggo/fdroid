package com.feimeng.fdroiddemo.base;


import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.FDPresenter;
import com.feimeng.fdroiddemo.data.doo.NetworkError;

/**
 * Author: Feimeng
 * Time:   2018/6/11 13:40
 * Description: 控制器基类
 */
public class BasePresenter<V extends BaseView<D>, D> extends FDPresenter<V, D> implements FDPresenter.OnWithoutNetwork {
    @Override
    public void withoutNetwork(Object o) {
        NetworkError.show(getActivity());
    }

    @Nullable
    @Override
    protected D onInit(boolean initAsync) throws Exception {
        init();
        return super.onInit(initAsync);
    }

    public void init() {
    }
}
