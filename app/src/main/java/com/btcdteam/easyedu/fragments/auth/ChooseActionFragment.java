package com.btcdteam.easyedu.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.activity.ParentActivity;
import com.btcdteam.easyedu.activity.TeacherActivity;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Teacher;
import com.btcdteam.easyedu.network.APIService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChooseActionFragment extends Fragment {
    private static final String TAG = "ChooseActionFragment";
    private Button btnChooseActionRegister, btnChooseActionLogin, btnChooseActionLoginGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private String role;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        return inflater.inflate(R.layout.fragment_choose_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        role = getArguments().getString("role");
        btnChooseActionLogin = view.findViewById(R.id.btn_choose_action_login);
        btnChooseActionRegister = view.findViewById(R.id.btn_choose_action_register);
        btnChooseActionLoginGoogle = view.findViewById(R.id.btn_choose_action_login_google);
        if (role.equalsIgnoreCase("parent")) {
            btnChooseActionRegister.setVisibility(View.GONE);
        }
        btnChooseActionLogin.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("role", role);
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_chooseActionFragment_to_loginFragment, bundle);
        });

        btnChooseActionRegister.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_chooseActionFragment_to_registerFragment);
        });

        btnChooseActionLoginGoogle.setOnClickListener(v -> {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (acct != null) {
               mGoogleSignInClient.signOut();
            } else {
                signInWithGoogle();
            }
        });
    }

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() == null) {
                //no data present
                return;
            }
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            handleSignInResult(task);
        }
    });

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        loginLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (role.equalsIgnoreCase("teacher")) {
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).teacherLoginWithEmail(account.getEmail());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 200) {
                            Type userListType = new TypeToken<Teacher>() {
                            }.getType();
                            Teacher teacher = new Gson().fromJson(response.body().getAsJsonObject("data"), userListType);
                            sharedPreferencesTeacher(role, teacher.getId(), teacher.getName(), teacher.getEmail(), teacher.getPhone(), teacher.getDob());
                            Toast.makeText(getContext(), "Hi: " + teacher.getName(), Toast.LENGTH_SHORT).show();
                            requireActivity().startActivity(new Intent(requireActivity(), TeacherActivity.class));
                            requireActivity().finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(getContext(), "Email chưa được liên kết với tài khoản nào!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                        mGoogleSignInClient.signOut();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(requireContext(), "Không thế kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).parentLoginWithEmail(account.getEmail());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 200) {
                            Type userListType = new TypeToken<Parent>() {
                            }.getType();
                            Parent parent = new Gson().fromJson(response.body().getAsJsonObject("data"), userListType);
                            sharedPreferencesParent(role, parent.getId(), parent.getName(), parent.getEmail(), parent.getPhone(), parent.getDob(), parent.getFcmToken());
                            Toast.makeText(getContext(), "Hi: " + parent.getName(), Toast.LENGTH_SHORT).show();
                            requireActivity().startActivity(new Intent(requireActivity(), ParentActivity.class));
                            requireActivity().finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(getContext(), "Email chưa được liên kết với tài khoản nào!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Đăng nhập thấy bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(requireContext(), "Không thế kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (ApiException e) {
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
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