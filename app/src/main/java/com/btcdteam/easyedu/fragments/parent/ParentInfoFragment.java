package com.btcdteam.easyedu.fragments.parent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Parent;
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

public class ParentInfoFragment extends Fragment {
    private ImageView btnBack;
    private Button btnSave;
    private EditText edName, edPhoneNumber, edEmail;
    private String parentPhoneNumber,parentId;
    private Button connectWithGG,disConnectWithGG;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentPhoneNumber = getArguments().getString("parentPhoneNumber");
        parentId = getArguments().getString("parentId");
        getInfoParent();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        return inflater.inflate(R.layout.fragment_account_info,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edName = view.findViewById(R.id.ed_acc_info_name);
        edPhoneNumber = view.findViewById(R.id.ed_acc_info_phone_number);
        btnSave = view.findViewById(R.id.btn_acc_info_save);
        btnBack = view.findViewById(R.id.img_acc_info_back);
        edEmail = view.findViewById(R.id.ed_acc_info_email);
        connectWithGG = view.findViewById(R.id.btn_choose_action_login_google1);
        disConnectWithGG = view.findViewById(R.id.btn_choose_action_login_google2);
        edEmail.setEnabled(false);
        btnSave.setEnabled(true);
        connectWithGG = view.findViewById(R.id.btn_choose_action_login_google1);
        disConnectWithGG = view.findViewById(R.id.btn_choose_action_login_google2);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateParent(parentId,edEmail.getText().toString());
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

    private void getInfoParent()
    {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getParentByPhone(parentPhoneNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200)
                {
                    Type type = new TypeToken<Parent>() {
                    }.getType();
                    Parent parent = new Gson().fromJson(response.body().getAsJsonObject("data").toString(), type);
                    edName.setText(parent.getName());
                    edEmail.setText(parent.getEmail());
                    edPhoneNumber.setText(parent.getPhone());
                    if(parent.getEmail() == null){
                        connectWithGG.setVisibility(View.VISIBLE);
                    }else{
                        edEmail.setText(parent.getEmail());
                        disConnectWithGG.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(requireContext(), "Không tìm thấy phụ huynh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi không thể kết nối tới Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateParent(String id,String email)
    {
        JsonObject object = new JsonObject();
        object.addProperty("parent_id", id);
        object.addProperty("parent_name", edName.getText().toString());
        object.addProperty("parent_phone", edPhoneNumber.getText().toString());
        if(!email.trim().equals("")){
            object.addProperty("parent_email", edEmail.getText().toString());
        }else{
            object.addProperty("parent_email", "");
        }

        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).updateParent(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 404) {
                    Toast.makeText(requireContext(), "Phụ huynh không tồn tại", Toast.LENGTH_SHORT).show();
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
    private void unLinkGoogleAccount() {
        updateParent(parentId, "");
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
