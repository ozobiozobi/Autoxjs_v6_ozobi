package com.stardust.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

public class StoragePermissionUtils {

    /**
     * 检查应用是否拥有管理所有文件的权限
     * @return 是否拥有权限
     */
    public static boolean hasManageAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return Environment.isExternalStorageManager();
        }
        return true;
    }

    /**
     * 跳转到管理所有文件权限的设置页面
     * @param context 应用上下文
     */
    public static void requestManageAllFilesPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // 如果设备不支持 ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION，跳转到通用设置页面
                Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(settingsIntent);
            }
        }
    }
}