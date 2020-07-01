package com.feimeng.fdroid.mvp;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.FragmentActivity;

import com.feimeng.fdroid.mvp.model.api.WithoutNetworkException;
import com.feimeng.fdroid.utils.FastTask;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.NetworkUtil;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 控制器基类
 * Created by feimeng on 2017/1/20.
 *
 * @param <V> 视图
 * @param <D> 初始化结果
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class FDPresenter<V extends FDView<D>, D> {
    private boolean mInitAsync; // 异步初始化
    protected V mView;// 视图

    /**
     * 开启异步初始化
     * 开启后，{@link #onInit(boolean)}将会在子线程回调，否则在UI线程回调。
     */
    public <P extends FDPresenter> P asyncInit() {
        mInitAsync = true;
        return (P) this;
    }

    /**
     * 绑定视图
     */
    void attach(V view) {
        L.v("绑定视图->" + view);
        mView = view;
        onAttach();
    }

    /**
     * 初始化，setContentView之前被调用
     */
    protected void onAttach() {
    }

    /**
     * 初始化，setContentView之后被调用
     */
    void afterContentView() {
        if (mInitAsync) {
            new FastTask<D>() {
                @Override
                public D task() throws Exception {
                    return onInit(true);
                }
            }.runIO(new FastTask.Result<D>() {
                @Override
                public void success(D initData) {
                    if (isActive()) postInitView(initData, null);
                }

                @Override
                public void fail(Throwable error, String info) {
                    if (isActive()) postInitView(null, error);
                }
            }, mView);
        } else {
            try {
                postInitView(onInit(false), null);
            } catch (Throwable throwable) {
                postInitView(null, throwable);
            }
        }
    }

    /**
     * 执行视图初始化
     *
     * @param initData  初始化的结果
     * @param throwable 初始化执行时抛出的异常
     */
    protected void postInitView(@Nullable final D initData, @Nullable final Throwable throwable) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.init(initData, throwable);
            }
        });
    }

    /**
     * 此方法在setContentView()之后回调
     *
     * @param initAsync 是否为异步回调，在初始化控制器时调用{{@link #asyncInit()}开启
     * @return 结果传给{@link FDView#onInit(Object,Throwable)}的第一个参数
     * @throws Exception 初始化执行时抛出的异常
     */
    @Nullable
    protected D onInit(boolean initAsync) throws Exception {
        return null;
    }

    /**
     * 解绑视图
     */
    void detach() {
        L.v("解绑视图->" + mView);
        mView = null;
        onDetach();
    }

    protected void onDetach() {
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
    @NonNull
    public Context getContext() {
        if (mView instanceof FDFragment) {
            FragmentActivity activity = ((FDFragment) mView).getActivity();
            if (activity != null) return activity;
        } else if (mView instanceof FDDialog) {
            FragmentActivity activity = ((FDDialog) mView).getActivity();
            if (activity != null) return activity;
        } else if (mView instanceof FDActivity) {
            return (Context) mView;
        }
        return FDCore.getApplication().getApplicationContext();
    }

    /**
     * 得到Activity
     *
     * @return 当前Activity的Context
     */
    @Nullable
    public FDActivity getActivity() {
        if (mView instanceof FDFragment) {
            return (FDActivity) ((FDFragment) mView).getActivity();
        } else if (mView instanceof FDDialog) {
            return (FDActivity) ((FDDialog) mView).getActivity();
        } else if (mView instanceof FDActivity) {
            return (FDActivity) mView;
        }
        return null;
    }

    @NonNull
    public FDActivity requireActivity() {
        FDActivity activity = getActivity();
        if (activity == null) {
            throw new IllegalStateException("Presenter " + this + " not attached to an activity.");
        }
        return activity;
    }

    /**
     * 显示对话框
     */
    public void showDialog() {
        showDialog(null, true);
    }

    public void showDialog(String message) {
        showDialog(message, true);
    }

    public void showDialog(boolean cancelable) {
        showDialog(null, cancelable);
    }

    public void showDialog(String message, boolean cancelable) {
        if (mView == null) return;
        if (mView instanceof FDActivity) {
            ((FDActivity) mView).showLoadingDialog(message, cancelable);
        } else if (mView instanceof FDFragment) {
            ((FDFragment) mView).showLoadingDialog(message, cancelable);
        } else if (mView instanceof FDDialog) {
            ((FDDialog) mView).showLoadingDialog(message, cancelable);
        }
    }

    /**
     * 隐藏对话框
     */
    public void hideDialog() {
        if (mView == null) return;
        if (mView instanceof FDActivity) {
            ((FDActivity) mView).hideLoadingDialog();
        } else if (mView instanceof FDFragment) {
            ((FDFragment) mView).hideLoadingDialog();
        } else if (mView instanceof FDDialog) {
            ((FDDialog) mView).hideLoadingDialog();
        }
    }

    /**
     * 当对话框消失的时候被回调
     */
    public void onDialogDismiss() {
    }

    /**
     * 保持与当前Activity生命周期同步，从而实现当前组件生命周期结束时，自动取消对Observable订阅
     */
    public <T> Observable<T> lifecycle(@NonNull Observable<T> observable) {
        if (mView == null) return observable;
        if (mView instanceof FDFragment) {
            return observable.compose(((FDFragment) mView).<T>bindToLifecycle());
        } else if (mView instanceof FDDialog) {
            return observable.compose(((FDDialog) mView).<T>bindToLifecycle());
        }
        return observable.compose(((FDActivity) mView).<T>bindToLifecycle());
    }

    /**
     * 指定在哪个生命周期方法调用时取消订阅 Activity
     */
    public <T> Observable<T> lifecycle(@NonNull Observable<T> observable, @NonNull ActivityEvent event) {
        if (mView instanceof FDActivity) {
            return observable.compose(((FDActivity) mView).<T>bindUntilEvent(event));
        }
        return observable;
    }

    /**
     * 指定在哪个生命周期方法调用时取消订阅 Fragment/Dialog
     */
    public <T> Observable<T> lifecycle(@NonNull Observable<T> observable, @NonNull FragmentEvent event) {
        if (mView instanceof FDFragment) {
            return observable.compose(((FDFragment) mView).<T>bindUntilEvent(event));
        } else if (mView instanceof FDDialog) {
            return observable.compose(((FDDialog) mView).<T>bindUntilEvent(event));
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
