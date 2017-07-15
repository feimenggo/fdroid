package com.feimeng.fdroid.upgrade;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;

import com.feimeng.fdroid.R;
import com.feimeng.fdroid.base.FDActivity;
import com.feimeng.fdroid.utils.SP;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.feimeng.fdroid.utils.SP.get;

/**
 * 版本升级管理器
 * Created by feimeng on 2017/2/4.
 */
public class VersionUpgradeManager {
    public static int REQUEST_CODE = 123;
    static String DOWNLOAD_ID = "upgrade_download_id";
    public static String DOWNLOAD_URL = "upgrade_download_url";
    private static VersionUpgradeManager mInstance;

    public String provider;

    private boolean mRunning;// 正在运行
    private boolean mShowNotification;// 在通知栏展示
    private boolean mAutoInstall = true;// 下载完成自动安装APK
    private DownloadCompleteReceiver mReceiver;
    private OnUpgradeListener mDownloadListener;
    private ContentObserver mDownloadObserver;

    private VersionInfo mVersionInfo;

    private VersionUpgradeManager() {
    }

    public static VersionUpgradeManager getInstance() {
        if (mInstance == null) {
            synchronized (VersionUpgradeManager.class) {
                if (mInstance == null) {
                    mInstance = new VersionUpgradeManager();
                }
            }
        }
        return mInstance;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * 下载完成后进行安装
     *
     * @param autoInstall true 安装, false 不安装
     */
    public void autoInstall(boolean autoInstall) {
        mAutoInstall = autoInstall;
    }

    /**
     * 在通知栏里显示下载进度
     *
     * @param versionInfo 在通知栏显示的版本信息
     */
    public void showNotification(VersionInfo versionInfo) {
        mShowNotification = true;
        mVersionInfo = versionInfo;
    }

    /**
     * 新版本升级
     *
     * @param context 上下文
     * @param apkName APK的文件名，不包含.apk
     * @param apkUrl  APK的下载地址
     * @return true 任务开始，false 任务失败
     */
    public boolean upgrade(Context context, String apkName, String apkUrl) {
        if (mRunning) return true;
        if (mVersionInfo == null) {
            mVersionInfo = new VersionInfo(context.getString(R.string.app_name) + "·新版本", "新功能", null);
        }
        mVersionInfo.setApkInfo(apkName, apkUrl);
        // 判断APK文件是否已下载
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , mVersionInfo.getApkName());
        if (file.exists()) {
            if (mDownloadListener != null) mDownloadListener.onComplete();
            startInstall(context);
            return true;
        }
        if (!SP.available()) return false;
        // 创建下载任务
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mVersionInfo.getApkUrl()));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mVersionInfo.getApkName());
        if (mShowNotification) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(mVersionInfo.getTitle());
            request.setDescription(mVersionInfo.getDescription());
        } else {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        // 开始下载
        long downloadId = downloadManager.enqueue(request);
        // 监听下载完成，进行自动安装APK
        if (mAutoInstall || mDownloadListener != null) registerBroadcast(context);
        // 监听下载进度
        if (mDownloadListener != null) downloadProgress(context, downloadManager, downloadId);
        mRunning = true;
        return SP.put(DOWNLOAD_ID, downloadId);
    }

    private void downloadProgress(Context context, final DownloadManager downloadManager, final long downloadId) {
        // 开始下载
        mDownloadListener.onStart();

        mDownloadObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //此处可以通知handle去查询下载状态
                queryDownloadStatus(downloadManager, downloadId);
            }
        };
        context.getContentResolver().registerContentObserver(
                Uri.parse("content://downloads/my_downloads"), true, mDownloadObserver);
    }

    private void queryDownloadStatus(DownloadManager downloadManager, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            long totalBytes = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            long downloadedBytes = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            if (totalBytes != -1)
                mDownloadListener.onProgress(totalBytes, downloadedBytes);
            c.close();
        }
    }

    /**
     * 设置下载进度监听器
     *
     * @param downloadListener 下载进度
     */
    public void setOnDownloadListener(OnUpgradeListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }


    /**
     * 注册APK下载完成广播
     */
    private void registerBroadcast(Context context) {
        mReceiver = new DownloadCompleteReceiver();
        IntentFilter intent = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(mReceiver, intent);
    }

    /**
     * 取消升级
     */
    public void cancel(Context context) {
        if (!mRunning || !SP.available()) return;
        long downloadId = (long) get(DOWNLOAD_ID, 0L);
        if (downloadId == 0) return;
        // 取消下载任务
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.remove(downloadId);
        // 注销进度监听
        if (mDownloadObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadObserver);
        }
        // 注销广播
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mInstance = null;
        if (mDownloadListener != null)
            mDownloadListener.onFail();
    }

    /**
     * 下载完成
     */
    void downloadComplete(Context context) {
        mRunning = false;
        // 通知下载完成
        if (mDownloadListener != null)
            mDownloadListener.onComplete();
        // 注销广播
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        // 注销进度监听
        if (mDownloadObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadObserver);
            mDownloadObserver = null;
        }
        // 自动安装APK
        if (mAutoInstall)
            startInstall(context);
    }

    void downloadFail(Context context) {
        mRunning = false;
        // 通知下载失败
        if (mDownloadListener != null)
            mDownloadListener.onFail();
        // 注销广播
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        // 注销进度监听
        if (mDownloadObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadObserver);
            mDownloadObserver = null;
        }
    }

    private void startInstall(Context context) {
        if (provider == null || provider.isEmpty()) return;
        if (context instanceof FDActivity) {
            ((FDActivity) context).startActivityForResult(install(context), REQUEST_CODE);
        } else {
            context.startActivity(install(context));
        }
    }

    private Intent install(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , mVersionInfo.getApkName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, provider, file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        return intent;
    }
}
