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
import android.widget.Toast;

import com.btcdteam.easyedu.R;


public class ChooseActionFragment extends Fragment {
    Button btnChooseActionRegister, btnChooseActionLogin, btnChooseActionLoginGoogle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnChooseActionLogin = view.findViewById(R.id.btn_choose_action_login);
        btnChooseActionRegister = view.findViewById(R.id.btn_choose_action_register);
        btnChooseActionLoginGoogle = view.findViewById(R.id.btn_choose_action_login_google);

        btnChooseActionLogin.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_chooseActionFragment_to_loginFragment);
        });

        btnChooseActionRegister.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_chooseActionFragment_to_registerFragment);
        });

        btnChooseActionLoginGoogle.setOnClickListener(v -> {

        });
    }
}