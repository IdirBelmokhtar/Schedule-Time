package com.ecommerce.scheduletime.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.ecommerce.scheduletime.Sync.SyncDataBaseServiceUpdate;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // do your task here
            Log.d("MyBroadcastReceiver", "Task running...");
            // schedule the task again in an hour
            handler.postDelayed(this, 60 * 60 * 1000); // Repeat Every Hour.
            //handler.postDelayed(this, 60 * 1000); // Repeat Every minute.
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        /** -- Start calling {@link SyncDataBaseServiceUpdate} when BOOT_COMPLETED or MY_PACKAGE_REPLACED -- */
        Intent intent1 = new Intent(context, SyncDataBaseServiceUpdate.class);
        context.startService(intent1);

        handler.post(runnable);
    }
}
