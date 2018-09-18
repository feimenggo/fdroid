package com.feimeng.fdroid.mvp.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.feimeng.fdroid.base.FDActivity;
import com.feimeng.fdroid.base.FDFragment;
import com.feimeng.fdroid.mvp.model.api.WithoutNetworkException;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.NetworkUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 控制器基类
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDPresenter<V extends FDView> {
    protected V mView;// 视图

    /**
     * 绑定视图
     */
    public void attach(V view) {
        L.d("绑定视图->" + view);
        mView = view;
        preInit();
    }

    /**
     * 初始化，setContentView之前被调用
     */
    protected void preInit() {
    }

    /**
     * 初始化，setContentView之后被调用
     */
    public void init() {
    }

    /**
     * 解绑视图
     */
    public void detach() {
        L.d("解绑视图->" + mView);
        onDestroy();
        mView = null;
    }

    protected void onDestroy() {
    }

    /**
     * 是否绑定视图
     */
    public boolean isActive() {
        return mView != null;
    }

    /**
     * 得到上下文
     *
     * @return 当前Activity的Context
     */
    public Context getContext() {
        if (mView == null) return null;
        if (mView instanceof FDFragment) {
            return ((FDFragment) mView).getActivity().getApplicationContext();
        }
        return ((Context) mView).getApplicationContext();
    }

    /**
     * 得到Activity
     *
     * @return 当前Activity的Context
     */
    public FDActivity getActivity() {
        if (mView == null) return null;
        if (mView instanceof FDFragment) {
            return (FDActivity) ((FDFragment) mView).getActivity();
        }
        return (FDActivity) mView;
    }

    /**
     * 显示对话框
     */
    public void showDialog() {
        showDialog(null);
    }

    public void showDialog(String message) {
        showDialog(message, true);
    }

    public void showDialog(String message, boolean cancelable) {
        if (mView == null) return;
        if (mView instanceof FDActivity)
            ((FDActivity) mView).showLoadingDialog(message, cancelable);
        else if (mView instanceof FDFragment)
            ((FDFragment) mView).showLoadingDialog(message, cancelable);
    }

    /**
     * 隐藏对话框
     */
    public void hideDialog() {
        if (mView == null) return;
        if (mView instanceof FDActivity)
            ((FDActivity) mView).hideLoadingDialog();
        else if (mView instanceof FDFragment)
            ((FDFragment) mView).hideLoadingDialog();
    }

    /**
     * 保持与当前Activity生命周期同步，从而实现当前组件生命周期结束时，自动取消对Observable订阅
     */
    public <T> Observable<T> lifecycle(@NonNull Observable<T> observable) {
        if (mView == null) return observable;
        if (mView instanceof FDFragment) {
            return observable.compose(((FDFragment) mView).<T>bindToLifecycle());
        }
        return observable.compose(((FDActivity) mView).<T>bindToLifecycle());
    }

    /**
     * 指定在哪个生命周期方法调用时取消订阅 Activity
     */
    public <T> Observable<T> lifecycle(@NonNull Observable<T> observable, @NonNull ActivityEvent event) {
        if (mView != null && mView instanceof FDActivity) {
            return observable.compose(((FDActivity) mView).<T>bindUntilEvent(event));
        }
        return observable;
    }

    /**
     * 指定在哪个生命周期方法调用时取消订阅 Fragment
     */
    public <T> Observable<T> lifecycle(@NonNull Observable<T> observable, @NonNull FragmentEvent event) {
        if (mView != null && mView instanceof FDFragment) {
            return observable.compose(((FDFragment) mView).<T>bindUntilEvent(event));
        }
        return observable;
    }

    public interface OnWithoutNetwork {
        /**
         * 网络不可用
         *
         * @param data 可传递的数据
         */
        void withoutNetwork(Object data);
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public <T> Observable<T> withNet(Observable<T> task) {
        return withNet(task, false);
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public <T> Observable<T> withNet(Observable<T> task, final Object data) {
        final Observable<T> checkNet = Observable.create(new ObservableOnSubscribe<T>() {
            @SuppressLint("MissingPermission")
            @Override
            public void subscribe(ObservableEmitter<T> emitter) {
                if (!NetworkUtil.isConnectingToInternet(getContext())) {
                    if (isActive() && mView instanceof OnWithoutNetwork) {
                        ((OnWithoutNetwork) mView).withoutNetwork(data);
                    }
                    emitter.onError(new WithoutNetworkException());
                    return;
                }
                emitter.onComplete();
            }
        });
        return Observable.concat(checkNet, task).firstElement().toObservable();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public <T> Observable<T> withNet(Observable<T> task, final OnWithoutNetwork network) {
        return withNet(task, network, null);
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public <T> Observable<T> withNet(Observable<T> task, final OnWithoutNetwork network, final Object data) {
        final Observable<T> checkNet = Observable.create(new ObservableOnSubscribe<T>() {
            @SuppressLint("MissingPermission")
            @Override
            public void subscribe(ObservableEmitter<T> emitter) {
                if (!NetworkUtil.isConnectingToInternet(getContext())) {
                    if (network != null) network.withoutNetwork(data);
                    emitter.onError(new WithoutNetworkException());
                    return;
                }
                emitter.onComplete();
            }
        });
        return Observable.concat(checkNet, task).firstElement().toObservable();
    }
}
