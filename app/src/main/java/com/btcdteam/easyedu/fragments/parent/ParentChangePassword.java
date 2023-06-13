package com.btcdteam.easyedu.fragments.parent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.network.APIService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentChangePassword extends Fragment {
    private EditText edOldPass, edNewPass, edRenewPass;
    private Button btnSave;
    private ImageView btnBack;
    private String oldPass, newPass, rePass, id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        id = getArguments().getString("parentId");
        return inflater.inflate(R.layout.fragment_account_change_password,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edOldPass = view.findViewById(R.id.ed_acc_info_old_pass);
        edNewPass = view.findViewById(R.id.ed_acc_info_new_pass);
        edRenewPass = view.findViewById(R.id.ed_acc_info_re_new_pass);
        btnSave = view.findViewById(R.id.btn_acc_info_save);
        btnBack = view.findViewById(R.id.img_acc_info_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnSave.setEnabled(true);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPass = edOldPass.getText().toString();
                newPass = edNewPass.getText().toString();
                rePass = edRenewPass.getText().toString();

                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(rePass)) {
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPass.equals(rePass)) {
                    Toast.makeText(requireContext(), "mật khẩu nhập lại không chính xác", Toast.LENGTH_SHORT).show();
                    return;
                }
                chanePasswordParent(id);
            }
        });
    }

    private void chanePasswordParent(String id) {
        JsonObject object = new JsonObject();
        object.addProperty("old_password", edOldPass.getText().toString());
        object.addProperty("new_password", edNewPass.getText().toString());
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).changePasswordParent("cbba1905-3957-4c7d-bd8b-23d3680424c1",object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 401) {
                    Toast.makeText(requireContext(), "mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }
                if (response.code() == 204) {
                    Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}