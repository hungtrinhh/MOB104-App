package com.btcdteam.easyedu.models;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Student {
    private String id;
    private String name;
    private String gender;
    private String dob;
    @SerializedName("parent_phone")
    private String parentPhone;
    @SerializedName("parent_id")
    private String parentId;

    public Student(String name, String gender, String dob, String parentId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.parentId = parentId;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public Student() {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
