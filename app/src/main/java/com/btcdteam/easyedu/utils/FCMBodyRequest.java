package com.btcdteam.easyedu.utils;

import java.util.ArrayList;

public class FCMBodyRequest {
    public Notification notification;
    public ArrayList<String> registration_ids;

    public void setNotification(String title, String body) {
        notification = new Notification(title, body);
    }

}

class Notification {
    String title, body;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }
}