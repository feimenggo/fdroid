package com.feimeng.fdroiddemo.mvp.model.file;

import android.content.Context;

import java.io.File;

/**
 * Author: Feimeng
 * Time:   2019/10/11
 * Description: 应用存储空间管理器
 */
public class FileManager {
    private static String APP_PATH; // 存储路径
    private static String APP_CACHE_PATH; // 缓存路径

    /**
     * 资源文件夹名
     */
    private static final String APP_DIARY = "diary"; // 日记目录：日记数据、日记资源
    private static final String APP_DIARY_FONT = "font"; // 日记字体目录
    private static final String APP_USER = "user"; // 用户目录
    private static final String APP_APK = "apk"; // 安装包目录
    private static final String APP_LOG = "log"; // 应用日志目录

    /**
     * 初始化文件管理者
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        try {
            // 使用外部应用存储空间
            File appFile = context.getExternalFilesDir(null);
            File cacheFile = context.getExternalCacheDir();
            // 如果共享存储当前不可用，使用内部应用存储空间
            if (appFile == null || cacheFile == null) {
                appFile = context.getFilesDir();
                cacheFile = context.getCacheDir();
            }
            // 创建资源文件夹
            makeDir(new File(appFile, APP_DIARY));
            makeDir(new File(appFile, APP_DIARY_FONT));
            makeDir(new File(appFile, APP_USER));
            makeDir(new File(appFile, APP_APK));
            makeDir(new File(cacheFile, APP_LOG));
            APP_PATH = appFile.getAbsolutePath();
            APP_CACHE_PATH = cacheFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取日记目录
     */
    public static File getDiaryDir() {
        return new File(APP_PATH + File.separator + APP_DIARY);
    }

    /**
     * 获取日记字体
     */
    public static File getDiaryFontDir() {
        return new File(APP_PATH + File.separator + APP_DIARY_FONT);
    }

    /**
     * 获取用户目录
     */
    public static File getUserDir() {
        return new File(APP_PATH + File.separator + APP_USER);
    }

    /**
     * 获取安装包目录
     */
    public static File getApkDir() {
        return new File(APP_PATH + File.separator + APP_APK);
    }

    /**
     * 获取日志目录
     */
    public static File getLogDir() {
        return new File(APP_PATH + File.separator + APP_LOG);
    }

    /**
     * 获取缓存目录
     */
    public static File getCacheFile() {
        return new File(APP_CACHE_PATH);
    }

    /**
     * 确保目录存在
     */
    public static File makeDir(File file) throws Exception {
        if (!file.exists() && !file.mkdirs()) throw new Exception("没有文件读写权限");
        return file;
    }
}
