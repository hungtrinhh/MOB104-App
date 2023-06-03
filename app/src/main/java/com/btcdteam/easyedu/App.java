package com.btcdteam.easyedu;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;

import com.kongzue.dialogx.DialogX;

public class App extends Application {
    public static final String CHANNEL_ID = "teacher_feedback";

    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        createNotificationChannel();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
