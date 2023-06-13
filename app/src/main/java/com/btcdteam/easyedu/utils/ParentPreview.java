package com.btcdteam.easyedu.utils;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ParentPreview {
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
    @SerializedName("student_id")
    private String studentId;
    @SerializedName("student_name")
    private String studentName;

    public ParentPreview(String name, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.password = "123456";
        this.phone = phone;
    }

    public ParentPreview() {

    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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
