package com.btcdteam.easyedu.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.btcdteam.easyedu.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        btnRegisterBack.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_registerFragment_to_chooseActionFragment);
        });
    }
}