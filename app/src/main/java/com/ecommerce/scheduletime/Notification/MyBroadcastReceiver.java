package com.ecommerce.scheduletime.Notification;

import static android.content.Context.MODE_PRIVATE;
import static com.ecommerce.scheduletime.Notification.App.CHANNEL_3_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.ecommerce.scheduletime.CreateAlarmNotification;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SplashScreen.SplashScreenActivity;
import com.ecommerce.scheduletime.Sync.SyncDataBaseServiceUpdate;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            /** @param call {@link SyncDataBaseServiceUpdate} when SyncDataUpdate is called -- */
            Intent intent1 = new Intent(context, SyncDataBaseServiceUpdate.class);
            context.startService(intent1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String id_ = intent.getStringExtra("id_");
        String title_ = intent.getStringExtra("title_");
        String color_ = intent.getStringExtra("color_");
        String description = intent.getStringExtra("description");

        Toast.makeText(context, "id_ : " + id_ + "\n" +
                "title_ : " + title_ + "\n" +
                "color_ : " + color_ + "\n" +
                "description : " + description, Toast.LENGTH_LONG).show();

        int color = ContextCompat.getColor(context, R.color.default_);

        if (color_.equals("default_") || color_.equals("default")) {
            color = ContextCompat.getColor(context, R.color.default_);
        } else if (color_.equals("high")) {
            color = ContextCompat.getColor(context, R.color.high);
        } else if (color_.equals("medium")) {
            color = ContextCompat.getColor(context, R.color.medium);
        } else if (color_.equals("low")) {
            color = ContextCompat.getColor(context, R.color.low);
        }

        try {
            sendOnChannel3(context, id_, title_, description, color);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendOnChannel3(Context context, String id_, String title, String description, int color) {
        NotificationManagerCompat notificationManager;
        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(3);// Remove previous Notification.

        Intent activityIntent = new Intent(context, SplashScreenActivity.class);
        SharedPreferences.Editor editor = context.getSharedPreferences("id_task", MODE_PRIVATE).edit();
        editor.putString("idName", id_);
        editor.apply();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("Message", description);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_3_ID)
                .setSmallIcon(R.drawable.schedule_time_icon)
                //.setSound(null)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(color)
                .setContentIntent(contentIntent) // Notification clicked.
                .setDeleteIntent(actionIntent) // Notification dismiss (swipe left or right)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, context.getResources().getString(R.string.dismiss), actionIntent)
                .build();
        //.setVibrate()
        //MediaPlayer mp= MediaPlayer.create(context, R.raw.alarm_notification);
        //mp.start();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(3, notification);

    }

}
