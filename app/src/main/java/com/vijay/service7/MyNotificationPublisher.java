package com.vijay.service7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyNotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new
                    NotificationChannel("10001",
                    "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int id = intent.getIntExtra("notification-id", 0);
        assert notificationManager != null;
        notificationManager.notify(id, notification);

        createAlarm(context);
    }

    void createAlarm(Context context) {
        Calendar calendarObj = Calendar.getInstance();
        Date date = getAlarmDate(context);

        calendarObj.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendarObj.set(Calendar.MINUTE, date.getMinutes());
        calendarObj.set(Calendar.SECOND, date.getSeconds());

        calendarObj.add(Calendar.SECOND, 15);

        MyNewAlarm myNewAlarm = new MyNewAlarm(context, calendarObj);
    }

    private Date getAlarmDate(Context context) {
        // getting Date Object from sharedPreferences
        SharedPreferences sp = context.getSharedPreferences("data", context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("date", null);
        return gson.fromJson(json, Date.class);
    }

}