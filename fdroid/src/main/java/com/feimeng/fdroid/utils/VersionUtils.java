package com.feimeng.fdroid.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Author: Feimeng
 * Time:   2016/6/22
 * Description: 版本 工具类
 */
public class VersionUtils {
    /**
     * 获取应用VersionCode
     */
    public static int getVerCode(Context context) {
        int verCode;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            L.e(e.getMessage());
            verCode = -1;
        }
        return verCode;
    }

    /**
     * 获取应用VersionName
     */
    public static String getVerName(Context context) {
        String verName;
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            L.e(e.getMessage());
            verName = "";
        }
        return verName;
    }
}
