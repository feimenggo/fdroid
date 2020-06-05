package com.feimeng.fdroiddemo.base;

import com.feimeng.fdroid.mvp.FDLazyFragment;

/**
 * Fragment基类
 * Created by feimeng on 2016/3/18.
 */
public abstract class BaseFragment<V extends BaseView<D>, P extends BasePresenter<V, D>, D> extends FDLazyFragment<V, P, D> {
}
