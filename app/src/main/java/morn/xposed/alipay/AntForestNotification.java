package morn.xposed.alipay;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class AntForestNotification {
    public static final int ANTFOREST_NOTIFICATION_ID = 46;
    public static final String CHANNEL_ID = "pansong291.xposed.quickenergy.ANTFOREST_NOTIFY_CHANNEL";
    private static NotificationManager mNotifyManager;
    private static Notification mNotification;
    private static Builder builder;
    private static boolean isStart = false;
    private static boolean autoStart = false;

    private static NotificationManager getNotificationManager(Context context) {
        if (mNotifyManager == null) {
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotifyManager;
    }

    public static void stop(Context context, boolean paramBoolean) {
        if (isStart) {
            if ((context instanceof Service)) {
                ((Service) context).stopForeground(paramBoolean);
            } else {
                getNotificationManager(context).cancel(46);
            }
            isStart = false;
        }
        autoStart = false;
    }

    public static void setContentText(CharSequence cs) {
        if (isStart && !autoStart) {
            mNotification = builder.setContentText(cs).build();
            if (mNotifyManager != null) {
                mNotifyManager.notify(ANTFOREST_NOTIFICATION_ID, mNotification);
            }
        }
    }

    public static void setAutoStart(boolean paramBoolean) {
        autoStart = paramBoolean;
    }

    public static boolean getAutoStart() {
        return autoStart;
    }

    private static void initNotification(Context context) {
        if (mNotification == null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("alipays://platformapi/startapp?appId="));
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "能量提醒", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setShowBadge(false);
                getNotificationManager(context).createNotificationChannel(notificationChannel);
                builder = new Builder(context, CHANNEL_ID);
            } else {
                getNotificationManager(context);
                builder = new Builder(context).setPriority(Notification.PRIORITY_LOW);
            }
            mNotification = builder
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentTitle("秋风")
                    .setContentText("秋风加载成功")
                    .setAutoCancel(false)
                    .setContentIntent(pi).build();
        }
    }

    public static void start(Context context) {
        initNotification(context);
        if (!isStart) {
            if (context instanceof Service) {
                ((Service) context).startForeground(46, mNotification);
            } else {
                getNotificationManager(context).notify(46, mNotification);
            }
            isStart = true;
        }
    }
}