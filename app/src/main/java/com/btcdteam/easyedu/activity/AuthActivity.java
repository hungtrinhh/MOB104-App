package com.btcdteam.easyedu.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.btcdteam.easyedu.R;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        askNotificationPermission();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    new MessageDialog("Thông báo", "Ứng dụng chưa được cấp quyền thông báo, bạn có thể bị bỏ lỡ những thông tin quan trọng!"
                            , "Cài đặt", "Đóng").setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                            return false;
                        }
                    }).show();
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                new MessageDialog("Thông báo", "Ứng dụng chưa được cấp quyền thông báo, bạn có thể bị bỏ lỡ những thông tin quan trọng!"
                        , "Cài đặt", "Đóng").setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog dialog, View v) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        return false;
                    }
                }).show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}