package com.vijay.service7;
/*

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        toast("Service Binded");
        MyNewAlarm newAlarm = new MyNewAlarm(this, getNextCalendar());
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        toast("Service Unbind");
        return super.onUnbind(intent);
    }

    private Calendar getNextCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 15);
        return calendar;
    }

    private Date getAlarmDate() {
        // getting Date Object from sharedPreferences
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("date", null);
        return gson.fromJson(json, Date.class);
    }

    @Override
    public boolean stopService(Intent name) {
        toast("Service STOPPED");
        return super.stopService(name);
    }

    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
*/
