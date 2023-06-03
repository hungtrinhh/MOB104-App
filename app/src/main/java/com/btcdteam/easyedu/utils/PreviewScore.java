package com.btcdteam.easyedu.utils;

public class PreviewScore {
    public String classroom_name, classroom_subject, classroom_id, teacher_name, teacher_phone, student_name, student_gender, student_dob;
    public Float regular_score_1, regular_score_2, regular_score_3, midterm_score, final_score;
    public int semester;

    public boolean isDone() {
        return regular_score_1 != null && regular_score_2 != null && regular_score_3 != null && midterm_score != null && final_score != null;
    }

    public Float getAvg() {
        return isDone() ? ((regular_score_1 + regular_score_2 + regular_score_3 + (2 * midterm_score) + (3 * final_score))) / 8 : null;
    }
}
