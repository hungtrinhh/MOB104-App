package com.btcdteam.easyedu.utils;

import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Student;

import java.util.List;

public class SyncBody {
    int classroom_id;
    List<Parent> parents;
    List<Student> students;

    public SyncBody(int classroom_id, List<Parent> parents, List<Student> students) {
        this.classroom_id = classroom_id;
        this.parents = parents;
        this.students = students;
    }

    public SyncBody() {
    }
}
