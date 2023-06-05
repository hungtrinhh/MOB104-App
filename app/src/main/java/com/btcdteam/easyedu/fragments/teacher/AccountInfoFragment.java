package com.btcdteam.easyedu.fragments.auth.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Teacher;
import com.btcdteam.easyedu.network.APIService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInfoFragment extends Fragment {
    private EditText edName, edPhoneNumber, edEmail;
    private Button btnSave;
    private ImageView btnBack;
    private int id;
    private Button connectWithGG,disConnectWithGG;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        return inflater.inflate(R.layout.fragment_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edName = view.findViewById(R.id.ed_acc_info_name);
        edPhoneNumber = view.findViewById(R.id.ed_acc_info_phone_number);
        btnSave = view.findViewById(R.id.btn_acc_info_save);
        btnBack = view.findViewById(R.id.img_acc_info_back);
        edEmail = view.findViewById(R.id.ed_acc_info_email);
        edEmail.setEnabled(false);
        connectWithGG = view.findViewById(R.id.btn_choose_action_login_google1);
        disConnectWithGG = view.findViewById(R.id.btn_choose_action_login_google2);

        id = getArguments().getInt("teacherId");
        getInfoTeacher(id);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnSave.setEnabled(true);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTeacher(id,edEmail.getText().toString());
            }
        });

        disConnectWithGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unLinkGoogleAccount();
            }
        });

        connectWithGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    private void updateTeacher(int id, String email) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", edName.getText().toString());
        object.addProperty("phone", edPhoneNumber.getText().toString());
        if(!email.trim().equals("")){
            object.addProperty("email", edEmail.getText().toString());
        }else{
            object.addProperty("email", "");
        }

        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).editTeacher(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 404) {
                    Toast.makeText(requireContext(), "Giáo viên không tồn tại", Toast.LENGTH_SHORT).show();
                }
                if (response.code() == 409) {
                    Toast.makeText(requireContext(), "Email hoặc số điện thoại đã tồn tại trên hệ thống!", Toast.LENGTH_SHORT).show();
                }
                if (response.code() == 204) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfoTeacher(int id) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getTeacherById(id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 404) {
                    Toast.makeText(requireContext(), "Giáo viên không tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    Type type = new TypeToken<Teacher>() {
                    }.getType();
                    Teacher teacher = new Gson().fromJson(response.body().getAsJsonObject("data").toString(), type);
                    edName.setText(teacher.getName());
                    edPhoneNumber.setText(teacher.getPhone());
                    if(teacher.getEmail() == null){
                        connectWithGG.setVisibility(View.VISIBLE);
                    }else{
                        edEmail.setText(teacher.getEmail());
                        disConnectWithGG.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unLinkGoogleAccount() {
        updateTeacher(id, "");
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
            edEmail.setText(account.getEmail());
            mGoogleSignInClient.signOut();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}