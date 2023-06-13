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
import com.btcdteam.easyedu.adapter.teacher.ScorePreviewAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.StudentDetail;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.FileUtils;
import com.btcdteam.easyedu.utils.StudentScoreReader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ImportScoreFragment extends Fragment {
    private RecyclerView rcvAddFile;
    private ImageView imgAddFileBack;
    private TextView tvAddFileTitle;
    private Button btnAddFileSave;
    private FloatingActionButton fabAddXlsFile;
    private List<StudentDetail> list;
    private LinearProgressIndicator lpiImportScore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_import_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvAddFile = view.findViewById(R.id.rcv_add_file);
        imgAddFileBack = view.findViewById(R.id.img_add_file_back);
        tvAddFileTitle = view.findViewById(R.id.tv_add_file_title);
        btnAddFileSave = view.findViewById(R.id.btn_add_file_save);
        lpiImportScore = view.findViewById(R.id.lpi_import_score);
        fabAddXlsFile = view.findViewById(R.id.fab_add_xls_file);
        btnAddFileSave.setEnabled(false);
        lpiImportScore.hide();
        imgAddFileBack.setOnClickListener(v -> requireActivity().onBackPressed());
        fabAddXlsFile.setOnClickListener(v -> {
            new MessageDialog("Thông báo", "Chúng tôi chỉ xử lý file excel có định dạng khi xuất ra từ EasyEdu. Khi cập nhật điểm vui lòng không sửa các thông tin không liên quan để dữ liệu được cập nhật một cách chính xác nhất", "Tiếp tục", "Huỷ")
                    .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                            importLauncher.launch(Intent.createChooser(intent, "Choose File excel.."));
                            lpiImportScore.show();
                            return false;
                        }
                    }).show();

        });
        btnAddFileSave.setOnClickListener(v -> {
            try {
                updateToServer();
            } catch (JSONException e) {
                Toast.makeText(requireContext(), "Danh sách không hợp lệ!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void updateToServer() throws JSONException {
        lpiImportScore.show();
        JSONObject body = new JSONObject();
        JSONArray scores = new JSONArray();
        if (list == null || list.size() == 0 || list.size() % 2 != 0) {
            Toast.makeText(requireContext(), "Dữ liệu không hợp lệ, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
            lpiImportScore.hide();
            return;
        }
        for (StudentDetail detail : list) {
            JSONObject scoreData = new JSONObject();
            scoreData.put("id", detail.getId());
            scoreData.put("classroom_id", detail.getClassroomId());
            scoreData.put("student_id", detail.getStudentId());
            scoreData.put("regular_score_1", detail.getRegularScore1() == null ? JSONObject.NULL : detail.getRegularScore1());
            scoreData.put("regular_score_2", detail.getRegularScore2() == null ? JSONObject.NULL : detail.getRegularScore2());
            scoreData.put("regular_score_3", detail.getRegularScore3() == null ? JSONObject.NULL : detail.getRegularScore3());
            scoreData.put("midterm_score", detail.getMidtermScore() == null ? JSONObject.NULL : detail.getMidtermScore());
            scoreData.put("final_score", detail.getFinalScore() == null ? JSONObject.NULL : detail.getFinalScore());
            scoreData.put("semester", detail.getSemester());
            scores.put(scoreData);
        }
        body.put("students_scores", scores);
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).importStudentScore(RequestBody.create(MediaType.parse("application/json"), body.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                lpiImportScore.hide();
                if (response.code() == 200) {
                    tvAddFileTitle.setText("Đã cập nhật bảng điểm");
                    btnAddFileSave.setEnabled(false);
                    Snackbar.make(requireView(), "Cập nhật thành công !", Toast.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.blue_primary, requireActivity().getTheme())).show();
                } else {
                    Snackbar.make(requireView(), "Đã có lỗi khi cập nhật, vui lòng thử lại sau!", Toast.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.caution, requireActivity().getTheme())).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                lpiImportScore.hide();
                t.printStackTrace();
                Toast.makeText(requireActivity(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> importLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    //no data present
                    return;
                }

                String filePath = new FileUtils(requireActivity()).getPath(result.getData().getData());
                Log.i("TAG", "Selected File Path: " + filePath);
                new StudentScoreReader(filePath, new StudentScoreReader.HandleOnComplete() {
                    @Override
                    public void onComplete(List<StudentDetail> studentDetails) {
                        lpiImportScore.hide();
                        if (getArguments() != null) {
                            if (studentDetails.get(0).getClassroomId() != getArguments().getInt("classroom_id")) {
                                Snackbar.make(requireView(), "Bảng điểm bạn chọn không thuộc lớp này!", Toast.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.caution, requireActivity().getTheme())).show();
                                return;
                            }
                            ScorePreviewAdapter studentDetailAdapter = new ScorePreviewAdapter(requireActivity(), studentDetails);
                            rcvAddFile.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
                            rcvAddFile.setAdapter(studentDetailAdapter);
                            list = studentDetails;
                            tvAddFileTitle.setText("Kiểm tra thông tin");
                            btnAddFileSave.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        lpiImportScore.hide();
                        Snackbar.make(requireView(), "File không hợp lệ, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.caution, requireActivity().getTheme())).show();
                    }
                }).start();
            }
        }
    });
}