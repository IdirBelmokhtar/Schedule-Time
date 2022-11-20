package com.ecommerce.scheduletime.Notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.ecommerce.scheduletime.R;

import java.util.Arrays;
import java.util.List;

public class App extends Application {

    public static final String CHANNEL_3_ID = "channel3";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel3 = new NotificationChannel(CHANNEL_3_ID,"Task Channel", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("Task Notification");
        }
    }
}
