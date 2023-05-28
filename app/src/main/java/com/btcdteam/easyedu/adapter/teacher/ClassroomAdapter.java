package com.btcdteam.easyedu.adapter.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.models.Classroom;

import java.util.List;
import java.util.Random;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomVH> {
    private List<Classroom> list;
    private ClassRoomItemListener listener;
    private int random;

    public interface ClassRoomItemListener {
        void onItemLongClick(int position, Classroom classroom, View view);

        void onItemClick(int position, Classroom classroom);

    }

    public ClassroomAdapter(List<Classroom> list, ClassRoomItemListener listene) {
        this.list = list;
        this.listener = listene;
    }

    public void setList(List<Classroom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ClassroomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassroomVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomVH holder, int position) {
        random = new Random().nextInt(9) + 1;
        Classroom classroom = list.get(position);
        holder.tvClassName.setText(classroom.getName());
        holder.tvClassDescription.setText(classroom.getDescription());
        holder.tvQuantity.setText("Học sinh: " + classroom.getCount());

        holder.itemClass.setOnLongClickListener(v -> {
            listener.onItemLongClick(position, classroom, v);
            return true;
        });

        holder.itemClass.setOnClickListener(v -> {
            listener.onItemClick(position, classroom);
        });

        switch (random) {
            case 1:
                holder.imgTemplate.setImageResource(R.drawable.partyboto);
                break;
            case 2:
                holder.imgTemplate.setImageResource(R.drawable.loving);
                break;
            case 3:
                holder.imgTemplate.setImageResource(R.drawable.lol);
                break;
            case 4:
                holder.imgTemplate.setImageResource(R.drawable.kiss);
                break;
            case 5:
                holder.imgTemplate.setImageResource(R.drawable.butto);
                break;
            case 6:
                holder.imgTemplate.setImageResource(R.drawable.cold_flame);
                break;
            case 7:
                holder.imgTemplate.setImageResource(R.drawable.cry);
                break;
            case 8:
                holder.imgTemplate.setImageResource(R.drawable.eyes);
                break;
            case 9:
                holder.imgTemplate.setImageResource(R.drawable.screamboto);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ClassroomVH extends RecyclerView.ViewHolder {
        CardView itemClass;
        TextView tvClassName, tvClassDescription, tvQuantity;
        ImageView imgTemplate;

        public ClassroomVH(@NonNull View itemView) {
            super(itemView);
            itemClass = itemView.findViewById(R.id.item_class);
            tvClassName = itemView.findViewById(R.id.tv_item_class_name);
            tvClassDescription = itemView.findViewById(R.id.tv_item_class_description);
            tvQuantity = itemView.findViewById(R.id.tv_item_class_quantity);
            imgTemplate = itemView.findViewById(R.id.img_item_class_template);
        }
    }
}
