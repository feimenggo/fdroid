package com.feimeng.fdroid.upgrade;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.feimeng.fdroid.utils.SP;

import static com.feimeng.fdroid.upgrade.VersionUpgradeManager.DOWNLOAD_ID;

/**
 * 下载完成广播接收器
 * Created by feimeng on 2017/1/17.
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SP.available()) return;
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        long downloadId = (long) SP.get(DOWNLOAD_ID, 0L);
        if (downloadId == completeDownloadId) {
            // 下载完成
            VersionUpgradeManager manager = VersionUpgradeManager.getInstance();
            if (manager != null)
                manager.downloadComplete(context);
        }
    }
}
