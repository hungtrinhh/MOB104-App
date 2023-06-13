package com.btcdteam.easyedu.adapter.parent;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.utils.PreviewScore;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviewStudentAdapter extends RecyclerView.Adapter<PreviewStudentAdapter.StudentVH> {
    private Map<String, PreviewScore> previewScoreMap1, getPreviewScoreMap2;
    private List<PreviewScore> scores1, scores2;
    private StudentItemListener listener;

    public interface StudentItemListener {
        void onItemClick(String classId);
    }

    public PreviewStudentAdapter(List<PreviewScore> scores1, List<PreviewScore> scores2, StudentItemListener listener) {
        this.scores1 = scores1;
        this.scores2 = scores2;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_parent, parent, false);
        return new StudentVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentVH holder, int position) {
        PreviewScore previewScore1 = scores1.get(position);
        PreviewScore previewScore2 = scores2.get(position);
        holder.tvName.setText(previewScore1.classroom_name);
        holder.tvSubject.setText(previewScore1.classroom_subject);
        if (previewScore1.isDone() && previewScore2.isDone()) {
            float avgScore = (previewScore1.getAvg() + previewScore2.getAvg() * 2) / 3;
            holder.tvStudentStatus.setText("Đã xong");
            holder.tvStudentStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvAvgScore.setText(String.format(Locale.US, "%.2f", avgScore));
            if (avgScore < 5) {
                holder.tvAvgScore.setTextColor(Color.parseColor("#D50000"));
            }
        } else {
            holder.tvStudentStatus.setText("Đang học");
            holder.tvAvgScore.setText("?");
        }
        holder.item.setOnClickListener(v -> {
            listener.onItemClick(previewScore1.classroom_id);
        });
    }

    @Override
    public int getItemCount() {
        return scores1.size();
    }

    public class StudentVH extends RecyclerView.ViewHolder {
        TextView tvName, tvSubject, tvAvgScore, tvStudentStatus;
        LinearLayout item;

        public StudentVH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_parent_student_name);
            tvSubject = itemView.findViewById(R.id.tv_student_parent_subject);
            tvAvgScore = itemView.findViewById(R.id.tv_student_parent_avg_score);
            tvStudentStatus = itemView.findViewById(R.id.tv_student_parent_status);
            item = itemView.findViewById(R.id.item_parent_student);
        }
    }
}
