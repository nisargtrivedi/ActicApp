package com.activedistribution.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.activedistribution.R;
import com.activedistribution.view.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;


public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";
    private PrefStore prefStore;
    Intent resultIntent;
    private Notification notification;


    public static void displayMessage(Context context, Bundle extras) {
        Intent intent = new Intent(Constants.DISPLAY_MESSAGE_ACTION);
        intent.putExtras(extras);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            prefStore = new PrefStore(this);
            Map data = remoteMessage.getData();
            Log.e("bundle Data", "bundle" + data);
            String from = remoteMessage.getFrom();
            Log.d(TAG, "From: " + from);
            Bundle bundle = new Bundle();
            if (data.size() != 0) {
                bundle.putBoolean("is_push", true);
                bundle.putString("label", data.get("label").toString());
                bundle.putString("message", data.get("message").toString());
                bundle.putString("user_id", data.get("user_id").toString());
                bundle.putString("job_id", data.get("job_id").toString());
            }
            if (inForeground()) {
                displayMessage(this, bundle);
                generateNotification(this, bundle);
            } else {
                generateNotification(this, bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean inForeground() {
        return prefStore.getBoolean(Constants.FORGROUND,false);
    }

    public void generateNotification(Context context, Bundle extra) {
        int id = 0;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"1")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setContentText(extra.getString("message"))
                .setAutoCancel(true);
                 Uri NotiSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            try {
                mediaPlayer.setDataSource(context, NotiSound);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBuilder.setSound(NotiSound);
        long[] vibrate = {1000, 1000};
        mBuilder.setVibrate(vibrate);

        resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtras(extra);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        PrefStore store = new PrefStore(this);
        store.saveString(Constants.getDEVICE_TOKEN(),s);
    }
}

