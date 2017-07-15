package com.feimeng.fdroid.upgrade;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.support.v4.content.FileProvider;

import com.feimeng.fdroid.utils.L;

/**
 * 权限工具
 * Created by feimeng on 2017/2/4.
 */
class FDPermissionUtil {
    private FDPermissionUtil() {
    }

    /**
     * 获取FileProvider的auth
     */
    static String getFileProviderAuthority(Context context) {
        try {
            for (ProviderInfo provider : context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS).providers) {
                if (FileProvider.class.getName().equals(provider.name) && provider.authority.endsWith(".version_upgrade.file_provider")) {
                    L.d(provider.name + "_" + provider.authority);
                    return provider.authority;
                }
            }
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return null;
    }
}
