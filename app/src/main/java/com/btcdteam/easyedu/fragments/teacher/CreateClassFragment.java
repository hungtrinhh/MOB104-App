package com.btcdteam.easyedu.fragments.auth.teacher;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Classroom;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.ProgressBarDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateClassFragment extends Fragment {
    TextInputLayout inputLayoutClassName, inputLayoutSubject, inputLayoutDescription;
    TextInputEditText edClassName, edSubject, edDescription;
    Button btnCreateClass;
    ImageView btnBack;
    ProgressBarDialog progressBarDialog;
    int type = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressBarDialog = new ProgressBarDialog(requireActivity());
        return inflater.inflate(R.layout.fragment_create_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputLayoutClassName = view.findViewById(R.id.ed_layout_create_class_class_name);
        inputLayoutSubject = view.findViewById(R.id.ed_layout_create_class_subject);
        inputLayoutDescription = view.findViewById(R.id.ed_layout_create_class_description);

        edClassName = view.findViewById(R.id.ed_create_class_class_name);
        edSubject = view.findViewById(R.id.ed_create_class_subject);
        edDescription = view.findViewById(R.id.ed_create_class_description);

        btnCreateClass = view.findViewById(R.id.btn_create_class_create);
        btnBack = view.findViewById(R.id.img_create_class_back);

        type = getArguments().getInt("type");
        if(type == 1) {
            btnCreateClass.setText("Cập nhật");
            edClassName.setText(getArguments().getString("classroomName"));
            edDescription.setText(getArguments().getString("classroomDescription"));
            edSubject.setText(getArguments().getString("classroomSubject"));
        } else {
            btnCreateClass.setText("Tạo");
            edClassName.setText("");
            edDescription.setText("");
            edSubject.setText("");
        }

        edClassName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()>0 && edSubject.getText().toString().trim().length() > 0)
                    btnCreateClass.setEnabled(true);
                else
                    btnCreateClass.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length()>0 && edClassName.getText().toString().trim().length() > 0)
                    btnCreateClass.setEnabled(true);
                else
                    btnCreateClass.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnCreateClass.setOnClickListener(v -> {
            progressBarDialog.setMessage("Loading").show();
            SharedPreferences shared = requireActivity().getSharedPreferences("SESSION", MODE_PRIVATE);
            if(type == 0) {
                Classroom classroom = new Classroom();
                classroom.setName(edClassName.getText().toString().trim());
                classroom.setSubject(edSubject.getText().toString().trim());
                classroom.setDescription(edDescription.getText().toString().trim());
                classroom.setTeacherId(shared.getInt("session_id", 0));
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).createClassroom(classroom);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        progressBarDialog.dismiss();
                        if(response.code() == 201) {
                            Toast.makeText(requireContext(), "Tạo lớp học thành công!", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else if(response.code() == 400) {
                            Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Tạo lớp học thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBarDialog.dismiss();
                        Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            } else {
                Classroom classroom = new Classroom();
                classroom.setId(getArguments().getInt("classroomId"));
                classroom.setName(edClassName.getText().toString().trim());
                classroom.setSubject(edSubject.getText().toString().trim());
                classroom.setDescription(edDescription.getText().toString().trim());
                classroom.setTeacherId(shared.getInt("session_id", 0));
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).updateClassRoomById(classroom);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        progressBarDialog.dismiss();
                        if(response.code() == 204) {
                            Toast.makeText(requireContext(), "Cập nhật lớp học thành công!", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        } else if(response.code() == 400) {
                            Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                        } else if(response.code() == 404){
                            Toast.makeText(requireContext(), "Lớp không tồn tại!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Cập nhật lớp học thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBarDialog.dismiss();
                        Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }
}