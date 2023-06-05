package com.btcdteam.easyedu.fragments.teacher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.adapter.teacher.PreviewAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Student;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.FileUtils;
import com.btcdteam.easyedu.utils.ProgressBarDialog;
import com.btcdteam.easyedu.utils.StudentListReader;
import com.btcdteam.easyedu.utils.SyncBody;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFileXlsFragment extends Fragment {

    private FloatingActionButton btnAddFile;
    private RecyclerView rcv;
    private TextView tvPreviewStudentCount, tvPreviewParentCount;
    private ArrayList<Parent> parentList;
    private Button btnSave;
    private List<Student> studentList;
    private ImageView btnBack;
    private PreviewAdapter adapter;
    private ProgressBarDialog progressBarDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressBarDialog = new ProgressBarDialog(requireActivity());
        return inflater.inflate(R.layout.fragment_add_file_xls, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddFile = view.findViewById(R.id.fab_add_xls_file);
        tvPreviewStudentCount = view.findViewById(R.id.tv_preview_student_count);
        tvPreviewParentCount = view.findViewById(R.id.tv_preview_parent_count);
        btnSave = view.findViewById(R.id.btn_add_file_save);
        rcv = view.findViewById(R.id.rcv_add_file);
        btnBack = view.findViewById(R.id.img_add_file_back);
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        btnSave.setOnClickListener(v -> {
            if (parentList == null || studentList == null || parentList.size() == 0 || studentList.size() == 0) {
                Snackbar.make(view, "No data", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBarDialog.show();
            int classroomId = getArguments().getInt("classroom_id");
            SyncBody body = new SyncBody(classroomId, parentList, studentList);
            Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).importStudentData(body);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressBarDialog.dismiss();
                    if (response.code() == 201) {
                        Toast.makeText(requireContext(), "Nhập danh sách thành công!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressBarDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnAddFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            openLauncher.launch(Intent.createChooser(intent, "Choose File excel.."));
        });
    }

    ActivityResultLauncher<Intent> openLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    //no data present
                    return;
                }

                String filePath = new FileUtils(requireActivity()).getPath(result.getData().getData());
                Log.i("TAG", "Selected File Path: " + filePath);
                progressBarDialog.setMessage("Loading").show();
                new StudentListReader(filePath, new StudentListReader.HandleOnComplete() {
                    @Override
                    public void onComplete(List<Student> students, Map<String, Parent> parentMapResult) {
                        parentList = new ArrayList<>(parentMapResult.values());
                        studentList = students;
                        adapter = new PreviewAdapter(students, parentMapResult);
                        rcv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
                        rcv.setAdapter(adapter);
                        progressBarDialog.dismiss();
                        tvPreviewStudentCount.setText("Học sinh: " + students.size());
                        tvPreviewParentCount.setText("Phụ huynh: " + parentMapResult.size());
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    });
}