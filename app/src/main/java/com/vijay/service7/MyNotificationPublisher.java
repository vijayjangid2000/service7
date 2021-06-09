package com.vijay.service7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.vijay.service7.MainActivity.myInterface;

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

        // to recreate the alarm for next time
        myInterface.whenNotificationCome();
    }

    // interface used (like passing a method from the activity using interface object)
    // interface is not serializable so I used static Object and imported here
    public interface ReceiverInterface{
        void whenNotificationCome();
    }
}