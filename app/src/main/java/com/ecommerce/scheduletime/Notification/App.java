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

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    public static final String CHANNEL_3_ID = "channel3";
    public static final String CHANNEL_4_ID = "channel4";
    public static final String CHANNEL_5_ID = "channel5";
    public static final String CHANNEL_6_ID = "channel6";
    public static final String CHANNEL_7_ID = "channel7";
    public static final String CHANNEL_8_ID = "channel8";
    public static final String CHANNEL_DOWNLOAD_ID = "channelDownload";

    public static final String CHANNEL_CUSTOM_ID = "channelCustom";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is channel 1");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID,"Channel 2", NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("This is channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

            //List of channels
            NotificationChannel channel3 = new NotificationChannel(CHANNEL_3_ID,"Channel 3", NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("This is channel 3");

            NotificationChannel channel4 = new NotificationChannel(CHANNEL_4_ID,"Channel 4", NotificationManager.IMPORTANCE_HIGH);
            channel4.setDescription("This is channel 4");

            NotificationChannel channel5 = new NotificationChannel(CHANNEL_5_ID,"Channel 5", NotificationManager.IMPORTANCE_HIGH);
            channel5.setDescription("This is channel 5");

            NotificationChannel channel6 = new NotificationChannel(CHANNEL_6_ID,"Channel 6", NotificationManager.IMPORTANCE_HIGH);
            channel6.setDescription("This is channel 6");

            NotificationChannel channel7 = new NotificationChannel(CHANNEL_7_ID,"Channel 7", NotificationManager.IMPORTANCE_HIGH);
            channel7.setDescription("This is channel 7");

            NotificationChannel channel8 = new NotificationChannel(CHANNEL_8_ID,"Channel 8", NotificationManager.IMPORTANCE_HIGH);
            channel8.setDescription("This is channel 8");

            NotificationChannel channel9 = new NotificationChannel(CHANNEL_DOWNLOAD_ID,"Channel Download", NotificationManager.IMPORTANCE_LOW);
            channel9.setDescription("This is channel Download");

            List<NotificationChannel> channels = Arrays.asList(channel3, channel4, channel5, channel6, channel7, channel8, channel9);

            manager.createNotificationChannels(channels);

            //Custom channel
            NotificationChannel channelCustom = new NotificationChannel(CHANNEL_CUSTOM_ID,"Channel Custom", NotificationManager.IMPORTANCE_HIGH);
            channelCustom.setDescription("This is a custom channel");

            NotificationManager manager_ = getSystemService(NotificationManager.class);
            manager_.createNotificationChannel(channelCustom);
        }
    }
}
