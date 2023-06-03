package com.btcdteam.easyedu.fragments.teacher;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Parent;
import com.btcdteam.easyedu.models.Student;
import com.btcdteam.easyedu.models.StudentDetail;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.ProgressBarDialog;
import com.btcdteam.easyedu.utils.Scores;
import com.btcdteam.easyedu.utils.UpdateStudentBody;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditStudentFragment extends Fragment implements TextWatcher {
    EditText edStudentName, edStudentDOB,
            edRegularScore1Term1, edRegularScore2Term1, edRegularScore3Term1, edMidtermScoreTerm1, edFinalScoreTerm1,
            edRegularScore1Term2, edRegularScore2Term2, edRegularScore3Term2, edMidtermScoreTerm2, edFinalScoreTerm2,
            edParentPhoneNumber, edParentName, edParentDOB, edParentEmail;
    Spinner spStudentGender;
    String[] items = new String[]{"Nam", "Nữ"};
    ImageView btnBack;
    Button btnSave, btnSearch;
    TextView tvStudentTitle;
    private String parent_id;
    private int checkParent = 0;
    private ProgressBarDialog progressBarDialog;
    private Calendar calendar;
    public Student student;
    public Parent parent;
    public Scores semester1;
    public Scores semester2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressBarDialog = new ProgressBarDialog(requireActivity());
        calendar = Calendar.getInstance();
        return inflater.inflate(R.layout.fragment_edit_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //điều hướng
        btnBack = view.findViewById(R.id.img_edit_student_back);
        btnSave = view.findViewById(R.id.btn_edit_student_save);
        btnSearch = view.findViewById(R.id.btn_parent_search_phone_number);
        // tên, ngày sinh
        edStudentName = view.findViewById(R.id.ed_student_name);
        edStudentDOB = view.findViewById(R.id.ed_student_dob);
        //điểm kỳ 1
        edRegularScore1Term1 = view.findViewById(R.id.ed_regular_1_term_1);
        edRegularScore2Term1 = view.findViewById(R.id.ed_regular_2_term_1);
        edRegularScore3Term1 = view.findViewById(R.id.ed_regular_3_term_1);
        edMidtermScoreTerm1 = view.findViewById(R.id.ed_midterm_term_1);
        edFinalScoreTerm1 = view.findViewById(R.id.ed_final_term_1);
        //điểm kỳ 2
        edRegularScore1Term2 = view.findViewById(R.id.ed_regular_1_term_2);
        edRegularScore2Term2 = view.findViewById(R.id.ed_regular_2_term_2);
        edRegularScore3Term2 = view.findViewById(R.id.ed_regular_3_term_2);
        edMidtermScoreTerm2 = view.findViewById(R.id.ed_midterm_term_2);
        edFinalScoreTerm2 = view.findViewById(R.id.ed_final_term_2);
        //giới tính
        spStudentGender = view.findViewById(R.id.sp_student_gender);
        //Phụ huynh
        edParentPhoneNumber = view.findViewById(R.id.ed_parent_phone_number);
        edParentName = view.findViewById(R.id.ed_parent_name);
        edParentDOB = view.findViewById(R.id.ed_parent_dob);
        edParentEmail = view.findViewById(R.id.ed_parent_email);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spStudentGender.setAdapter(adapter);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        // set ban phim khong che Edittext
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //check null
        edStudentName.addTextChangedListener(this);
        edStudentDOB.addTextChangedListener(this);
        tvStudentTitle = view.findViewById(R.id.tv_edit_student_title);
        int type = getArguments().getInt("type");
        if (type == 0) {
            tvStudentTitle.setText("Thêm học sinh");
        } else {
            tvStudentTitle.setText("Cập nhật học sinh");
            getStudent();
        }

        btnSave.setOnClickListener(v -> {
            if (type == 0) {
                if (edParentPhoneNumber.getText().toString().trim().length() != 10) {
                    Toast.makeText(requireContext(), "Số điện thoại không không hợp lệ hoặc chưa thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBarDialog.setMessage("Loading").show();
                if (checkParent == 1) {
                    createStudentWithParent(parent_id);
                } else {
                    createStudentWithoutParent();
                }
            } else {
                if (edParentPhoneNumber.getText().toString().trim().length() != 10) {
                    Toast.makeText(requireContext(), "Số điện thoại không không hợp lệ hoặc chưa thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBarDialog.setMessage("Loading").show();
                if (checkParent == 1) {
                    updateStudent(parent_id);
                } else {
                    updateStudentWithoutParent();
                }
            }
        });

        btnSearch.setOnClickListener(v -> {
            getParentByPhone(edParentPhoneNumber.getText().toString().trim());
        });

        edStudentDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        edStudentDOB.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        edParentDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DAY_OF_MONTH, i2);
                        edParentDOB.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (edStudentDOB.getText().toString().trim().length() > 0 && edStudentName.getText().toString().trim().length() > 0) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void getParentByPhone(String phone) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getParentByPhone(phone);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBarDialog.dismiss();
                if (response.code() == 200) {
                    checkParent = 1;
                    edParentName.setEnabled(false);
                    edParentDOB.setEnabled(false);
                    edParentEmail.setEnabled(false);
                    parent_id = !response.body().getAsJsonObject("data").get("parent_id").isJsonNull() ? response.body().getAsJsonObject("data").get("parent_id").getAsString() : null;
                    edParentName.setText(!response.body().getAsJsonObject("data").get("parent_name").isJsonNull() ? response.body().getAsJsonObject("data").get("parent_name").getAsString() : null);
                    edParentDOB.setText(!response.body().getAsJsonObject("data").get("parent_dob").isJsonNull() ? response.body().getAsJsonObject("data").get("parent_dob").getAsString() : null);
                    edParentEmail.setText(!response.body().getAsJsonObject("data").get("parent_email").isJsonNull() ? response.body().getAsJsonObject("data").get("parent_email").getAsString() : null);
                } else {
                    checkParent = 0;
                    edParentName.setEnabled(true);
                    edParentDOB.setEnabled(true);
                    edParentEmail.setEnabled(true);
                    Toast.makeText(requireContext(), "Không có phụ huynh nào, vui lòng tạo mới!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressBarDialog.dismiss();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createStudentWithParent(String parentId) {
        Student student = new Student();
        student.setName(edStudentName.getText().toString().trim());
        student.setDob(edStudentDOB.getText().toString().trim());
        student.setGender(spStudentGender.getSelectedItem().toString());
        student.setParentId(checkParent == 1 ? parent_id : parentId);
        JsonObject body = new JsonObject();
        body.addProperty("id", student.getId());
        body.addProperty("name", student.getName());
        body.addProperty("gender", student.getGender());
        body.addProperty("dob", student.getDob());
        body.addProperty("parent_id", student.getParentId());
        body.addProperty("classroom_id", getArguments().getInt("classroom_id"));
        JsonArray jsonElements = new JsonArray();
        JsonObject semester1 = new JsonObject();
        semester1.addProperty("regular_score_1", edRegularScore1Term1.getText().toString().trim().equals("") ? null : edRegularScore1Term1.getText().toString().trim());
        semester1.addProperty("regular_score_2", edRegularScore2Term1.getText().toString().trim().equals("") ? null : edRegularScore2Term1.getText().toString().trim());
        semester1.addProperty("regular_score_3", edRegularScore3Term1.getText().toString().trim().equals("") ? null : edRegularScore3Term1.getText().toString().trim());
        semester1.addProperty("midterm_score", edMidtermScoreTerm1.getText().toString().trim().equals("") ? null : edMidtermScoreTerm1.getText().toString().trim());
        semester1.addProperty("final_score", edFinalScoreTerm1.getText().toString().trim().equals("") ? null : edFinalScoreTerm1.getText().toString().trim());
        semester1.addProperty("semester", 1);
        JsonObject semester2 = new JsonObject();
        semester2.addProperty("regular_score_1", edRegularScore1Term2.getText().toString().trim().equals("") ? null : edRegularScore1Term2.getText().toString().trim());
        semester2.addProperty("regular_score_2", edRegularScore2Term2.getText().toString().trim().equals("") ? null : edRegularScore2Term2.getText().toString().trim());
        semester2.addProperty("regular_score_3", edRegularScore3Term2.getText().toString().trim().equals("") ? null : edRegularScore3Term2.getText().toString().trim());
        semester2.addProperty("midterm_score", edMidtermScoreTerm2.getText().toString().trim().equals("") ? null : edMidtermScoreTerm2.getText().toString().trim());
        semester2.addProperty("final_score", edFinalScoreTerm2.getText().toString().trim().equals("") ? null : edFinalScoreTerm2.getText().toString().trim());
        semester2.addProperty("semester", 2);
        jsonElements.add(semester1);
        jsonElements.add(semester2);
        body.add("scores", jsonElements);
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).createStudentHandMade(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBarDialog.dismiss();
                if (response.code() == 201) {
                    Toast.makeText(requireContext(), "Tạo học sinh thành công!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else if (response.code() == 400) {
                    Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Tạo lớp học thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBarDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createStudentWithoutParent() {
        Parent parent = new Parent(edParentName.getText().toString().trim(), edParentPhoneNumber.getText().toString().trim());
        parent.setDob(edParentDOB.getText().toString().trim());
        parent.setEmail(edParentEmail.getText().toString().trim());
        if (parent.getName().equals("") || parent.getPhone().equals("")) {
            Toast.makeText(requireContext(), "Chưa chọn phụ huynh!", Toast.LENGTH_SHORT).show();
        } else {
            Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).createParent(parent);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressBarDialog.dismiss();
                    if (response.code() == 201) {
                        createStudentWithParent(parent.getId());
                    } else if (response.code() == 409) {
                        Toast.makeText(requireContext(), "Số điện thoại đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 400) {
                        Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Tạo phụ huynh thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressBarDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getStudent() {
        int classroomId = getArguments().getInt("classroom_id");
        String studentId = getArguments().getString("student_id");
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getStudentById(studentId, classroomId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBarDialog.dismiss();
                if (response.code() == 200) {
                    JsonObject data = response.body().getAsJsonObject("data");
                    JsonObject studentBody = data.getAsJsonObject("student");
                    JsonObject parentBody = studentBody.getAsJsonObject("parentId");
                    student = new Student();
                    student.setId(studentBody.get("id").getAsString());
                    student.setName(studentBody.get("name").getAsString());
                    student.setDob(studentBody.get("dob").getAsString());
                    student.setGender(studentBody.get("gender").getAsString());
                    student.setParentId(parentBody.get("id").getAsString());
                    parent = new Parent();
                    parent.setId(parentBody.get("id").getAsString());
                    parent.setName(parentBody.get("name").getAsString());
                    parent.setEmail(!parentBody.get("email").isJsonNull() ? parentBody.get("email").getAsString() : null);
                    parent.setDob(!parentBody.get("dob").isJsonNull() ? parentBody.get("dob").getAsString() : null);
                    parent.setPhone(!parentBody.get("phone").isJsonNull() ? parentBody.get("phone").getAsString() : null);
                    JsonArray scores = data.getAsJsonArray("scores");
                    semester1 = new Gson().fromJson(scores.get(0), Scores.class);
                    semester2 = new Gson().fromJson(scores.get(1), Scores.class);
                    fillData();
                } else {
                    progressBarDialog.dismiss();
                    Toast.makeText(requireContext(), "Không tìm thấy học sinh!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBarDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fillData() {
        edStudentName.setText(student.getName());
        edStudentDOB.setText(student.getDob());
        spStudentGender.setSelection(student.getGender().equals("Nam") ? 0 : 1);
        edRegularScore1Term1.setText(semester1.regularScore1 == null ? "" : String.valueOf(semester1.regularScore1));
        edRegularScore2Term1.setText(semester1.regularScore2 == null ? "" : String.valueOf(semester1.regularScore2));
        edRegularScore3Term1.setText(semester1.regularScore3 == null ? "" : String.valueOf(semester1.regularScore3));
        edMidtermScoreTerm1.setText(semester1.midtermScore == null ? "" : String.valueOf(semester1.midtermScore));
        edFinalScoreTerm1.setText(semester1.finalScore == null ? "" : String.valueOf(semester1.finalScore));
        edRegularScore1Term2.setText(semester2.regularScore1 == null ? "" : String.valueOf(semester2.regularScore1));
        edRegularScore2Term2.setText(semester2.regularScore2 == null ? "" : String.valueOf(semester2.regularScore2));
        edRegularScore3Term2.setText(semester2.regularScore3 == null ? "" : String.valueOf(semester2.regularScore3));
        edMidtermScoreTerm2.setText(semester2.midtermScore == null ? "" : String.valueOf(semester2.midtermScore));
        edFinalScoreTerm2.setText(semester2.finalScore == null ? "" : String.valueOf(semester2.finalScore));
        edParentPhoneNumber.setText(parent.getPhone());
        edParentName.setText(parent.getName());
        edParentDOB.setText(parent.getDob());
        edParentEmail.setText(parent.getEmail());
        getParentByPhone(parent.getPhone());
    }

    public void updateStudent(String parentId) {
        UpdateStudentBody updateStudentBody = new UpdateStudentBody();
        updateStudentBody.student = new Student();
        updateStudentBody.student.setId(student.getId());
        updateStudentBody.student.setName(edStudentName.getText().toString().trim());
        updateStudentBody.student.setDob(edStudentDOB.getText().toString().trim());
        updateStudentBody.student.setGender(spStudentGender.getSelectedItem().toString());
        updateStudentBody.student.setParentId(checkParent == 1 ? parent_id : parentId);
        StudentDetail studentDetail1 = new StudentDetail();
        studentDetail1.setId(semester1.id);
        studentDetail1.setRegularScore1(edRegularScore1Term1.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edRegularScore1Term1.getText().toString().trim()));
        studentDetail1.setRegularScore2(edRegularScore2Term1.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edRegularScore2Term1.getText().toString().trim()));
        studentDetail1.setRegularScore3(edRegularScore3Term1.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edRegularScore3Term1.getText().toString().trim()));
        studentDetail1.setMidtermScore(edMidtermScoreTerm1.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edMidtermScoreTerm1.getText().toString().trim()));
        studentDetail1.setFinalScore(edFinalScoreTerm1.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edFinalScoreTerm1.getText().toString().trim()));
        studentDetail1.setSemester(semester1.semester);
        studentDetail1.setClassroomId(semester1.classroomId);
        studentDetail1.setStudentId(semester1.studentId);
        StudentDetail studentDetail2 = new StudentDetail();
        studentDetail2.setId(semester2.id);
        studentDetail2.setRegularScore1(edRegularScore1Term2.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edRegularScore1Term2.getText().toString().trim()));
        studentDetail2.setRegularScore2(edRegularScore2Term2.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edRegularScore2Term2.getText().toString().trim()));
        studentDetail2.setRegularScore3(edRegularScore3Term2.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edRegularScore3Term2.getText().toString().trim()));
        studentDetail2.setMidtermScore(edMidtermScoreTerm2.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edMidtermScoreTerm2.getText().toString().trim()));
        studentDetail2.setFinalScore(edFinalScoreTerm2.getText().toString().trim().isEmpty() ? null : Float.parseFloat(edFinalScoreTerm2.getText().toString().trim()));
        studentDetail2.setSemester(semester2.semester);
        studentDetail2.setClassroomId(semester2.classroomId);
        studentDetail2.setStudentId(semester2.studentId);
        updateStudentBody.scores = new ArrayList<>();
        updateStudentBody.scores.add(studentDetail1);
        updateStudentBody.scores.add(studentDetail2);
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).updateStudent(updateStudentBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBarDialog.dismiss();
                if (response.code() == 204) {
                    requireActivity().onBackPressed();
                    Toast.makeText(requireContext(), "Cập nhật dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(requireContext(), "Học sinh không tồn tại!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarDialog.dismiss();
                    Toast.makeText(requireContext(), "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBarDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStudentWithoutParent() {
        Parent newParent = new Parent(edParentName.getText().toString().trim(), edParentPhoneNumber.getText().toString().trim());
        newParent.setDob(edParentDOB.getText().toString().trim());
        newParent.setEmail(edParentEmail.getText().toString().trim());
        if (newParent.getName().equals("") || newParent.getPhone().equals("")) {
            Toast.makeText(requireContext(), "Chưa chọn phụ huynh!", Toast.LENGTH_SHORT).show();
        } else {
            Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).createParent(newParent);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressBarDialog.dismiss();
                    if (response.code() == 201) {
                        updateStudent(newParent.getId());
                    } else if (response.code() == 409) {
                        Toast.makeText(requireContext(), "Số điện thoại đã tồn tai!", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 400) {
                        Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Tạo phụ huynh thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressBarDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(requireContext(), "Không thể kết nối tới máy chủ!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}