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
public class FDApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        config();
        // 核心初始化
        initCore();
    }

    /**
     * 配置
     */
    protected void config() {

    }

    private void initCore() {
        // 初始化Toast、Log、SharedPreferences
        // Toast
        if (FDConfig.SHOW_TOAST) T.init(true);
        // Log
        if (FDConfig.SHOW_LOG) L.init(true, L.V);
        // 增强用户体验效果工具
        UE.init(getApplicationContext());
        // 共享参数
        SP.init(getApplicationContext(), FDConfig.SP_NAME);
    }
}
