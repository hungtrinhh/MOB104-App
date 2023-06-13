package com.btcdteam.easyedu.adapter.parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.models.Feedback;
import com.btcdteam.easyedu.utils.TimeUtils;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiVH> {
    private List<Feedback> list;

    public NotificationAdapter(List<Feedback> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NotiVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotiVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiVH holder, int position) {
        Feedback feedback = list.get(position);
        holder.tvTitle.setText(feedback.getClassroomName());
        holder.tvDate.setText(TimeUtils.convertToDate(feedback.getDate()) + " - " + TimeUtils.timeAgo(feedback.getDate()));
        holder.tvContent.setText("Nội dung: " + feedback.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotiVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvContent;

        public NotiVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_noti_title);
            tvDate = itemView.findViewById(R.id.tv_noti_date);
            tvContent = itemView.findViewById(R.id.tv_noti_content);
        }
    }
}
