package com.feimeng.fdroid.base;

import android.app.Application;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.SP;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroid.utils.UE;

/**
 * 全局Application
 * Created by feimeng on 2017/1/20.
 */
public abstract class FDApp extends Application {
    private static FDApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        initCore();// 核心初始化
        config();
    }

    /**
     * 配置
     */
    protected abstract void config();

    private void initCore() {
        sInstance = this;
        // 初始化 Toast、Log、SharedPreferences
        // Toast
        T.init(FDConfig.SHOW_TOAST);
        // Log
        L.init(FDConfig.SHOW_LOG, L.V);
        // 增强用户体验效果工具
        UE.init(getApplicationContext());
        // 共享参数
        SP.init(getApplicationContext(), FDConfig.SP_NAME);
    }

    public static FDApp getInstance() {
        return sInstance;
    }
}
