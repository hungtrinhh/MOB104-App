package com.btcdteam.easyedu.adapter.teacher;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.models.StudentDetail;

import java.util.List;
import java.util.Locale;

public class ScorePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<StudentDetail> list;

    public void setList(List<StudentDetail> list) {
        this.list = list;
    }

    public ScorePreviewAdapter(Context context, List<StudentDetail> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StudentDetail student = list.get(position);
        StudentVH studentVH = (StudentVH) holder;
        ((StudentVH) holder).tvStudentName.setText(student.getName());
        if (student.getFinalScore() == null) {
            ((StudentVH) holder).tvFinalScore.setText("?");
            ((StudentVH) holder).tvFinalScore.setTextColor(Color.parseColor("#D50000"));
        } else {
            ((StudentVH) holder).tvFinalScore.setText(String.format(Locale.US, "%.1f", student.getFinalScore()));
            ((StudentVH) holder).tvFinalScore.setTextColor(Color.parseColor("#3787FF"));
        }
        if (student.getRegularScore1() == null) {
            ((StudentVH) holder).tvRegularScore1.setText("?");
            ((StudentVH) holder).tvRegularScore1.setTextColor(Color.parseColor("#D50000"));
        } else {
            ((StudentVH) holder).tvRegularScore1.setText(String.format(Locale.US, "%.1f", student.getRegularScore1()));
            ((StudentVH) holder).tvRegularScore1.setTextColor(Color.parseColor("#3787FF"));
        }
        if (student.getRegularScore2() == null) {
            ((StudentVH) holder).tvRegularScore2.setText("?");
            ((StudentVH) holder).tvRegularScore2.setTextColor(Color.parseColor("#D50000"));
        } else {
            ((StudentVH) holder).tvRegularScore2.setText(String.format(Locale.US, "%.1f", student.getRegularScore2()));
            ((StudentVH) holder).tvRegularScore2.setTextColor(Color.parseColor("#3787FF"));
        }
        if (student.getRegularScore3() == null) {
            ((StudentVH) holder).tvRegularScore3.setText("?");
            ((StudentVH) holder).tvRegularScore3.setTextColor(Color.parseColor("#D50000"));
        } else {
            ((StudentVH) holder).tvRegularScore3.setText(String.format(Locale.US, "%.1f", student.getRegularScore3()));
            ((StudentVH) holder).tvRegularScore3.setTextColor(Color.parseColor("#3787FF"));
        }
        if (student.getMidtermScore() == null) {
            ((StudentVH) holder).tvMidtermScore.setText("?");
            ((StudentVH) holder).tvMidtermScore.setTextColor(Color.parseColor("#D50000"));
        } else {
            ((StudentVH) holder).tvMidtermScore.setText(String.format(Locale.US, "%.1f", student.getMidtermScore()));
            ((StudentVH) holder).tvMidtermScore.setTextColor(Color.parseColor("#3787FF"));
        }
        if (student.getSemester() == 1) {
            ((StudentVH) holder).option.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.semeter01));
        } else {
            ((StudentVH) holder).option.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.semeter02));
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }


    public class StudentVH extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvRegularScore1, tvRegularScore2, tvRegularScore3, tvMidtermScore, tvFinalScore;
        CardView itemStudent;
        ImageView option;

        public StudentVH(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvRegularScore1 = itemView.findViewById(R.id.tv_regular_score1);
            tvRegularScore2 = itemView.findViewById(R.id.tv_regular_score2);
            tvRegularScore3 = itemView.findViewById(R.id.tv_regular_score3);
            tvMidtermScore = itemView.findViewById(R.id.tv_midterm_score);
            tvFinalScore = itemView.findViewById(R.id.tv_final_score);
            itemStudent = itemView.findViewById(R.id.item_student);
            option = itemView.findViewById(R.id.option);
        }
    }

}
