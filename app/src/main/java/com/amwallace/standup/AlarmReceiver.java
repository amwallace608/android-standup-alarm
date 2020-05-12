package com.amwallace.standup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        //init notification manager
        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        //deliver notification
        deliverNotification(context);
    }

    //deliver notification method
    private void deliverNotification(Context context){
        //create intent for notification content
        Intent contentIntent = new Intent(context, MainActivity.class);
        //pending intent from content intent w/ notification ID and update current flag
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //build notification w/ icon, contentIntent, config notification options
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setContentIntent(contentPendingIntent)
                        .setContentTitle("Stand Up Alert")
                        .setContentText("Time to get up and move!")
                        .setSmallIcon(R.drawable.ic_move)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);

        //build and deliver notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
