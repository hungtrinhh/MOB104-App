package com.btcdteam.easyedu.fragments.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.btcdteam.easyedu.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;


public class LoginFragment extends Fragment {
    private static  final String TAG = "LoginFragment";
    TextInputLayout inputLayoutLoginPhoneNumber, getInputLayoutLoginPassword;
    TextInputEditText edLoginPhoneNumber, edLoginPassword;
    Button btnLogin, btnLoginGoogle;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputLayoutLoginPhoneNumber = view.findViewById(R.id.ed_layout_login_phone_number);
        getInputLayoutLoginPassword = view.findViewById(R.id.ed_layout_login_password);
        edLoginPhoneNumber = view.findViewById(R.id.ed_login_phone_number);
        edLoginPassword = view.findViewById(R.id.ed_login_password);
        btnLogin = view.findViewById(R.id.btn_login_login);
        btnLoginGoogle = view.findViewById(R.id.btn_login_login_google);

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
                if(account != null){
                    new MessageDialog("Thông tin", "Email: "+account.getEmail(),"Logout","Close").setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(requireContext(), "Logout Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return false;
                        }
                    }).show();
                }else{
                    signInWithGoogle();
                }
            }
        });
    }

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getData() == null){
                return;
            }
            Task<GoogleSignInAccount> task  = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            handleSignInResult(task);
        }
    });


    private void signInWithGoogle(){
        Intent sign = mGoogleSignInClient.getSignInIntent();
        loginLauncher.launch(sign);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
        }catch (ApiException e){
            Log.w(TAG,"Login fail code: "+e.getStatusCode());
        }
    }


}