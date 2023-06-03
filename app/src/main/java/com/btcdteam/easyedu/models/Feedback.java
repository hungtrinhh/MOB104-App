package com.btcdteam.easyedu.models;

import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("feedback_id")
    private int id;
    @SerializedName("feedback_content")
    private String content;
    @SerializedName("feedback_date")
    private long date;
    @SerializedName("classroom_id")
    private int classroomId;
    @SerializedName("student_id")
    private String studentId;
    @SerializedName("classroom_name")
    private String classroomName;
    @SerializedName("classroom_subject")
    private String classroomSubject;

    public Feedback(String content, int classroomId, String studentId) {
        this.content = content;
        this.classroomId = classroomId;
        this.studentId = studentId;
        date = System.currentTimeMillis();
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getClassroomSubject() {
        return classroomSubject;
    }

    public void setClassroomSubject(String classroomSubject) {
        this.classroomSubject = classroomSubject;
    }

    public Feedback() {
        date = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int teacherId) {
        this.classroomId = teacherId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
