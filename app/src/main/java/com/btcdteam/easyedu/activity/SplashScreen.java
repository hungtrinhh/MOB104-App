package com.btcdteam.easyedu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shared = getSharedPreferences("SESSION", MODE_PRIVATE);
        if (shared.getAll().size() == 0) {
            startActivity(new Intent(this, AuthActivity.class));
        } else if (shared.getString("session_role", "").equalsIgnoreCase("teacher")) {
            startActivity(new Intent(this, TeacherActivity.class));
        } else {
            startActivity(new Intent(this, ParentActivity.class));
        }
        finish();
    }
}
