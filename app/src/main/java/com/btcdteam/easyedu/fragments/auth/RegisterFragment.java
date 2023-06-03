package com.btcdteam.easyedu.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Teacher;
import com.btcdteam.easyedu.network.APIService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    ImageButton btnRegisterBack;
    TextInputLayout inputLayoutFullName,
            inputLayoutPhoneNumber,
            inputLayoutPassword,
            inputLayoutRepassword;
    TextInputEditText edFullName,
            edPhoneNumber,
            edPassword,
            edRepassword;
    String fullName, phone, passWord, rePassWord;

    Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRegisterBack = view.findViewById(R.id.btn_register_back);
        inputLayoutFullName = view.findViewById(R.id.ed_layout_register_full_name);
        inputLayoutPhoneNumber = view.findViewById(R.id.ed_layout_register_phone_number);
        inputLayoutPassword = view.findViewById(R.id.ed_layout_register_password);
        inputLayoutRepassword = view.findViewById(R.id.ed_layout_register_repassword);
        edFullName = view.findViewById(R.id.ed_register_full_name);
        edPhoneNumber = view.findViewById(R.id.ed_register_phone_number);
        edPassword = view.findViewById(R.id.ed_register_password);
        edRepassword = view.findViewById(R.id.ed_register_repassword);
        btnSubmit = view.findViewById(R.id.btn_register_submit);

        btnSubmit.setOnClickListener(v -> {
            fullName = edFullName.getText().toString();
            phone = edPhoneNumber.getText().toString();
            passWord = edPassword.getText().toString();
            rePassWord = edRepassword.getText().toString();

            if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(passWord) || TextUtils.isEmpty(rePassWord)) {
                Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ các trường", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passWord.equals(rePassWord)) {
                Toast.makeText(getActivity(), "Mật khẩu nhập lại không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher teacher = new Teacher(passWord, fullName, phone);
            Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).teacherRegister(teacher);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.code() == 409) {
                        Toast.makeText(getContext(), "Số điện thoại đã được đăng ký", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.code() == 201) {
                        Bundle bundle = new Bundle();
                        bundle.putString("role","teacher");
                        Toast.makeText(getContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_registerFragment_to_loginFragment,bundle);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getContext(), "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                }
            });

        });

        btnRegisterBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
//            Bundle bundle = new Bundle();
//            bundle.putString("role","teacher");
//            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_registerFragment_to_chooseActionFragment,bundle);
        });
    }
}