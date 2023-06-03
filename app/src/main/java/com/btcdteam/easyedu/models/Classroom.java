package com.btcdteam.easyedu.models;

import com.google.gson.annotations.SerializedName;

public class Classroom {
    @SerializedName("classroom_id")
    private int id;
    @SerializedName("classroom_name")
    private String name;
    @SerializedName("classroom_description")
    private String description;
    @SerializedName("classroom_subject")
    private String subject;
    @SerializedName("teacher_id")
    private int teacherId;
    @SerializedName("classroom_count")
    private int count;

    public Classroom(String name, String description, String subject, int teacherId) {
        this.name = name;
        this.description = description;
        this.subject = subject;
        this.teacherId = teacherId;
    }

    public Classroom() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
