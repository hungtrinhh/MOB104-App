package com.btcdteam.easyedu.fragments.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.btcdteam.easyedu.R;
import com.google.android.material.button.MaterialButton;


public class ChooseRoleFragment extends Fragment {
    private Button btnChooseRoleTeacher, btnChooseRoleParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_role, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnChooseRoleTeacher = view.findViewById(R.id.btn_choose_role_teacher);
        btnChooseRoleParent = view.findViewById(R.id.btn_choose_role_parent);
        btnChooseRoleTeacher.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("role", "teacher");
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_chooseRoleFragment_to_chooseActionFragment, bundle);
        });
        btnChooseRoleParent.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("role", "parent");
            Navigation.findNavController(requireActivity(), R.id.nav_host_auth).navigate(R.id.action_chooseRoleFragment_to_chooseActionFragment, bundle);
        });

    }
}