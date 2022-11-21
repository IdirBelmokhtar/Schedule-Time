package com.ecommerce.scheduletime.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SplashScreen.SplashScreenActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotification extends FirebaseMessagingService {
    private static final String CANAL = "MyNotificationCanal";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String myTitle = remoteMessage.getNotification().getTitle();
        String myMessage = remoteMessage.getNotification().getBody();

        //action
        //open click notification
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        Intent intent;
        //intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //cree notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL);
        builder.setContentTitle(myTitle);
        builder.setContentText(myMessage);

        //add action
        builder.setContentIntent(pendingIntent);

        //Vibration
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        builder.setLights(Color.RED, 3000, 3000);

        //Son
        //builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));

        //une icon
        builder.setSmallIcon(R.drawable.schedule_time_icon);

        //envoyer notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = getString(R.string.notification_channel_id);
            String channelTitle = getString(R.string.notification_channel_title);
            String channelDescription = getString(R.string.notification_channel_description);
            NotificationChannel channel = new NotificationChannel(channelId,channelTitle,NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);

        }
        notificationManager.notify(1, builder.build());
    }
}
