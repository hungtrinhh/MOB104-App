package com.btcdteam.easyedu.fragments.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.adapter.parent.NotificationAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Feedback;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.SnackbarUntil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {
    private List<Feedback> list;
    private RecyclerView rcv;
    private ImageView btnBack;
    private NotificationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcv_parent_noti);
        btnBack = view.findViewById(R.id.img_parent_noti_back);

        getListFeedback();
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private void getListFeedback() {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getFeedbackByStudent(getArguments().getString("student_id"));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    Type type = new TypeToken<List<Feedback>>() {
                    }.getType();
                    list = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    adapter = new NotificationAdapter(list);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    rcv.setLayoutManager(manager);
                    rcv.setAdapter(adapter);
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