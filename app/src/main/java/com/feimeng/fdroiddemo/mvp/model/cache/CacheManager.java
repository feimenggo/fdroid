package com.feimeng.fdroiddemo.mvp.model.cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Feimeng
 * Time:   2019/11/25
 * Description: 缓存管理器
 */
public class CacheManager {
    private static CacheManager sInstance;
    private SharedPreferences mSP;

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (sInstance == null) sInstance = new CacheManager();
        return sInstance;
    }

    public void init(Context context) {
        mSP = context.getSharedPreferences("cache", Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return mSP.getString(key, defaultValue);
    }

    public int getInteger(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return mSP.getInt(key, defaultValue);
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue) {
        return mSP.getLong(key, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSP.getBoolean(key, defaultValue);
    }

    public Set<String> getStringSet(String key) {
        return mSP.getStringSet(key, new HashSet<>());
    }

    public void putString(String key, String value) {
        mSP.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSP.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSP.edit().putLong(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        mSP.edit().putBoolean(key, value).apply();
    }

    public void putStringSet(String key, Set<String> values) {
        mSP.edit().putStringSet(key, values).apply();
    }

    public void remove(String key) {
        mSP.edit().remove(key).apply();
    }

    public boolean has(String key) {
        return mSP.contains(key);
    }

    public void clear() {
        mSP.edit().clear().apply();
    }

    public SharedPreferences getSP() {
        return mSP;
    }
}
