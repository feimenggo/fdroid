package com.feimeng.fdroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences 工具类
 * Created by feimeng on 2017/1/20.
 */
public class SP {
    private static SharedPreferences sp;

    public static void init(Context context, String spName) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    public static boolean available() {
        return sp != null;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key    名称
     * @param object 数据
     */
    public static boolean put(String key, Object object) {
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else if (object instanceof Set) {
            editor.putStringSet(key, (Set<String>) object);
        }
        return SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key           名称
     * @param defaultObject 默认数据
     * @return 数据
     */
    public static Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else if (defaultObject instanceof Set) {
            return sp.getStringSet(key, (Set<String>) defaultObject);
        } else {
            return null;
        }
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key 名称
     */
    public static void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key 名称
     * @return true 存在，false 不存在
     */
    public static boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return true 存在，false 不存在
     */
    public static Map<String, ?> getAll() {
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return 方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class<SharedPreferences.Editor> clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException ignored) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor editor
         */
        static boolean apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return true;
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) {
            }
            return editor.commit();
        }
    }

    /**
     * 得到编辑器
     *
     * @return 编辑前
     */
    public static SharedPreferences.Editor getEdit() {
        return sp.edit();
    }

    public static SharedPreferences getSP() {
        return sp;
    }

    /**
     * 保存编辑器内容
     *
     * @param editor 编辑器
     */
    public static void saveEdit(SharedPreferences.Editor editor) {
        SharedPreferencesCompat.apply(editor);
    }
}