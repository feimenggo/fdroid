package com.feimeng.fdroid.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * 使用系统默认的文件选择器
 * Created by feimeng on 2017/7/13.
 */
public class FilePicker {
    private Activity activity;
    private int requestCode;
    public static final String TYPE_JPG = "image/jpeg";
    public static final String TYPE_MP3 = "audio/mpeg";
    public static final String TYPE_MP4 = "video/mp4";

    /**
     * 选择文件
     *
     * @param activity    Activity
     * @param type        文件MIME类型
     * @param requestCode 请求码
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startPick(@NonNull Activity activity, String type, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type == null || type.isEmpty() ? "*/*" : type);
        activity.startActivityForResult(Intent.createChooser(intent, null), requestCode);
    }

    /**
     * 处理选择结果
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        结果数据
     * @return 文件路径
     */
    @Nullable
    @SuppressLint("NewApi")
    public String onActivityResult(int requestCode, int resultCode, Intent data) {
        if (activity != null && this.requestCode == requestCode && resultCode == RESULT_OK && null != data) {
            String path = data.getData().getPath();
            if (!new File(path).exists()) {// 判断返回的uri是否是file://类型
                try {
                    path = FileUtils.getPath(activity, data.getData());
                } catch (Exception ignored) {
                }
            }
            return path;
        }
        return null;
    }
}
