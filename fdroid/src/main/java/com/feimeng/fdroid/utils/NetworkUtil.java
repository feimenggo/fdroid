package com.feimeng.fdroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络连接 工具类
 * Created by penfi on 2015/11/9.
 */
public class NetworkUtil {
    /**
     * 检测是否已经连接网络。
     *
     * @param context 上下文
     * @return 当且仅当连上网络时返回true, 否则返回false。
     */
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 当前网络是否是WiFi
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
