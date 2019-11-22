package com.feimeng.fdroiddemo.data.doo;

import android.app.Activity;
import android.content.Context;

import com.feimeng.fdroid.utils.T;
import com.feimeng.fdroiddemo.R;

/**
 * Author: Feimeng
 * Time:   2018/7/23 19:39
 * Description:
 */
public class NetworkError {
    public static void show(Activity activity) {
        T.showS(activity, getNetWorkError(activity));
    }

    public static String getNetWorkError(Context context) {
        return context.getString(R.string.network_unavailable);
    }
}
