package com.btcdteam.easyedu.fragments.parent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.PreviewScore;
import com.btcdteam.easyedu.utils.SnackbarUntil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentDetailFragment extends Fragment {
    private ImageView btnBack, btnNoti;
    private TextView tvStudentName,
            tvStudentDateOfBirth,
            tvStudentGender,
            tvRegularScore1,
            tvRegularScore2,
            tvRegularScore3,
            tvMidtermScore,
            tvFinalScore,
            tvTotalScore,
            tvAvg,
            tvClassName,
            tvSubject,
            tvTeacherName;
    private Switch swTerm;
    private List<PreviewScore> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = view.findViewById(R.id.img_parent_student_detail_back);
        btnNoti = view.findViewById(R.id.img_parent_student_detail_noti);
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
        tvClassName = view.findViewById(R.id.tv_student_detail_class);
        tvSubject = view.findViewById(R.id.tv_student_detail_subject);
        tvTeacherName = view.findViewById(R.id.tv_student_detail_teacher_name);

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        btnNoti.setOnClickListener(v -> {
            // chuyển đến màn hình thông báo
            Bundle bundle = new Bundle();
            bundle.putString("student_id", getArguments().getString("student_id"));
            Navigation.findNavController(requireActivity(), R.id.nav_host_parent).navigate(R.id.action_studentDetailFragment2_to_notificationFragment, bundle);
        });

        swTerm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                toggleSemester(list.get(1));
            } else {
                toggleSemester(list.get(0));
            }
        });
        initData();
    }

    private void initData() {
        if (getArguments() != null) {
            String studentId = getArguments().getString("student_id");
            String classId = getArguments().getString("classroom_id");
            Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getScoreByStudentAndClass(studentId, classId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.code() == 200) {
                        Type type = new TypeToken<List<PreviewScore>>() {
                        }.getType();
                        list = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                        toggleSemester(list.get(0));
                        showTotalScore(list.get(0), list.get(1));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    SnackbarUntil.showError(requireView(), getString(R.string.server_connect_error));
                }
            });
        } else {
            SnackbarUntil.showError(requireView(), "Something went wrong!");
        }
    }

    private void toggleSemester(PreviewScore score) {
        Float avg = null;
        if (score.regular_score_1 != null && score.regular_score_2 != null && score.regular_score_3 != null && score.midterm_score != null && score.final_score != null) {
            avg = calcAvg(score);
        }
        tvTeacherName.setText(score.teacher_name);
        tvSubject.setText(score.classroom_subject);
        tvStudentName.setText(score.student_name);
        tvStudentDateOfBirth.setText(score.student_dob);
        tvStudentGender.setText(score.student_gender);
        tvRegularScore1.setText(score.regular_score_1 == null ? "?" : String.format(Locale.US, "%.2f", score.regular_score_1));
        tvRegularScore2.setText(score.regular_score_2 == null ? "?" : String.format(Locale.US, "%.2f", score.regular_score_2));
        tvRegularScore3.setText(score.regular_score_3 == null ? "?" : String.format(Locale.US, "%.2f", score.regular_score_3));
        tvMidtermScore.setText(score.midterm_score == null ? "?" : String.format(Locale.US, "%.2f", score.midterm_score));
        tvFinalScore.setText(score.final_score == null ? "?" : String.format(Locale.US, "%.2f", score.final_score));
        tvAvg.setText(avg == null ? "?" : String.format(Locale.US, "%.2f", avg));
    }

    private Float calcAvg(PreviewScore score) {
        if (score.regular_score_1 != null && score.regular_score_2 != null && score.regular_score_3 != null && score.midterm_score != null && score.final_score != null) {
            return ((score.regular_score_1 + score.regular_score_2 + score.regular_score_3 + (2 * (score.midterm_score)) + (3 * (score.final_score)))) / 8;
        }
        return null;
    }

    private void showTotalScore(PreviewScore score1, PreviewScore score2) {
        if (calcAvg(score1) != null && calcAvg(score2) != null) {
            Float total = (calcAvg(score1) + 2 * calcAvg(score2)) / 3;
            tvTotalScore.setText(String.format(Locale.US, "%.2f", total));
        } else {
            tvTotalScore.setText("?");
        }
    }
}