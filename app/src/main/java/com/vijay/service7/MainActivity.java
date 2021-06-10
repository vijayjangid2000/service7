package com.vijay.service7;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    Button btn_createAlarm, btn_stop;
    TextView tv_message;
    TimePicker timePicker;

    static String alarmTimeDetail, timerMessageToTextView;
    Timer timer;

    EditText et_userNotifyMessage;
    MyNewAlarm myNewAlarm;
    Date nextDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_createAlarm = findViewById(R.id.save);
        tv_message = findViewById(R.id.tv_message);
        timePicker = findViewById(R.id.timePicker);
        et_userNotifyMessage = findViewById(R.id.customMessage);
        btn_createAlarm.setOnClickListener(view -> createAlarm());
        btn_stop = findViewById(R.id.stop);
        btn_stop.setOnClickListener(view -> {
            new MyNewAlarm(MainActivity.this, null);
            timer.cancel();
            tv_message.setText("");
        });
        if (getAlarmDate() != null) startUiTimer();
    }

    void createAlarm() {
        Calendar calendarObj = Calendar.getInstance();
        calendarObj.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendarObj.set(Calendar.MINUTE, timePicker.getMinute());
        calendarObj.add(Calendar.SECOND, 7);
        myNewAlarm = new MyNewAlarm(this, calendarObj);
        nextDate = calendarObj.getTime();
        startUiTimer();
        updateTextView();
        // then broadcast receiver binds to it, then
    }


    private void updateTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "h:mm:ss a", Locale.getDefault());
        alarmTimeDetail = "Next Alarm Tomorrow: " + sdf.format(nextDate);
    }

    private Date getAlarmDate() {
        // getting Date Object from sharedPreferences
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("date", null);
        return gson.fromJson(json, Date.class);
    }

    /* ---> shows remaining time in textView  */
    void startUiTimer() {

        if (getAlarmDate().getTime() < new Date().getTime()) return;

        if (timer != null) timer.cancel();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(() -> {
                    nextDate = getAlarmDate();
                    timerMessageToTextView = "Remaining Time: "
                            + calculateRemaining(new Date(), nextDate);
                    updateTextView();
                    tv_message.setText(alarmTimeDetail + "\n" + timerMessageToTextView);
                    timePicker.setHour(nextDate.getHours());
                    timePicker.setMinute(nextDate.getMinutes());
                });

            }
        }, 0, 1000);

    }

    private String calculateRemaining(Date prev, Date next) {
        // return difference between two date object in hh:mm:ss format
        long total = next.getTime() - prev.getTime();
        total = total / 1000;
        long hours = total / 3600;
        long minutes = (total - hours * 3600) / 60;
        long seconds = ((total - (hours * 3600) - (minutes * 60)));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

}
