package com.feimeng.fdroid.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * SD卡 工具类
 * Created by feimeng on 2017/1/20.
 */
public class SDCardUtil {
    private static final String PATH = File.separator + "aa" + File.separator + "bb";

    /**
     * SD卡是否挂载成功
     *
     * @return true 成功，false 失败
     */
    public static boolean isOk() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 得到SD卡的绝对路径
     *
     * @return 绝对路径
     */
    public static String getAbsolutelyPath() {
        if (isOk()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 得到可用空间大小
     *
     * @return 可用空间字节
     */
    @SuppressWarnings("deprecation")
    public static int getAvailableSize() {
        if (isOk()) {
            String path = getAbsolutelyPath();
            if (path != null) {
                StatFs fs = new StatFs(path);
                int size = fs.getBlockSize();
                int blocks = fs.getAvailableBlocks();
                return blocks * size;
            }
        }
        return 0;
    }


    /**
     * 存储数据
     *
     * @param filename 文件名
     * @param type     文件后缀名，如：txt,png,mp3,apk
     * @param bytes    数据
     * @return true 成功，false 失败
     */
    public static boolean putData(String filename, String type, byte[] bytes) {
        if (getAbsolutelyPath() != null && bytes.length < getAvailableSize()) {
            File file = new File(getAbsolutelyPath());
            if (!file.exists()) {
                if (!file.mkdirs())
                    return false;
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file.getAbsoluteFile() + File.separator + filename + "." + type);
                fos.write(bytes, 0, bytes.length);
                fos.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 存储数据
     *
     * @param filename 文件名
     * @param type     后缀名
     * @param is       数据
     * @return true 成功，false 失败
     */
    public static boolean putData(String filename, String type, InputStream is) {
        int size;
        try {
            size = is.available();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (getAbsolutelyPath() != null && size < getAvailableSize()) {
            File file = new File(getAbsolutelyPath() + PATH);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return false;
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file.getAbsoluteFile() + File.separator + filename + "." + type);
                int len;
                byte[] b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    fos.write(b, 0, len);
                }
                fos.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
