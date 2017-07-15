package com.feimeng.fdroid.upgrade;

/**
 * 下载升级版本进度监听器
 * Created by feimeng on 2017/2/4.
 */
public interface OnUpgradeListener {
    /**
     * 开始下载
     */
    void onStart();

    /**
     * 正在下载
     *
     * @param totalBytes      总共字节
     * @param downloadedBytes 已下载字节
     */
    void onProgress(long totalBytes, long downloadedBytes);

    /**
     * 下载完成
     */
    void onComplete();

    /**
     * 下载失败
     */
    void onFail();
}
