package com.stardust.autojs.core.ozobi.deviceadmin;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stardust.autojs.runtime.DeviceAdminReceiverMsg;

import java.util.Date;

/* Created by Ozobi - 2024/11/10
* */
public class OzobiDeviceAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "ozobiDebug";

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        // 当设备管理员被启用时调用
        super.onEnabled(context, intent);
        Log.d(TAG, "Device admin enabled");
        // 可以在这里执行一些初始化操作，比如设置密码策略等
        DeviceAdminReceiverMsg.INSTANCE.setEnabled(true);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        // 当设备管理员被禁用时调用
        super.onDisabled(context, intent);
        Log.d(TAG, "Device admin disabled");
        // 可以在这里执行一些清理操作
        DeviceAdminReceiverMsg.INSTANCE.setEnabled(false);
    }

    @Override
    public void onPasswordChanged(@NonNull Context context, @NonNull Intent intent) {
        // 当设备密码被更改时调用
        super.onPasswordChanged(context, intent);
        Log.d(TAG, "Device password changed");
        DeviceAdminReceiverMsg.INSTANCE.setLastPasswordChangedDate(new Date());
    }

    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent) {
        // 当设备密码输入失败达到一定次数时调用
        super.onPasswordFailed(context, intent);
        Log.d(TAG, "Device password failed");
        // 可以在这里执行一些操作，比如锁定设备
        DeviceAdminReceiverMsg.INSTANCE.setLastPasswordFailedDate(new Date());
    }
    @Override
    public void onPasswordSucceeded(@NonNull Context context, @NonNull Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Log.d(TAG, "Device password succeeded");
        DeviceAdminReceiverMsg.INSTANCE.setLastPasswordSucceededDate(new Date());
    }

    // 其他回调方法可以根据需要重写，比如：
    // onLockTaskModeEntering()
    // onLockTaskModeExiting()
    // ...
}
