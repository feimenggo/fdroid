package com.feimeng.fdroid.mvp.model.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Feimeng
 * Time:   2018/7/27 10:22
 * Description: 解决Activity使用Intent传递大数据的问题
 */
public class DataHolder {
    public static String string1, string2;
    private Map<String, WeakReference<Object>> mDataList = new HashMap<>();

    private static class Holder {
        private static final DataHolder INSTANCE = new DataHolder();
    }

    public static DataHolder getInstance() {
        return Holder.INSTANCE;
    }

    public void setData(@NonNull String key, @NonNull Object data) {
        mDataList.put(key, new WeakReference<>(data));
    }

    @Nullable
    public Object getData(@NonNull String key) {
        WeakReference<Object> reference = mDataList.get(key);
        if (reference != null) return reference.get();
        return null;
    }
}
