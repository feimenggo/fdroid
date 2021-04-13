package com.feimeng.fdroid.mvp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feimeng.fdroid.R;
import com.feimeng.fdroid.bean.Ignore;
import com.feimeng.fdroid.utils.ActivityPageManager;
import com.feimeng.fdroid.utils.FastTask;
import com.feimeng.fdroid.widget.FDLoadingDialog;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

/**
 * Activity基类
 * Created by feimeng on 2017/1/20.
 *
 * @param <V> 视图
 * @param <P> 控制器
 * @param <D> 初始化结果
 */
public abstract class FDActivity<V extends FDView<D>, P extends FDPresenter<V, D>, D> extends RxAppCompatActivity implements FDView<D> {
    protected Bundle savedInstanceState; // 重建恢复数据
    private static long mLastStartupId = -1L; // 上次应用的启动ID
    private static final long mStartupTime = System.currentTimeMillis(); // 当前应用的启动ID
    private boolean mStarted;
    private boolean mOnCreateFlag, mOnStartFlag, mOnResumeFlag;

    /**
     * 对话框
     */
    private Dialog mLoading; // 加载弹窗
    private int mLoadCount; // 加载次数

    /**
     * 实例化控制器
     */
    protected P mPresenter;

    protected abstract P initPresenter();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        ActivityPageManager.getInstance().addActivity(this);
        if (needInitApp()) {
            getDelegate().setContentView(onInitView());
            new FastTask<Ignore>() {
                @Override
                public Ignore task() throws Exception {
                    try {
                        onInitApp();
                    } catch (Throwable throwable) {
                        throw new Exception((throwable));
                    }
                    return Ignore.instance;
                }
            }.runIO(new FastTask.Result<Ignore>() {
                @Override
                public void success(Ignore data) {
                    if (!isDestroyed()) {
                        // 绑定控制器
                        if ((mPresenter = initPresenter()) != null) {
                            mPresenter.attach((V) FDActivity.this);
                        }
                        // 获取之前的启动ID
                        if (savedInstanceState != null) {
                            long savedStartupId = savedInstanceState.getLong("AppStartupTime");
                            if (savedStartupId != mStartupTime && savedStartupId != mLastStartupId) { // 启动ID不一致，进程销毁重建了，需要执行进程恢复操作
//                                L.d("nodawang", "Application恢复 mStartupTime:" + mStartupTime + " mLastStartupId:" + mLastStartupId + " savedStartupId:" + savedStartupId);
                                mLastStartupId = savedStartupId;
                                // Application被清理
                                onApplicationCleaned();
                            }
                        }
                        callOnCreateActivity();
                    }
                }

                @Override
                public void fail(Throwable error, String info) {
                    onInitFail(error);
                }
            }, this);
        } else {
            // 绑定控制器
            if ((mPresenter = initPresenter()) != null) mPresenter.attach((V) this);
            callOnCreateActivity();
        }
    }

    private void callOnCreateActivity() {
        mOnCreateFlag = true;
//        L.d("nodawang", "onCreateActivity->mOnCreateFlag:" + mOnCreateFlag + " class:" + getClass().getSimpleName());
        onCreateActivity(savedInstanceState);
        if (mOnStartFlag) onStartActivity();
        if (mOnResumeFlag) onResumeActivity();
    }

    public boolean isActivityCreated() {
        return mOnCreateFlag;
    }

    /**
     * 判断是否需要初始化Application
     *
     * @return true/false
     */
    protected boolean needInitApp() {
        return FDCore.needWaitConfigFinish(); // 判断是否需要等待异步配置完成
    }

    /**
     * 初始化Application
     *
     * @throws Throwable 异常
     */
    protected void onInitApp() throws Throwable {
        FDCore.waitConfigFinish(); // 等待异步配置完成
    }

    /**
     * 初始化Application期间，Activity展示的内容
     *
     * @return view
     */
    protected View onInitView() {
        TextView tv = new TextView(this);
        tv.setText("正在初始化（模拟）");
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return tv;
    }

    /**
     * 初始化Application失败
     *
     * @param error 错误
     */
    protected void onInitFail(Throwable error) {
        error.printStackTrace();
    }

    /**
     * Application被清理时调用
     */
    protected void onApplicationCleaned() {
    }

    /**
     * Activity onCreate
     * Application已完成异步配置
     */
    protected void onCreateActivity(@Nullable Bundle savedInstanceState) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mOnCreateFlag) {
            onStartActivity();
        } else {
            mOnStartFlag = true;
        }
        mStarted = true;
    }

    /**
     * Activity onStart
     * Application已完成异步配置
     */
    protected void onStartActivity() {
//        L.d("nodawang", "onStartActivity->mOnStartFlag:" + mOnStartFlag + " class:" + getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOnCreateFlag) {
            onResumeActivity();
        } else {
            mOnResumeFlag = true;
        }
    }

    /**
     * Activity onResume
     * Application已完成异步配置
     */
    protected void onResumeActivity() {
//        L.d("nodawang", "onResumeActivity->mOnResumeFlag:" + mOnResumeFlag + " class:" + getClass().getSimpleName());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        afterContentView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putLong("AppStartupTime", mStartupTime);
        outState.putInt("LoadCount", mLoadCount);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLoadCount = savedInstanceState.getInt("LoadCount");
    }

    @Override
    public void init(D initData, Throwable e) {
    }

    /**
     * 获取关联的RxLifecycle
     */
    public <T> LifecycleTransformer<T> getLifecycle(@NonNull ActivityEvent event) {
        return bindUntilEvent(event);
    }

    /**
     * 绘制对话框
     * 一般用于网络访问时显示(子类可重写，使用自定义对话框)
     *
     * @param message 提示的信息
     * @return Dialog 对话框
     */
    protected Dialog createLoadingDialog(@Nullable String message) {
        return new FDLoadingDialog(this, R.style.Theme_AppCompat_Dialog, message);
    }

    protected void updateLoadingDialog(@Nullable Dialog dialog, @Nullable String message) {
        if (dialog != null) ((FDLoadingDialog) dialog).updateLoadingDialog(message);
    }

    /**
     * 显示对话框
     */
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    public void showLoadingDialog(String message) {
        showLoadingDialog(message, true);
    }

    /**
     * 显示对话框 showLoadingDialog()和hideLoadingDialog()必须成对调用
     */
    public synchronized void showLoadingDialog(String message, boolean cancelable) {
        mLoadCount++;
        if (mLoading == null) {
            mLoading = createLoadingDialog(message);
            mLoading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mPresenter != null) mPresenter.onDialogDismiss(mLoadCount != 0);
                    updateLoadingDialog(null, null);
                    mLoading = null;
                    mLoadCount = 0;
                }
            });
        } else {
            updateLoadingDialog(mLoading, message);
        }
        mLoading.setCancelable(cancelable);
        mLoading.show();
    }

    /**
     * 隐藏对话框
     */
    public synchronized void hideLoadingDialog() {
        mLoadCount = Math.max(0, mLoadCount - 1);
        if (mLoadCount > 0) return;
        if (mLoading != null) {
            mLoading.setOnDismissListener(null);
            mLoading.dismiss();
            mLoading = null;
        }
    }

    public synchronized void cancelLoadingDialog() {
        mLoadCount = 1;
        hideLoadingDialog();
    }


    /**
     * 拿到最新Activity
     *
     * @return BaseActivity
     */
    public static FDActivity getLatestActivity() {
        return ActivityPageManager.getInstance().currentActivity();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAll() {
        ActivityPageManager.getInstance().finishAllActivity();
    }

    @Override
    protected void onStop() {
        mStarted = false;
        super.onStop();
    }

    public boolean isStarted() {
        return mStarted;
    }

    @Override
    protected void onDestroy() {
        ActivityPageManager.getInstance().removeActivity(this);
        if (mLoading != null) {
            mLoading.dismiss();
            mLoading = null;
        }
        // 解绑控制器
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
        super.onDestroy();
    }

    private void afterContentView() {
        if (mPresenter != null) {
            if (mPresenter.isActive()) mPresenter.afterContentView();
        } else {
            getWindow().getDecorView().post(() -> init(null, null));
        }
    }
}
