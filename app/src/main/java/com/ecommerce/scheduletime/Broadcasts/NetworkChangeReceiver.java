package com.ecommerce.scheduletime.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.ecommerce.scheduletime.Sync.SyncDataBaseServiceUpdate;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                Toast.makeText(context, "is Online", Toast.LENGTH_SHORT).show();

                /** -- Start calling {@link SyncDataBaseServiceUpdate} after CalendarFragment is started or refreshed -- */
                Intent intent1 = new Intent(context, SyncDataBaseServiceUpdate.class);
                context.startService(intent1);
            } else {
                Toast.makeText(context, "is Not Online", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            // Should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
