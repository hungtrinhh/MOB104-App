package com.btcdteam.easyedu.fragments.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.adapter.teacher.ClassroomAdapter;
import com.btcdteam.easyedu.models.Classroom;

import java.util.ArrayList;
import java.util.List;

public class StudentsClassFragment extends Fragment implements ClassroomAdapter.ClassRoomItemListener {
    private RecyclerView rcv;
    private ImageView btnBack;
//    private StudentDetailAdapter adapter;
    private ClassroomAdapter adapter;
    private List<Classroom> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcv_parent_student_class);
        btnBack = view.findViewById(R.id.img_parent_student_class_back);

        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));
        list.add(new Classroom("Lop 1", "Noi dung ghi chu", "Ten mon hoc", 0));

        adapter = new ClassroomAdapter(list, this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        rcv.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void onItemLongClick(int position, Classroom classroom, View view) {

    }

    @Override
    public void onItemClick(int position, Classroom classroom) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_parent).navigate(R.id.action_viewStudentFragment_to_studentDetailFragment);
    }
}