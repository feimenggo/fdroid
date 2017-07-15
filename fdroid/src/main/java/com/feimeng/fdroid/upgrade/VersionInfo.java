package com.feimeng.fdroid.upgrade;

/**
 * 版本信息
 * Created by feimeng on 2017/2/4.
 */
public class VersionInfo {
    private String title;
    private String description;
    private String apkUrl;
    private String apkName;
    private String versionName;

    public VersionInfo(String title, String description, String versionName) {
        this.title = title;
        this.description = description;
        this.versionName = versionName;
    }

    public void setApkInfo(String apkName, String apkUrl) {
        this.apkName = apkName;
        this.apkUrl = apkUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getApkName() {
        if (versionName == null) {
            return apkName + "_new" + ".apk";
        }
        return apkName + "_v" + versionName + ".apk";
    }
}
