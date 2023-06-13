package com.btcdteam.easyedu.adapter.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Student;

import java.util.List;
import java.util.Map;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewVH> {
    private List<Student> list;
    private Map<String, Parent> parentMap;

    public PreviewAdapter(List<Student> list, Map<String, Parent> parentMap) {
        this.list = list;
        this.parentMap = parentMap;
    }

    @NonNull
    @Override
    public PreviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview, parent, false);
        return new PreviewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewVH holder, int position) {
        if (position % 2 == 0) {
            holder.item.setBackgroundResource(R.color.bg_primary);
        }
        Student student = list.get(position);
        Parent parent = parentMap.get(student.getParentId());
        holder.tvStudentName.setText(student.getName());
        holder.tvParentName.setText(parent.getName());
        holder.tvStudentDOB.setText(student.getDob());
        holder.tvParentPhoneNumber.setText(parent.getPhone());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PreviewVH extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvParentName, tvStudentDOB, tvParentPhoneNumber;
        TableLayout item;

        public PreviewVH(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_preview_student_name);
            tvParentName = itemView.findViewById(R.id.tv_preview_parent_name);
            tvStudentDOB = itemView.findViewById(R.id.tv_preview_student_dob);
            tvParentPhoneNumber = itemView.findViewById(R.id.tv_preview_parent_phone_number);
            item = itemView.findViewById(R.id.item_preview);
        }
    }
}
