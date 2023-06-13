package com.btcdteam.easyedu.adapter.teacher;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.utils.ParentPreview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackVH> {
    public static final int FILTER_NOT_INSTALL_APP = 0;
    public static final int FILTER_INSTALL_APP = 1;
    public static final int FILTER_DEFAULT = 2;
    private List<ParentPreview> list;
    private List<ParentPreview> listData;
    private FeedbackCallback feedbackCallback;
    private boolean isShowCheckBox = false;
    private static int MODE = FILTER_DEFAULT;
    private Map<String, ParentPreview> selectedToken;

    public FeedbackAdapter(List<ParentPreview> list, FeedbackCallback feedbackCallback) {
        this.list = list;
        this.feedbackCallback = feedbackCallback;
        listData = new ArrayList<>(list);
        selectedToken = list.stream().collect(Collectors.toMap(ParentPreview::getStudentId, Function.identity()));
    }

    public void toggleShowCheckbox() {
        if (isShowCheckBox) {
            isShowCheckBox = false;
        } else {
            isShowCheckBox = true;
        }
        notifyDataSetChanged();
    }

    public interface FeedbackCallback {
        void callWithPhoneNumber(ParentPreview parent);

        void smsTextWithPhoneNumber(ParentPreview parent);

        void onCheckStateChange(boolean isChecked);
    }

    @NonNull
    @Override
    public FeedbackVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_parent_selector, parent, false);
        return new FeedbackVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackVH holder, int position) {
        ParentPreview parent = listData.get(position);
        bindParentDefault(holder, parent);
    }

    public void setMode(int mode) {
        MODE = mode;
        listData.clear();
        if (MODE == FILTER_NOT_INSTALL_APP) {
            listData = list.stream().filter(item -> item.getFcmToken() == null || item.getFcmToken().equals("")).collect(Collectors.toList());
        } else if (MODE == FILTER_INSTALL_APP) {
            listData = list.stream().filter(item -> item.getFcmToken() != null && !item.getFcmToken().equals("")).collect(Collectors.toList());
        } else {
            listData.addAll(list);
        }
        selectedToken = listData.stream().collect(Collectors.toMap(ParentPreview::getStudentId, Function.identity()));
        notifyDataSetChanged();
    }

    public Map<String, ParentPreview> getSelectedToken() {
        return selectedToken;
    }

    public void resetSelectBox() {
        if (selectedToken.size() > 0) {
            selectedToken.clear();
        } else {
            selectedToken = listData.stream().collect(Collectors.toMap(ParentPreview::getStudentId, Function.identity()));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    private void bindParentDefault(FeedbackVH holder, ParentPreview parent) {
        if (parent.getFcmToken() == null || parent.getFcmToken().equals("")) {
            holder.btnSms.setColorFilter(Color.parseColor("#D50000"));
        } else {
            holder.btnSms.setColorFilter(Color.parseColor("#3787FF"));
        }
        if (isShowCheckBox) {
            holder.cbParentCheck.setVisibility(View.VISIBLE);
        } else {
            holder.cbParentCheck.setVisibility(View.GONE);
        }
        holder.cbParentCheck.setChecked(selectedToken.get(parent.getStudentId()) != null);
        holder.tvContentFeedback.setText(parent.getName());
        holder.tvDateFeedback.setText(parent.getPhone());
        holder.tvStudent.setText(parent.getStudentName());
        holder.btnPhone.setOnClickListener(v -> feedbackCallback.callWithPhoneNumber(parent));
        holder.btnSms.setOnClickListener(v -> feedbackCallback.smsTextWithPhoneNumber(parent));
        holder.cbParentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedToken.put(parent.getStudentId(), parent);
                    feedbackCallback.onCheckStateChange(true);
                } else {
                    selectedToken.remove(parent.getStudentId());
                    feedbackCallback.onCheckStateChange(false);
                }
            }
        });
    }

    public class FeedbackVH extends RecyclerView.ViewHolder {
        TextView tvContentFeedback, tvDateFeedback, tvStudent;
        LinearLayout item;
        ImageView btnSms, btnPhone;
        CheckBox cbParentCheck;

        public FeedbackVH(@NonNull View itemView) {
            super(itemView);
            tvContentFeedback = itemView.findViewById(R.id.tv_feedback_content);
            tvDateFeedback = itemView.findViewById(R.id.tv_feedback_date);
            item = itemView.findViewById(R.id.item_feedback);
            btnSms = itemView.findViewById(R.id.img_feedback_sms);
            tvStudent = itemView.findViewById(R.id.tv_feedback_student);
            cbParentCheck = itemView.findViewById(R.id.cb_parent_check);
            btnPhone = itemView.findViewById(R.id.img_feedback_call);
        }
    }
}
