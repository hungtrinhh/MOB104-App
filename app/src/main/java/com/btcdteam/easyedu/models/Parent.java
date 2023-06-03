package com.btcdteam.easyedu.models;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Parent {
    @SerializedName("parent_id")
    private String id;
    @SerializedName("parent_name")
    private String name;
    @SerializedName("parent_email")
    private String email;
    private String password;
    @SerializedName("parent_dob")
    private String dob;
    @SerializedName("parent_fcmtoken")
    private String fcmToken;
    @SerializedName("parent_phone")
    private String phone;


    public Parent(String name, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.password = "123456";
        this.phone = phone;
    }

    public Parent() {

    }

    public void makeId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
