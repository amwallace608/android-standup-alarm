package com.amwallace.standup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    //constant for notification ID
    private static final int NOTIFICATION_ID = 0;
    //constant for notification channel ID
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate alarm toggle button
        ToggleButton alarmToggleBtn = (ToggleButton) findViewById(R.id.alarmToggleBtn);
        //init alarm manager
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //init notification manager w/ notification system service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //create notification channel method
        createNotificationChannel();

        //create notification intent for Alarm broadcast receiver
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        //check state of alarm
        boolean alarmSet = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        //set state of toggle button according to state of alarm
        alarmToggleBtn.setChecked(alarmSet);

        //create pending intent for notification intent
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarm toggle button onCheckedChanged listener
        alarmToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //display message to indicate alarm is on or off
                String toggleMessage;
                if(isChecked){
                    //alarm toggled on
                    toggleMessage = "Stand Up Alarm Is On";
                    //setup alarm time for 15 minutes from now
                    long notifyTime = SystemClock.elapsedRealtime()
                            + AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    //set repeating alarm with 15 minute intervals
                    if (alarmManager != null){
                        //wake device if asleep with elapsed realtime wakeup
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                notifyTime,
                                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                                notifyPendingIntent);
                    }
                } else {
                    //alarm toggled off
                    toggleMessage = "Stand Up Alarm Is Off";
                    //cancel notification
                    notificationManager.cancelAll();
                    //cancel alarm
                    if (alarmManager != null){
                        alarmManager.cancel(notifyPendingIntent);
                    }
                }
                //show message
                Toast.makeText(MainActivity.this, toggleMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    //create notification channel for API >= 26
    public void createNotificationChannel(){
        //check SDK version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //create notification channel w/ channel ID, message, high priority
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Stand up notification", NotificationManager.IMPORTANCE_HIGH);
            //config channel settings - enable lights, light color, vibration, description
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription("Notifies every 15 minutes to get up and move");
            //create channel w/ manager
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
