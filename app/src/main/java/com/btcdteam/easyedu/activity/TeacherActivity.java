package com.btcdteam.easyedu.activity;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.btcdteam.easyedu.BuildConfig;
import com.btcdteam.easyedu.R;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TeacherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 30) {
            ActivityResultLauncher<Intent> storagePermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (!Environment.isExternalStorageManager()) {
                        new MessageDialog(R.string.notification, R.string.permission_noti_1, R.string.close).show();
                    }
                }
            });
            if (!Environment.isExternalStorageManager()) {
                new MessageDialog(R.string.notification, R.string.permission_request, R.string.go_to, R.string.close).setCancelButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog dialog, View v) {
                        System.exit(0);
                        return false;
                    }
                }).setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog dialog, View v) {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        //intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri = Uri.fromParts("package", TeacherActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        storagePermissionResultLauncher.launch(intent);
                        return false;
                    }
                }).show();

            }
        } else {
            ActivityResultLauncher<String> requestPermissionLauncher =
                    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (!isGranted) {
                            new MessageDialog(R.string.notification, R.string.permission_noti_1, R.string.close).show();
                        }
                    });
            if (ContextCompat.checkSelfPermission(TeacherActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(TeacherActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new MessageDialog(R.string.notification, R.string.permission_request, R.string.grant, R.string.close).setCancelButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            System.exit(0);
                            return false;
                        }
                    }).setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            return false;
                        }
                    }).show();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }

}
