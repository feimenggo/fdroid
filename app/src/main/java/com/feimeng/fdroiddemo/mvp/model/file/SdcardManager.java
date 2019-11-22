package com.feimeng.fdroiddemo.mvp.model.file;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.feimeng.fdroiddemo.base.BaseApp;

import java.io.File;

/**
 * Author: Feimeng
 * Time:   2019/10/11
 * Description: SdCard存储管理器
 */
public class SdcardManager {
    private static final String APP = "FDroid";
    public static final String INFO = "存储卡不可用或无读写权限";

    /**
     * SdCard是否可用
     *
     * @return true:可用 false:不可用
     */
    public static boolean available() {
        // 检测SdCard是否挂载
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return false;
        // 检测是否有写的权限
        int permission = ActivityCompat.checkSelfPermission(BaseApp.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    public static void availableThrow() throws Exception {
        if (!available()) throw new Exception(INFO);
    }

    @NonNull
    public static File getAppDir() {
        return Environment.getExternalStoragePublicDirectory(APP);
    }

    @NonNull
    public static File getBackupDir() {
        return noMedia(makeDir(new File(getAppDir(), "备份")));
    }

    @NonNull
    public static File getSharedPictureDir() {
        return makeDir(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP));
    }

    /**
     * 确保目录存在
     */
    private static File makeDir(File file) {
        try {
            if (!file.exists() && !file.mkdirs()) throw new Exception("无法读写文件");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 确保.noMedia存在
     */
    private static File noMedia(File file) {
        try {
            if (file != null && file.isDirectory()) {
                File noMedia = new File(file, ".nomedia");
                if (!noMedia.exists() && !noMedia.createNewFile()) throw new Exception("无法读写文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
