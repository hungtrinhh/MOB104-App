package com.btcdteam.easyedu.fragments.parent;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.activity.AuthActivity;
import com.btcdteam.easyedu.adapter.parent.PreviewStudentAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Student;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.PreviewScore;
import com.btcdteam.easyedu.utils.SnackbarUntil;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewStudentFragment extends Fragment implements PreviewStudentAdapter.StudentItemListener {
    private ImageView btnInfo, btnNoti;
    private RecyclerView rcv;
    private TextView tvStudentName;
    private LinearProgressIndicator lpiClass;
    private PreviewStudentAdapter adapter;
    private List<Student> listStudent;
    private List<PreviewScore> classItems;
    private SharedPreferences shared;
    private String parentId = null;
    private String parentPhoneNumber = null;
    int selectMenuIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        shared = requireActivity().getSharedPreferences("SESSION", MODE_PRIVATE);
        parentId = shared.getString("session_id", "None");
        parentPhoneNumber = shared.getString("session_phone", "None");
        return inflater.inflate(R.layout.fragment_view_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lpiClass = view.findViewById(R.id.lpi_parent_student);
        btnInfo = view.findViewById(R.id.img_item_parent_student_info);
        btnNoti = view.findViewById(R.id.img_item_parent_student_noti);
        tvStudentName = view.findViewById(R.id.tv_parent_home_student_name);
        rcv = view.findViewById(R.id.rcv_item_parent_student);

        btnNoti.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("student_id", listStudent.get(selectMenuIndex).getId());
            Navigation.findNavController(requireActivity(), R.id.nav_host_parent).navigate(R.id.action_viewStudentFragment_to_notificationFragment, bundle);
        });

        btnInfo.setOnClickListener(this::showPopupMenu);
        getListStudent(parentId);
    }

    private void getListStudent(String parentId) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getListStudentByParent(parentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    Type type = new TypeToken<List<Student>>() {
                    }.getType();
                    listStudent = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    handleUpdateClass(listStudent.get(selectMenuIndex).getId());
                } else {
                    Toast.makeText(requireContext(), "Không có học sinh nào. Liên hệ giáo viên của lớp để biết thêm thông tin.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUpdateClass(String studentId) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getScoreWithClass(studentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    List<PreviewScore> semester1, semester2;
                    Type type = new TypeToken<List<PreviewScore>>() {
                    }.getType();
                    classItems = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    semester1 = classItems.stream().filter(item -> item.semester == 1).collect(Collectors.toList());
                    semester2 = classItems.stream().filter(item -> item.semester == 2).collect(Collectors.toList());
                    loadData(semester1, semester2);
                } else {
                    SnackbarUntil.showWarning(requireView(), "Học sinh hiện không tham gia lớp nào!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData(List<PreviewScore> semester1, List<PreviewScore> semester2) {
        tvStudentName.setText(listStudent.get(selectMenuIndex).getName());
        adapter = new PreviewStudentAdapter(semester1, semester2, this);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setAdapter(adapter);
        lpiClass.hide();
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Đổi học sinh");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Thông tin cá nhân");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Đổi mật khẩu");
        popupMenu.getMenu().add(Menu.NONE, 4, 4, "Đăng xuất");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle bundle;
                switch (item.getItemId()) {
                    case 1:
                        BottomMenu.show(listStudent.stream().map(Student::getName).collect(Collectors.toList())).setTitle("Danh sách học sinh").setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                            @Override
                            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                                selectMenuIndex = index;
                                handleUpdateClass(listStudent.get(index).getId());
                                return false;
                            }
                        }).setSelection(selectMenuIndex);
                        return true;
                    case 2:
                        Bundle bundle02 = new Bundle();
                        bundle02.putString("parentPhoneNumber",parentPhoneNumber);
                        bundle02.putString("parentId",parentId);
                        Navigation.findNavController(requireActivity(), R.id.nav_host_parent).navigate(R.id.action_viewStudentFragment_to_parentInfoFragment,bundle02);
                        return true;
                    case 3:
                        Bundle bundle01 = new Bundle();
                        bundle01.putString("parentId",parentId);
                        Navigation.findNavController(requireActivity(), R.id.nav_host_parent).navigate(R.id.action_viewStudentFragment_to_parentChangePassword,bundle01);
                        return true;
                    case 4:
                        MessageDialog.show("Đăng xuất", "Bạn có muốn đăng xuất không ?", "Có", "Không").setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
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

    @Override
    public void onItemClick(String classId) {
        // navigation
        Bundle bundle = new Bundle();
        bundle.putString("classroom_id", classId);
        bundle.putString("student_id", listStudent.get(selectMenuIndex).getId());
        Navigation.findNavController(requireActivity(), R.id.nav_host_parent).navigate(R.id.action_viewStudentFragment_to_studentDetailFragment, bundle);
    }
}