package com.example.vrcontrolleroffset;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class OffsetService extends Service {
    public static final String ACTION_START_FOREGROUND = "com.example.vrcontrolleroffset.action.START_FOREGROUND";
    public static final String ACTION_STOP_FOREGROUND = "com.example.vrcontrolleroffset.action.STOP_FOREGROUND";
    private static final String CHANNEL_ID = "offset_service_channel";
    private static final int NOTIFICATION_ID = 101;

    private static float offsetX = 0f;
    private static float offsetY = 0f;
    private static float offsetZ = 0f;
    private static boolean fixedControllers = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_START_FOREGROUND.equals(intent.getAction())) {
            startForeground(NOTIFICATION_ID, buildNotification());
        } else if (intent != null && ACTION_STOP_FOREGROUND.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        String contentText = String.format(
                "Offsets X: %.1f Y: %.1f Z: %.1f | Fixed: %s",
                offsetX,
                offsetY,
                offsetZ,
                fixedControllers ? "On" : "Off"
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("VR Controller Offset Service")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "VR Controller Offset Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Persistent notification for VR controller offset service.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void setOffset(String axis, float value) {
        switch (axis) {
            case "X":
                offsetX = value;
                break;
            case "Y":
                offsetY = value;
                break;
            case "Z":
                offsetZ = value;
                break;
        }
    }

    public static void setFixedControllers(boolean enabled) {
        fixedControllers = enabled;
    }
}
