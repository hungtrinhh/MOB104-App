package com.btcdteam.easyedu.adapter.parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;

public class StudentDetailAdapter extends RecyclerView.Adapter<StudentDetailAdapter.StudentDetailVH> {

    @NonNull
    @Override
    public StudentDetailAdapter.StudentDetailVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_detail, parent, false);
        return new StudentDetailVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentDetailVH holder, int position) {

        holder.swTerm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                //set điểm kì 2
            }else{
                // set điểm kì 1
            }
        });

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class StudentDetailVH extends RecyclerView.ViewHolder {
        TextView tvStudentName,
                tvStudentDateOfBirth,
                tvStudentGender,
                tvRegularScore1,
                tvRegularScore2,
                tvRegularScore3,
                tvMidtermScore,
                tvFinalScore,
                tvTotalScore,
                Tvtotal,
                tvAvg,
                tvClassName,
                tvSubject,
                tvTeacherName;
        Switch swTerm;

        public StudentDetailVH(@NonNull View itemView) {
            super(itemView);
            //student
            tvStudentName = itemView.findViewById(R.id.tv_student_detail_name);
            tvStudentDateOfBirth = itemView.findViewById(R.id.tv_student_detail_dob);
            tvStudentGender = itemView.findViewById(R.id.tv_student_detail_gender);
            tvRegularScore1 = itemView.findViewById(R.id.tv_student_detail_regular_1);
            tvRegularScore2 = itemView.findViewById(R.id.tv_student_detail_regular_2);
            tvRegularScore3 = itemView.findViewById(R.id.tv_student_detail_regular_3);
            tvMidtermScore = itemView.findViewById(R.id.tv_student_detail_midterm);
            tvFinalScore = itemView.findViewById(R.id.tv_student_detail_final);
            tvTotalScore = itemView.findViewById(R.id.tv_student_detail_total);
            tvAvg = itemView.findViewById(R.id.tv_student_detail_avg);
            swTerm = itemView.findViewById(R.id.sw_student_detail_term);
            Tvtotal = itemView.findViewById(R.id.Tvtotal);

            tvClassName = itemView.findViewById(R.id.tv_student_detail_class);
            tvSubject = itemView.findViewById(R.id.tv_student_detail_subject);
            tvTeacherName = itemView.findViewById(R.id.tv_student_detail_teacher_name);
        }
    }
}
