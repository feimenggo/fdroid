package com.feimeng.fdroid.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feimeng.fdroid.mvp.base.FDPresenter;
import com.feimeng.fdroid.mvp.base.FDView;

/**
 * Fragment基类 rootView会被持久化保存。切换时Fragment不会重复创建rootView
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDFragmentReuse<V extends FDView, P extends FDPresenter<V>> extends FDFragment<V, P> {
    protected abstract View bindView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    protected abstract void loadData(@Nullable Bundle savedInstanceState);

    protected View rootView;// 缓存Fragment view
    private boolean isViewCreated, isActivityCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = bindView(inflater, container, savedInstanceState);
        }
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isViewCreated) {
            isViewCreated = true;
            initView(view, savedInstanceState);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isActivityCreated) {
            isActivityCreated = true;
            loadData(savedInstanceState);
        }
    }
}
