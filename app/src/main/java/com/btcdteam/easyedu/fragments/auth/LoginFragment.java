package com.btcdteam.easyedu.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.activity.ParentActivity;
import com.btcdteam.easyedu.activity.TeacherActivity;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Teacher;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.ProgressBarDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private TextInputEditText edLoginPhoneNumber, edLoginPassword;
    private Button btnLogin;
    private String role;
    private ProgressBarDialog progressBarDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressBarDialog = new ProgressBarDialog(requireActivity());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        role = getArguments().getString("role");
        edLoginPhoneNumber = view.findViewById(R.id.ed_login_phone_number);
        edLoginPassword = view.findViewById(R.id.ed_login_password);
        btnLogin = view.findViewById(R.id.btn_login_login);

        btnLogin.setOnClickListener(v -> {
            String phone = edLoginPhoneNumber.getText().toString();
            String password = edLoginPassword.getText().toString();

            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(requireActivity(), "Không được để trống các trường", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBarDialog.setMessage("Loading").show();
            JsonObject object = new JsonObject();
            object.addProperty("phone", phone);
            object.addProperty("password", password);

            if (role.equals("teacher")) {
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).teacherLogin(object);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        progressBarDialog.dismiss();
                        if (response.code() == 200) {
                            Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Type userListType = new TypeToken<Teacher>() {
                            }.getType();
                            Teacher teacher = new Gson().fromJson(response.body().getAsJsonObject("data"), userListType);
                            sharedPreferencesTeacher(role, teacher.getId(), teacher.getName(), teacher.getEmail(), teacher.getPhone(), teacher.getDob());
                            requireActivity().startActivity(new Intent(requireActivity(), TeacherActivity.class));
                            requireActivity().finish();
                        } else if (response.code() == 401) {
                            Toast.makeText(getContext(), "Sai thông tin tài khoản", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBarDialog.dismiss();
                        t.printStackTrace();
                        Toast.makeText(requireContext(), "Không thế kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).parentLogin(object);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        progressBarDialog.dismiss();
                        if (response.code() == 200) {
                            Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Type userListType = new TypeToken<Parent>() {
                            }.getType();
                            Parent parent = new Gson().fromJson(response.body().getAsJsonObject("data"), userListType);
                            sharedPreferencesParent(role, parent.getId(), parent.getName(), parent.getEmail(), parent.getPhone(), parent.getDob(), parent.getFcmToken());
                            updateFcmToken(parent);
                        } else if (response.code() == 404) {
                            Toast.makeText(getContext(), "Sai thông tin tài khoản", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(getContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Đăng nhập thấy bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progressBarDialog.dismiss();
                        t.printStackTrace();
                        Toast.makeText(requireContext(), "Không thế kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void updateFcmToken(Parent parent) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            Toast.makeText(requireActivity(), "Đã xảy ra lỗi thông báo, vui lòng cài đặt lại ứng dụng!", Toast.LENGTH_SHORT).show();
                        } else {
                            String token = task.getResult();
                            if (parent.getFcmToken() == null || parent.getFcmToken().isEmpty() || !parent.getFcmToken().equalsIgnoreCase(token)) {
                                parent.setFcmToken(token);
                                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).updateParent(parent);
                                call.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.code() != HttpsURLConnection.HTTP_NO_CONTENT) {
                                            Toast.makeText(requireActivity(), "Xảy ra lỗi khi cập nhật token, bạn sẽ không nhận được thông báo nổi từ giáo viên. Hãy kiểm tra thủ công!", Toast.LENGTH_LONG).show();
                                        }
                                        enterParentActivity();
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        t.printStackTrace();
                                        Toast.makeText(requireActivity(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                enterParentActivity();
                            }
                        }
                    }
                });
    }

    private void enterParentActivity() {
        requireActivity().startActivity(new Intent(requireActivity(), ParentActivity.class));
        requireActivity().finish();
    }

    private void sharedPreferencesTeacher(String role, int id, String name, String email, String phone, String dob) {
        SharedPreferences.Editor edt = requireActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE).edit();
        edt.clear();
        edt.putString("session_role", role);
        edt.putInt("session_id", id);
        edt.putString("session_name", name);
        edt.putString("session_email", email);
        edt.putString("session_phone", phone);
        edt.putString("session_dob", dob);
        edt.apply();
    }

    private void sharedPreferencesParent(String role, String id, String name, String email, String phone, String dob, String fcm_token) {
        SharedPreferences.Editor edt = requireActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE).edit();
        edt.clear();
        edt.putString("session_role", role);
        edt.putString("session_id", id);
        edt.putString("session_name", name);
        edt.putString("session_email", email);
        edt.putString("session_phone", phone);
        edt.putString("session_dob", dob);
        edt.putString("session_fcmToken", fcm_token);
        edt.apply();
    }

}