package com.btcdteam.easyedu.adapter.teacher;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.models.StudentDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<StudentDetail> list;
    private List<StudentDetail> listold;
    private StudentItemListener listener;


    public interface StudentItemListener {
        void onItemClick(int position, StudentDetail student);

        void onOptionClick(int position, StudentDetail student, View v);
    }

    public void setList(List<StudentDetail> list) {
        this.list = list;
    }

    public StudentAdapter(List<StudentDetail> list, StudentItemListener listener) {
        this.listener = listener;
        this.list = list;
        this.listold = list;
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

        studentVH.itemStudent.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClick(holder.getAdapterPosition(), student);
        });

        studentVH.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onOptionClick(holder.getAdapterPosition(), student, view);
            }
        });
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    list = listold;
                } else {
                    ArrayList<StudentDetail> Mmusic = new ArrayList<>();
                    for (StudentDetail o : listold) {
                        if (o.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            Mmusic.add(o);
                        }
                    }
                    list = Mmusic;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<StudentDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
