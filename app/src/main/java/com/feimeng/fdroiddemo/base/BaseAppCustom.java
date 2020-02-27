package com.feimeng.fdroiddemo.base;

import android.app.Application;

import com.feimeng.fdroid.config.FDConfig;
import com.feimeng.fdroid.mvp.FDCore;
import com.feimeng.fdroid.utils.L;
import com.feimeng.fdroid.utils.SP;
import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.BuildConfig;
import com.feimeng.fdroiddemo.data.Constants;

/**
 * Author: Feimeng
 * Time:   2020/2/27
 * Description: 自定义Application
 */
public class BaseAppCustom extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FDCore.init(this, new FDCore() {
            @Override
            protected void config(Application application) { // 在UI线程调用
                // 初始化 Log
                L.init(BuildConfig.DEBUG, L.V);
                // 初始化 SharedPreferences
                SP.init(application, Constants.SP_NAME);
                // 初始化Toast
                T.init(true);
                FDConfig.SHOW_HTTP_LOG = BuildConfig.DEBUG;
                FDConfig.SHOW_HTTP_EXCEPTION_INFO = BuildConfig.DEBUG;
                FDConfig.READ_TIMEOUT = 120;
                FDConfig.WRITE_TIMEOUT = 120;
                FDConfig.CONNECT_TIMEOUT = 30;
                FDConfig.INFO_EOF_EXCEPTION = "数据格式异常";
                FDConfig.INFO_TIMEOUT_EXCEPTION = "请求超时";
                FDConfig.INFO_CONNECT_EXCEPTION = "无法连接服务器";
                FDConfig.INFO_UNKNOWN_EXCEPTION = "抱歉！遇到错误了";
            }

            @Override
            protected void configAsync(Application application) { // 在子线程调用
            }
        });
    }
}
