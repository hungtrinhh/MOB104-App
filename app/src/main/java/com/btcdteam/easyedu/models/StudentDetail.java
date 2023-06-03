package com.btcdteam.easyedu.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudentDetail implements Serializable {
    private int id;
    @SerializedName("classroom_id")
    private int classroomId;
    @SerializedName("student_id")
    private String studentId;
    @SerializedName("student_name")
    private String name;
    @SerializedName("student_dob")
    private String dob;
    @SerializedName("regular_score_1")
    private Float regularScore1;
    @SerializedName("regular_score_2")
    private Float regularScore2;
    @SerializedName("regular_score_3")
    private Float regularScore3;
    @SerializedName("midterm_score")
    private Float midtermScore;
    @SerializedName("final_score")
    private Float finalScore;
    private int semester;
    @SerializedName("parent_name")
    private String parentName;
    @SerializedName("parent_email")
    private String parentEmail;
    @SerializedName("parent_dob")
    private String parentDob;
    @SerializedName("parent_phone")
    private String parentPhone;
    @SerializedName("student_gender")
    private String studentGender;


    public StudentDetail(int id, int classroomId, String studentId, String name, int semester) {
        this.id = id;
        this.classroomId = classroomId;
        this.studentId = studentId;
        this.name = name;
        this.semester = semester;
    }

    public StudentDetail() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Float getRegularScore1() {
        return regularScore1;
    }

    public void setRegularScore1(Float regularScore1) {
        this.regularScore1 = regularScore1;
    }

    public Float getRegularScore2() {
        return regularScore2;
    }

    public void setRegularScore2(Float regularScore2) {
        this.regularScore2 = regularScore2;
    }

    public Float getRegularScore3() {
        return regularScore3;
    }

    public void setRegularScore3(Float regularScore3) {
        this.regularScore3 = regularScore3;
    }

    public Float getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(Float midtermScore) {
        this.midtermScore = midtermScore;
    }

    public Float getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Float finalScore) {
        this.finalScore = finalScore;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getParentDob() {
        return parentDob;
    }

    public void setParentDob(String parentDob) {
        this.parentDob = parentDob;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(String studentGender) {
        this.studentGender = studentGender;
    }
}
