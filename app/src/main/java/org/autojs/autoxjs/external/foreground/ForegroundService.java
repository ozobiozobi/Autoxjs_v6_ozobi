package org.autojs.autoxjs.external.foreground;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.autojs.autoxjs.R;
import org.autojs.autoxjs.ui.main.MainActivity;

public class ForegroundService extends Service {


    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = ForegroundService.class.getName() + ".foreground";

    public static int start(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ForegroundService.class));
        } else {
            context.startService(new Intent(context, ForegroundService.class));
        }
        return START_NOT_STICKY;
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, ForegroundService.class));
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification() {
        // 创建通知渠道（仅在 Android 8 及以上版本中需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // 创建 PendingIntent，确保兼容 Android 11 及以上版本
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), flags);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.foreground_notification_title))
                .setContentText(getString(R.string.foreground_notification_text))
                .setSmallIcon(R.drawable.autojs_logo)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setVibrate(new long[0]);

        // 仅在 Android 8 及以上版本中设置通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        CharSequence name = getString(R.string.foreground_notification_channel_name);
        String description = getString(R.string.foreground_notification_channel_name);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        channel.enableLights(false);
        manager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();

    }
}
