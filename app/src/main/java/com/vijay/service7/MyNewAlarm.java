package com.vijay.service7;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class MyNewAlarm {

    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private Context context;
    private Date nextAlarmTime;

    // give date and context and it creates a notification
    public MyNewAlarm(Context context, Calendar alarmTime) {
        this.context = context;
        if(alarmTime == null) cancelAll();
        else setNextAlarm(alarmTime);
    }

    private void setNextAlarm(Calendar calendarObj) {
        nextAlarmTime = calendarObj.getTime();
        saveAlarmDate(); // also save date in sharedPreferences
        // ---> Creating a notification object and scheduling
        scheduleNotification(createNotification("HERE MESSAGE")
                , nextAlarmTime.getTime());

        toast("Next Reminder created");
    }

    // this is to create an notification then we pass this into scheduleN..
    private Notification createNotification(String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, default_notification_channel_id);
        builder.setContentTitle("Hey, This is Reminder");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_baseline_alarm_on_24);
        builder.setColor(Color.GREEN);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }

    private void cancelAll() {

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION,
                createNotification("HERE MESSAGE"));

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context.getApplicationContext(), 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService
                (Context.ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.cancel(pendingIntent); // remove old alarms
        toast("All Reminders deleted");
    }

    private void scheduleNotification(Notification notification, long delay) {

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.cancel(pendingIntent); // remove old alarms
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    // We save the date in sharedPreferences,
    // So that we can find if there are reminders already created.
    // If user use it for first time then it is null,
    // means we will create alarm

    private void saveAlarmDate() {
        // setting Date Object from sharedPreferences
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        String json = new Gson().toJson(nextAlarmTime);
        spEdit.putString("date", json);
        spEdit.apply();
    }

    private void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
