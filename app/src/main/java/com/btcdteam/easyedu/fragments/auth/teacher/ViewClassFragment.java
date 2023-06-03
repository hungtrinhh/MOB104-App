package com.btcdteam.easyedu.fragments.auth.teacher;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.activity.AuthActivity;
import com.btcdteam.easyedu.adapter.teacher.ClassroomAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Classroom;
import com.btcdteam.easyedu.network.APIService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewClassFragment extends Fragment implements ClassroomAdapter.ClassRoomItemListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView btnInfo;
    private ExtendedFloatingActionButton fabAddClass;
    private RecyclerView rcv;
    private List<Classroom> list;
    private ClassroomAdapter adapter;
    private SharedPreferences shared;
    private Bundle bundle;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnInfo = view.findViewById(R.id.img_item_class_info);
        rcv = view.findViewById(R.id.rcv_item_class);
        swipeRefreshLayout = view.findViewById(R.id.srl_class);
        fabAddClass = view.findViewById(R.id.fab_add_class);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_primary);
        swipeRefreshLayout.setProgressViewOffset(false, 100, 400);

        fabAddClass.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("type", 0);
            Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_viewClassFragment_to_createClassFragment, bundle);
        });

        btnInfo.setOnClickListener(v -> {
            showPopupMenu(btnInfo);
        });

        list = new ArrayList<>();
        getList();

        rcv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //scrollUp
                if (scrollY > oldScrollY + 20 && fabAddClass.isExtended()) {
                    fabAddClass.shrink();
                }

                //scrollDown
                if (scrollY < oldScrollY - 20 && !fabAddClass.isExtended()) {
                    fabAddClass.extend();
                }
            }
        });

    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.menu_teacher_option);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_account_info:
                        shared = requireActivity().getSharedPreferences("SESSION", MODE_PRIVATE);
                        bundle = new Bundle();
                        bundle.putInt("teacherId", shared.getInt("session_id", 0));
                        Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_viewClassFragment_to_accountInfoFragment, bundle);
                        return true;
                    case R.id.menu_change_password:
                        shared = requireActivity().getSharedPreferences("SESSION", MODE_PRIVATE);
                        bundle = new Bundle();
                        bundle.putInt("teacherId", shared.getInt("session_id", 0));
                        Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_viewClassFragment_to_changePasswordFragment, bundle);
                        return true;
                    case R.id.menu_logout:
                        MessageDialog.show("Đăng Xuất","Bạn có muốn đăng xuất không ?","Có","Không").setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {
                                SharedPreferences.Editor editor = requireActivity().getSharedPreferences("SESSION", MODE_PRIVATE).edit();
                                editor.clear().apply();
                                startActivity(new Intent(requireActivity(), AuthActivity.class));
                                requireActivity().finish();
                                return false;
                            }
                        }).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void getList() {
        if(!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);
        SharedPreferences shared = requireActivity().getSharedPreferences("SESSION", MODE_PRIVATE);
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getListClassroom(shared.getInt("session_id", 0));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                if (response.code() == 200) {
                    Type type = new TypeToken<List<Classroom>>() {
                    }.getType();
                    list = new Gson().fromJson(response.body().getAsJsonArray("data").toString(), type);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    adapter = new ClassroomAdapter(list, ViewClassFragment.this);
                    rcv.setLayoutManager(manager);
                    rcv.setAdapter(adapter);
                } else {
                    if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Không có lớp nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Không thể kết nối với máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveClassroomId(int classroomId, String classroomName) {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("CLASSROOM_ID", MODE_PRIVATE).edit();
        editor.clear();
        editor.putInt("classroomId", classroomId);
        editor.putString("classroomName", classroomName);
        editor.apply();
    }

    @Override
    public void onItemLongClick(int position, Classroom classroom, View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.menu_class);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_update_class:
                        updateClassRoom(classroom);
                        return true;
                    case R.id.menu_delete_class:
                        deleteClassRoom(classroom.getId(), position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onItemClick(int position, Classroom classroom) {
        saveClassroomId(classroom.getId(), classroom.getName());
        Bundle bundle = new Bundle();
        bundle.putInt("classroom_id", classroom.getId());
        bundle.putString("classroom_name", classroom.getName());
        bundle.putString("classroom_description", classroom.getDescription());
        bundle.putString("classroom_subject", classroom.getSubject());
        Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_viewClassFragment_to_classInfoFragment, bundle);
    }

    private void deleteClassRoom(int id, int position) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).deleteClassRoomById(id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 204) {
                    Toast.makeText(requireContext(), "Xóa lớp thành công", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    adapter.setList(list);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy thông tin lớp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateClassRoom(Classroom classroom) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putInt("classroomId", classroom.getId());
        bundle.putString("classroomName", classroom.getName());
        bundle.putString("classroomDescription", classroom.getDescription());
        bundle.putString("classroomSubject", classroom.getSubject());
        Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_viewClassFragment_to_createClassFragment, bundle);
    }

    @Override
    public void onRefresh() {
        getList();
    }
}