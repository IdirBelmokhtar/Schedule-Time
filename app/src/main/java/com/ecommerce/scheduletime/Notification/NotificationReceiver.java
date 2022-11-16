package com.ecommerce.scheduletime.Notification;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("Message");
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        NotificationManagerCompat mNM = (NotificationManagerCompat) NotificationManagerCompat.from(context);
        mNM.cancel(3);

        // Cancel Scrolling to task where (task id = task id of notification) when MainActivity was started.
        SharedPreferences.Editor editor = context.getSharedPreferences("id_task", MODE_PRIVATE).edit();
        editor.putString("idName", "");
        editor.apply();
    }
}
