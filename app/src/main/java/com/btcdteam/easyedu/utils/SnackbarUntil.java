package com.btcdteam.easyedu.utils;

import android.graphics.Color;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarUntil {
    public static void showInfo(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.parseColor("#3787FF")).show();
    }

    public static void showWarning(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.parseColor("#FF8F00")).show();
    }

    public static void showError(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.parseColor("#D50000")).show();
    }
}
