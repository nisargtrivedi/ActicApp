package com.activedistribution.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;


public class WakeUpService extends Service {
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(12345678, getNotification());
        }else{
            startForeground(12345678,getNotification());
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(12345678, getNotification());
        }else{
            startForeground(12345678,getNotification());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public class LocationServiceBinder extends Binder {
        public WakeUpService getService() {
            return WakeUpService.this;
        }
    }
    private Notification getNotification() {
        Notification.Builder builder=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_01",
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
                channel.setSound(null,null);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                builder= new Notification.Builder(getApplicationContext(), "channel_01");
        }else{
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            builder=new Notification.Builder(getApplicationContext());
        }


        return builder.build();
    }
}
