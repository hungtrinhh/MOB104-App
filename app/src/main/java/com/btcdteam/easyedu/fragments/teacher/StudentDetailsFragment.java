package com.btcdteam.easyedu.fragments.auth.teacher;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.GoogleAPI;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Feedback;
import com.btcdteam.easyedu.models.StudentDetail;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.FCMBodyRequest;
import com.btcdteam.easyedu.utils.ProgressBarDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentDetailsFragment extends Fragment {
    private ImageView btnBack, btnSendFeedback;
    private ProgressBarDialog progressBarDialog;

    private TextView tvStudentName,
            tvStudentDateOfBirth,
            tvStudentGender,
            tvRegularScore1,
            tvRegularScore2,
            tvRegularScore3,
            tvMidtermScore,
            tvFinalScore,
            tvTotalScore,
            tvParentName,
            tvParentDateOfBirth,
            tvEmail,
            tvTotal,
            tvAvg,
            tvPhoneNumber;

    private EditText edSendFeedBack;
    private Switch swTerm;
    private ProgressBar pbTextFieldLoader;
    private Button btnDeleteStudent;
    private List<StudentDetail> studentDetails;
    private StudentDetail studentDetails1, studentDetails2;
    private String studentId, studentName;
    private int classId;
    private int teacherId;
    private String parentFcmToken = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressBarDialog = new ProgressBarDialog(requireActivity());
        teacherId = requireActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE).getInt("session_id", 0);
        return inflater.inflate(R.layout.fragment_student_details, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = view.findViewById(R.id.img_class_detail_back);
        studentDetails = new ArrayList<>();
        studentId = getArguments().getString("studentId");
        classId = getArguments().getInt("classRoomId");
        studentName = getArguments().getString("studentName");

        //student
        tvStudentName = view.findViewById(R.id.tv_student_detail_name);
        tvStudentDateOfBirth = view.findViewById(R.id.tv_student_detail_dob);
        tvStudentGender = view.findViewById(R.id.tv_student_detail_gender);
        tvRegularScore1 = view.findViewById(R.id.tv_student_detail_regular_1);
        tvRegularScore2 = view.findViewById(R.id.tv_student_detail_regular_2);
        tvRegularScore3 = view.findViewById(R.id.tv_student_detail_regular_3);
        tvMidtermScore = view.findViewById(R.id.tv_student_detail_midterm);
        tvFinalScore = view.findViewById(R.id.tv_student_detail_final);
        tvTotalScore = view.findViewById(R.id.tv_student_detail_total);
        tvAvg = view.findViewById(R.id.tv_student_detail_avg);
        swTerm = view.findViewById(R.id.sw_student_detail_term);
        btnDeleteStudent = view.findViewById(R.id.btn_class_detail_delete);
        tvTotal = view.findViewById(R.id.Tvtotal);

        // parent
        btnSendFeedback = view.findViewById(R.id.btn_send_feedback);
        edSendFeedBack = view.findViewById(R.id.ed_feedback);
        tvParentName = view.findViewById(R.id.tv_parent_detail_name);
        tvParentDateOfBirth = view.findViewById(R.id.tv_parent_detail_dob);
        tvEmail = view.findViewById(R.id.tv_parent_detail_email);
        tvPhoneNumber = view.findViewById(R.id.tv_parent_detail_phone_number);
        pbTextFieldLoader = view.findViewById(R.id.pb_text_field_loader);
        progressBarDialog.setMessage("Loading").show();
        tvPhoneNumber.setOnClickListener(v -> {
            dialPhoneNumber(tvPhoneNumber.getText().toString().trim());
        });
        getInfoTeacherAndParent();
        // set ban phim khong che Edittext
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        btnDeleteStudent.setOnClickListener(v -> {
            new MessageDialog("Xóa học sinh", "Bạn có muốn xóa học sinh: " + studentName + " không ?", "Có", "Không")
                    .setButtonOrientation(LinearLayout.HORIZONTAL)
                    .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            deleteStudent(studentId, classId);
                            return false;
                        }
                    }).show();
        });


        swTerm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setValues(studentDetails2);
            } else {
                setValues(studentDetails1);
            }
        });

        btnSendFeedback.setOnClickListener(v -> {
            if (teacherId == 0) {
                Toast.makeText(requireContext(), "Không nhận diện được giáo viên, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (edSendFeedBack.getText().toString().trim().isEmpty()) {
                return;
            }
            if (parentFcmToken == null || parentFcmToken.equalsIgnoreCase("null")) {
                new MessageDialog("Thông báo", "Phụ huynh chưa cài đặt EasyEdu hoặc không dùng điện thoại thông minh. Bạn có thể gửi SMS hoặc lưu lại thông báo ? ", "Gửi SMS", "Huỷ bỏ", "Lưu")
                        .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {
                                sendTextMessage();
                                return false;
                            }
                        })
                        .setOtherButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                            @Override
                            public boolean onClick(MessageDialog dialog, View v) {
                                saveFeedback();
                                return false;
                            }
                        }).show();
            } else {
                pushNotificationToParent();
            }

        });
    }

    private void sendTextMessage() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + studentDetails.get(0).getParentPhone()));
        intent.putExtra("sms_body", edSendFeedBack.getText().toString().trim());
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Không có ứng dụng mở tin nhắn!", Toast.LENGTH_SHORT).show();
        }

    }

    private void pushNotificationToParent() {
        pbTextFieldLoader.setVisibility(View.VISIBLE);
        btnSendFeedback.setVisibility(View.INVISIBLE);
        FCMBodyRequest bodyRequest = new FCMBodyRequest();
        bodyRequest.setNotification("Bạn có thông báo từ giáo viên", edSendFeedBack.getText().toString().trim());
        ArrayList<String> fcmTokens = new ArrayList<>();
        fcmTokens.add(parentFcmToken);
        bodyRequest.registration_ids = fcmTokens;
        Call<JsonObject> call = GoogleAPI.getInstance().create(APIService.class).pushNotification(bodyRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body().get("success").getAsInt() == 0) {
                    Toast.makeText(requireContext(), "Gửi thông báo đến phụ huynh thất bại! Dữ liệu đã được lưu lại!", Toast.LENGTH_SHORT).show();
                }
                saveFeedback();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ gửi dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void saveFeedback() {
        pbTextFieldLoader.setVisibility(View.VISIBLE);
        btnSendFeedback.setVisibility(View.INVISIBLE);
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).sendFeedback(new Feedback(edSendFeedBack.getText().toString().trim(), classId, studentId));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpsURLConnection.HTTP_CREATED) {
                    pbTextFieldLoader.setVisibility(View.INVISIBLE);
                    btnSendFeedback.setVisibility(View.VISIBLE);
                    edSendFeedBack.setText("");
                    PopTip.show("Đã gửi thông báo!");
                } else {
                    PopTip.show("Đã xảy ra lỗi khi gủi thông báo!");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void deleteStudent(String studentId, int classId) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).deleteStudentById(studentId, classId);
        Log.e(">>>>>>>>>>>>>>>", "deleteStudent: " + studentId + " / " + classId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 204) {
                    Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy thông tin học sinh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getInfoTeacherAndParent() {
        String studentId = getArguments().getString("studentId");
        int classId = getArguments().getInt("classRoomId");
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getInfoParentAndStudent(studentId, classId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBarDialog.dismiss();
                if (response.code() == 200) {
                    parentFcmToken = String.valueOf(response.body().getAsJsonArray("data").get(0).getAsJsonObject().get("parent_fcmtoken")).replace("\"", "");
                    ;
                    Type type = new TypeToken<List<StudentDetail>>() {
                    }.getType();
                    studentDetails = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    for (StudentDetail o : studentDetails) {
                        if (o.getSemester() % 2 == 0) {
                            studentDetails2 = o;
                        } else {
                            studentDetails1 = o;
                            setValues(studentDetails1);
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Học sinh không tồn tại !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setValues(StudentDetail detail) {

        Float total = null;
        tvTotalScore.setVisibility(View.GONE);
        tvTotal.setVisibility(View.GONE);
        if (detail.getRegularScore1() != null && detail.getRegularScore2() != null && detail.getRegularScore3() != null && detail.getMidtermScore() != null && detail.getFinalScore() != null) {
            total = ((detail.getRegularScore1() + detail.getRegularScore2() + detail.getRegularScore3() + (2 * (detail.getMidtermScore())) + (3 * (detail.getFinalScore())))) / 8;
        }
        tvStudentName.setText(detail.getName());
        tvStudentDateOfBirth.setText(detail.getDob());
        tvStudentGender.setText(detail.getStudentGender());
        tvRegularScore1.setText(detail.getRegularScore1() == null ? "?" : String.format(Locale.US, "%.2f", detail.getRegularScore1()));
        tvRegularScore2.setText(detail.getRegularScore2() == null ? "?" : String.format(Locale.US, "%.2f", detail.getRegularScore2()));
        tvRegularScore3.setText(detail.getRegularScore3() == null ? "?" : String.format(Locale.US, "%.2f", detail.getRegularScore2()));
        tvMidtermScore.setText(detail.getMidtermScore() == null ? "?" : String.format(Locale.US, "%.2f", detail.getMidtermScore()));
        tvFinalScore.setText(detail.getFinalScore() == null ? "?" : String.format(Locale.US, "%.2f", detail.getFinalScore()));
        tvParentName.setText(detail.getParentName());
        tvParentDateOfBirth.setText(detail.getParentDob());
        tvEmail.setText(detail.getParentEmail());
        if (total == null) {
            tvAvg.setText("?");
        } else {
            tvAvg.setText(String.format(Locale.US, "%.2f", total));
        }
        tvPhoneNumber.setText(detail.getParentPhone());
    }

}