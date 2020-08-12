package com.feimeng.fdroid.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pools;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现异步加载布局的功能
 * 1.引入线程池，减少单线程等待
 * 2.手动设置setFactory2
 */
public class AsyncLayoutInflaterPlus implements Handler.Callback {
    private static final String TAG = "AsyncLayoutInflaterPlus";
    private Pools.SynchronizedPool<InflateRequest> mRequestPool = new Pools.SynchronizedPool<>(10);

    private Handler mHandler;
    private Dispather mDispatcher;
    private LayoutInflater mInflater;

    public AsyncLayoutInflaterPlus(@NonNull Context context) {
        mHandler = new Handler(this);
        mDispatcher = new Dispather();
        mInflater = getLayoutInflater(context);
    }

    @UiThread
    public void inflate(@LayoutRes int resId, @NonNull ViewGroup parent, @NonNull OnInflateFinishedListener callback) {
        inflate(resId, parent, false, callback);
    }

    @UiThread
    public void inflate(@LayoutRes int resId, @NonNull ViewGroup parent, boolean attachToRoot, @NonNull OnInflateFinishedListener callback) {
        inflate(resId, parent, attachToRoot, true, callback);
    }

    @UiThread
    public void inflate(@LayoutRes int resId, @NonNull ViewGroup parent, boolean attachToRoot, boolean async, @NonNull OnInflateFinishedListener callback) {
        if (async) {
            InflateRequest request = obtainRequest();
            request.inflater = this;
            request.resId = resId;
            request.parent = parent;
            request.attachToRoot = attachToRoot;
            request.callback = callback;
            mDispatcher.enqueue(request);
        } else {
            View view = mInflater.inflate(resId, parent, false);
            if (attachToRoot) parent.addView(view);
            callback.onInflateFinished(view, resId, parent);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        InflateRequest request = (InflateRequest) msg.obj;
        if (request.view == null) {
            request.view = mInflater.inflate(request.resId, request.parent, false);
        }
        if (request.attachToRoot) request.parent.addView(request.view);
        request.callback.onInflateFinished(request.view, request.resId, request.parent);
        releaseRequest(request);
        return true;
    }

    public interface OnInflateFinishedListener {
        void onInflateFinished(@NonNull View view, @LayoutRes int resId, @NonNull ViewGroup parent);
    }

    private LayoutInflater getLayoutInflater(Context context) {
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getLayoutInflater();
        } else {
            return new BasicInflater(context);
        }
    }

    private static class InflateRequest {
        AsyncLayoutInflaterPlus inflater;
        ViewGroup parent;
        boolean attachToRoot;
        int resId;
        View view;
        OnInflateFinishedListener callback;
    }

    private static class Dispather {
        // 获得当前CPU的核心数
        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        // 设置线程池的核心线程数2-4之间,但是取决于CPU核数
        private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
        // 设置线程池的最大线程数为 CPU核数 * 2 + 1
        private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        // 设置线程池空闲线程存活时间30s
        private static final int KEEP_ALIVE_SECONDS = 30;

        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "AsyncLayoutInflatePlus #" + mCount.getAndIncrement());
            }
        };

        // LinkedBlockingQueue 默认构造器，队列容量是Integer.MAX_VALUE
        private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();

        /**
         * An {@link Executor} that can be used to execute tasks in parallel.
         */
        static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

        static {
            Log.i(TAG, "static initializer: " + " CPU_COUNT = " + CPU_COUNT + " CORE_POOL_SIZE = " + CORE_POOL_SIZE + " MAXIMUM_POOL_SIZE = " + MAXIMUM_POOL_SIZE);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            THREAD_POOL_EXECUTOR = threadPoolExecutor;
        }

        void enqueue(InflateRequest request) {
            THREAD_POOL_EXECUTOR.execute((new InflateRunnable(request)));
        }
    }

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        BasicInflater(Context context) {
            super(context);
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return new BasicInflater(newContext);
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            for (String prefix : sClassPrefixList) {
                try {
                    View view = createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }

            return super.onCreateView(name, attrs);
        }
    }

    private static class InflateRunnable implements Runnable {
        private InflateRequest request;
        private boolean isRunning;

        InflateRunnable(InflateRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            isRunning = true;
            try {
                request.view = request.inflater.mInflater.inflate(request.resId, request.parent, false);
            } catch (RuntimeException ex) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(TAG, "Failed to inflate resource in the background! Retrying on the UI thread", ex);
            }
            Message.obtain(request.inflater.mHandler, 0, request).sendToTarget();
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

    private InflateRequest obtainRequest() {
        InflateRequest obj = mRequestPool.acquire();
        if (obj == null) obj = new InflateRequest();
        return obj;
    }

    private void releaseRequest(InflateRequest obj) {
        obj.callback = null;
        obj.inflater = null;
        obj.parent = null;
        obj.attachToRoot = false;
        obj.resId = 0;
        obj.view = null;
        mRequestPool.release(obj);
    }

    public static ViewGroup createPlaceHolder(@NonNull Context context, @NonNull ViewGroup.LayoutParams params, Integer minHeight, Integer backgroundColor) {
        FrameLayout parent = new FrameLayout(context);
        parent.setLayoutParams(params);
        if (minHeight != null) parent.setMinimumHeight(minHeight);
        if (backgroundColor != null) parent.setBackgroundColor(backgroundColor);
        return parent;
    }
}

