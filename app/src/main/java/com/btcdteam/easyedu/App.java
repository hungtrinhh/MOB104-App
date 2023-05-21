package com.btcdteam.easyedu;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.kongzue.dialogx.DialogX;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DialogX.init(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
