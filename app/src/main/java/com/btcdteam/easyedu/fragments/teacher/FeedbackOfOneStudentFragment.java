package com.btcdteam.easyedu.fragments.teacher;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.adapter.parent.NotificationAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Feedback;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.SnackbarUntil;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackOfOneStudentFragment extends Fragment {
    private RecyclerView rcv;
    private ImageView btnBack;
    private NotificationAdapter adapterNoti;
    private TextView tvTitle;
    private List<Feedback> listFeedback;
    private ExtendedFloatingActionButton fabFeedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback_of_one_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcv_feedback_of_one_student);
        btnBack = view.findViewById(R.id.img_feedback_back_of_one_student);
        tvTitle = view.findViewById(R.id.tv_feedback_title_of_one_student);
        fabFeedback = view.findViewById(R.id.fab_add_feedback_of_one_student);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        getListFeedback();

        fabFeedback.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("studentId", getArguments().getString("student_id"));
            bundle.putInt("classRoomId", getArguments().getInt("classroom_id"));
            Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_feedbackOfOneStudentFragment_to_studentDetailsFragment, bundle);
        });

        rcv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //scrollUp
                if (scrollY > oldScrollY + 20 && fabFeedback.isExtended()) {
                    fabFeedback.shrink();
                }

                //scrollDown
                if (scrollY < oldScrollY - 20 && !fabFeedback.isExtended()) {
                    fabFeedback.extend();
                }
            }
        });
    }

    private void getListFeedback() {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getFeedbackByStudent(getArguments().getString("student_id"));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i(">>>>>>", "onResponse: "+response.code());
                if (response.code() == 200) {
                    Type type = new TypeToken<List<Feedback>>() {
                    }.getType();
                    listFeedback = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    adapterNoti = new NotificationAdapter(listFeedback);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    rcv.setLayoutManager(manager);
                    rcv.setAdapter(adapterNoti);
                } else {
                    SnackbarUntil.showWarning(requireView(), "Không có thông báo nào!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                SnackbarUntil.showError(requireView(), "Không thể kết nối tới máy chủ!");
            }
        });
    }

}