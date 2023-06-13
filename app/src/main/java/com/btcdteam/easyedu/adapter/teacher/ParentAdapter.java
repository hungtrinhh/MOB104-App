package com.btcdteam.easyedu.adapter.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.models.Parent;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Parent> list;
    private ParentItemListener listener;

    public interface ParentItemListener {
        void onItemClick(int position, Parent parent);

        void onOptionClick(int position, Parent parent);
    }

    public ParentAdapter(List<Parent> list, ParentItemListener listener) {
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);
        return new ParentVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Parent parent = list.get(position);

        ParentVH parentVH = (ParentVH) holder;
        parentVH.tvParentName.setText(parent.getName());

        parentVH.itemParent.setOnClickListener(v -> {
            listener.onItemClick(position, parent);
        });

        parentVH.btnOption.setOnClickListener(v -> {
            listener.onOptionClick(position, parent);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ParentVH extends RecyclerView.ViewHolder {
        TextView tvParentName;
        ImageView btnOption;
        LinearLayout itemParent;

        public ParentVH(@NonNull View itemView) {
            super(itemView);
            tvParentName = itemView.findViewById(R.id.tv_parent_name);
            btnOption = itemView.findViewById(R.id.img_parent_option);
            itemParent = itemView.findViewById(R.id.item_parent);
        }
    }
}
