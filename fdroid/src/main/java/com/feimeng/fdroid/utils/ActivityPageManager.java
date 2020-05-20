/*
    ShengDao Android Client, ActivityPageManager
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package com.feimeng.fdroid.utils;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.FDActivity;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Author: Feimeng
 * Time:   2017/1/20
 * Description: FDActivity页面管理器
 */
public class ActivityPageManager {
    private static Stack<FDActivity> activities = new Stack<>();
    private static ActivityPageManager instance = new ActivityPageManager();

    private ActivityPageManager() {
    }

    /**
     * get the AppManager instance, the AppManager is singleton.
     */
    public static ActivityPageManager getInstance() {
        return instance;
    }

    public List<FDActivity> all() {
        return activities;
    }

    /**
     * add Activity to Stack
     */
    public void addActivity(FDActivity activity) {
        activities.add(activity);
    }


    /**
     * remove Activity from Stack
     */
    public void removeActivity(FDActivity activity) {
        activities.remove(activity);
    }

    /**
     * 获取最新的Activity
     */
    @Nullable
    public FDActivity currentActivity() {
        return activities.size() == 0 ? null : activities.lastElement();
    }

    /**
     * 销毁最新的Activity
     */
    public void finishActivity() {
        finishActivity(currentActivity());
    }

    /**
     * 销毁指定的Activity
     *
     * @param activity Activity类
     */
    public void finishActivity(FDActivity activity) {
        if (activity != null) {
            activity.finish();
            activities.remove(activity);
        }
    }

    /**
     * 销毁指定的Activity
     *
     * @param cls Activity类
     */
    public void finishActivity(Class<?> cls) {
        for (FDActivity activity : activities) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void finishAllActivity() {
        finishAllActivity(null);
    }

    /**
     * 销毁所有的Activity
     *
     * @param activity 保留的Activity
     */
    public void finishAllActivity(Activity activity) {
        Iterator<FDActivity> iterator = activities.iterator();
        while (iterator.hasNext()) {
            FDActivity fdActivity = iterator.next();
            if (activity != null && fdActivity == activity) continue;
            fdActivity.finish();
            iterator.remove();
        }
    }

    /**
     * 退出应用
     */
    public void exit() {
        finishAllActivity(); // 销毁所有的Activity
        android.os.Process.killProcess(android.os.Process.myPid()); // 杀死该应用进程
    }
}
