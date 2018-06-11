package com.feimeng.fdroid.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 版本 工具类
 * Created by feimeng on 2016/6/22.
 */
public class VersionUtils {
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            L.e(e.getMessage());
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            L.e(e.getMessage());
        }
        return verName;
    }
}
