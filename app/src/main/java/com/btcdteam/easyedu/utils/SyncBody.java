package com.btcdteam.easyedu.utils;

import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Student;

import java.util.List;

public class SyncBody {
    String classroom_id;
    List<Parent> parents;
    List<Student> students;
}
