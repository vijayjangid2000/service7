package com.vijay.service7;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements MyNotificationPublisher.ReceiverInterface {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    Button tv_saveAlarm;
    TextView tv_message;
    Calendar myCalendar = Calendar.getInstance();
    TimePicker timePicker;

    static String alarmTimeDetail, timerMessageToTextView;
    Date nextAlarmTime;
    Timer timerBackground;

    EditText et_userNotifyMessage;

    static MyNotificationPublisher.ReceiverInterface myInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_saveAlarm = findViewById(R.id.save);
        tv_message = findViewById(R.id.tv_message);
        timePicker = findViewById(R.id.timePicker);
        et_userNotifyMessage = findViewById(R.id.customMessage);

        tv_saveAlarm.setOnClickListener(view -> setNextAlarm());

        // we don't create reminder if we don't reminder
        if (getAlarmDate() == null) setNextAlarm();

        // this method will create the alarm
    }

    private void setNextAlarm() {

        // ---> setting time for next alarm

        Toast.makeText(this, "Reminder Loop Created", Toast.LENGTH_LONG).show();

        String myFormat = "h:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        myCalendar.set(Calendar.MINUTE, timePicker.getMinute());

        myCalendar.add(Calendar.SECOND, 15);
        //myCalendar.add(Calendar.DATE, 1);

        nextAlarmTime = myCalendar.getTime();
        setAlarmDate(); // also save date in sharedPreferences

        startUiTimer();

        // ---> Creating a notification object and scheduling
        scheduleNotification(createNotification(tv_saveAlarm.getText()
                .toString()), nextAlarmTime.getTime());

        alarmTimeDetail = "Next Alarm Tomorrow: " + sdf.format(nextAlarmTime);
        tv_message.setText(alarmTimeDetail);

    }

    // this is to create an notification then we pass this into scheduleN..
    private Notification createNotification(String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, default_notification_channel_id);
        if (et_userNotifyMessage.getText().length() < 2)
            builder.setContentTitle("Hey, This is Reminder");
        else builder.setContentTitle(et_userNotifyMessage.getText());
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_baseline_alarm_on_24);
        builder.setColor(Color.GREEN);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);

        return builder.build();
    }

    private void scheduleNotification(Notification notification, long delay) {

        // ---> Pending Intent is a service from alarm manager, so no need to create service
        // Check this link https://www.journaldev.com/27681/android-alarmmanager-broadcast-receiver-and-service

        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        myInterface = this; /* THE INTERFACE OBJECT */

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.cancel(pendingIntent); // remove old alarms
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }



    /* ---> This timer finds the difference and shows in hh:mm:ss in textView that's it  */
    void startUiTimer() {
        if (timerBackground == null) timerBackground = new Timer();
        else return;

        timerBackground.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(() -> {
                    timerMessageToTextView = "Remaining Time: "
                            + getTimeDifference(new Date(), nextAlarmTime);
                    tv_message.setText(alarmTimeDetail + "\n" + timerMessageToTextView);
                });

            }
        }, 0, 1000);
    }

    private String getTimeDifference(Date prev, Date next) {
        long total = next.getTime() - prev.getTime();
        total = total / 1000;
        long hours = total / 3600;
        long minutes = (total - hours * 3600) / 60;
        long seconds = ((total - (hours * 3600) - (minutes * 60)));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // We save the date in sharedPreferences, So that we can find if there are reminders already created.
    // If user use it for first time then it is null, means we will create alarm
    private void setAlarmDate() {
        // setting Date Object from sharedPreferences
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(nextAlarmTime);
        spEdit.putString(json, "date");
    }

    private Date getAlarmDate() {

        // getting Date Object from sharedPreferences

        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sp.getString("date", null);
        return gson.fromJson(json, Date.class);
    }

    @Override
    public void whenNotificationCome() {

        /* When broadcast receiver, receives a trigger then this method is also called
         * Use runOnUiThread for updating ui, Otherwise error: looper.prepare not called */

        runOnUiThread(() -> setNextAlarm());
    }


}