package com.btcdteam.easyedu.fragments.teacher;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.adapter.teacher.FeedbackAdapter;
import com.btcdteam.easyedu.apis.GoogleAPI;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.Feedback;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.FCMBodyRequest;
import com.btcdteam.easyedu.utils.ParentPreview;
import com.btcdteam.easyedu.utils.SnackbarUntil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends Fragment implements FeedbackAdapter.FeedbackCallback {
    RecyclerView rcv;
    ImageView btnBack, btnSendFeedback, btnFeedbackOption;
    FeedbackAdapter adapter;
    EditText edFeedback;
    TextView tvTitle;
    List<ParentPreview> list;
    private int teacherId;
    ArrayList<String> fcmTokens;
    private ProgressBar pbTextFieldLoader;
    ActionMode actionMode;
    int totalStudent = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        teacherId = requireActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE).getInt("session_id", 0);
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcv_feedback);
        fcmTokens = new ArrayList<>();
        btnBack = view.findViewById(R.id.img_feedback_back);
        btnSendFeedback = view.findViewById(R.id.btn_send_feedback);
        btnFeedbackOption = view.findViewById(R.id.img_feedback_option);
        edFeedback = view.findViewById(R.id.ed_feedback);
        tvTitle = view.findViewById(R.id.tv_feedback_title);
        pbTextFieldLoader = view.findViewById(R.id.pb_text_field_loader);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
        btnFeedbackOption.setOnClickListener(this::showPopupMenu);
        btnSendFeedback.setOnClickListener(v -> {
            if (edFeedback.getText().toString().trim().isEmpty()) {
                return;
            }
            fcmTokens = (ArrayList<String>) adapter.getSelectedToken().values().stream().map(ParentPreview::getFcmToken).collect(Collectors.toList());
            fcmTokens = (ArrayList<String>) fcmTokens.stream().filter(Objects::nonNull).collect(Collectors.toList());
            list = new ArrayList<>(adapter.getSelectedToken().values());
            if (list.size() == 0) {
                Toast.makeText(requireContext(), "Chưa chọn học sinh nào!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (teacherId == 0) {
                Toast.makeText(requireContext(), "Không nhận diện được giáo viên, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                return;
            }
            pbTextFieldLoader.setVisibility(View.VISIBLE);
            btnSendFeedback.setVisibility(View.INVISIBLE);
            pushNotificationToParent();
        });
        getListParent(getArguments().getInt("classroom_id"));
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Lựa chọn nhiều PH");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Lọc PH đã cài đặt ứng dụng");
        popupMenu.getMenu().add(Menu.NONE, 3, 3, "Lọc PH chưa cài đặt ứng dụng");
        popupMenu.getMenu().add(Menu.NONE, 4, 4, "Toàn bộ danh sách");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        adapter.toggleShowCheckbox();
                        actionMode = requireActivity().startActionMode(new android.view.ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                                menu.add(Menu.NONE, 1, 1, "Bỏ chọn tất cả");
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                                mode.setTitle(totalStudent + " / " + totalStudent);
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                                if (item.getItemId() == 1) {
                                    if (adapter.getSelectedToken().size() > 0) {
                                        item.setTitle("Chọn tất cả");
                                    } else {
                                        item.setTitle("Bỏ chọn tất cả");
                                    }
                                }
                                adapter.resetSelectBox();
                                mode.setTitle(adapter.getSelectedToken().size() + " / " + totalStudent);
                                return false;
                            }

                            @Override
                            public void onDestroyActionMode(android.view.ActionMode mode) {
                                adapter.toggleShowCheckbox();
                            }
                        });
                        return true;
                    case 2:
                        adapter.setMode(FeedbackAdapter.FILTER_INSTALL_APP);
                        return true;
                    case 3:
                        adapter.setMode(FeedbackAdapter.FILTER_NOT_INSTALL_APP);
                        return true;
                    case 4:
                        adapter.setMode(FeedbackAdapter.FILTER_DEFAULT);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void getListParent(int classroomId) {
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getListParentByIdClassRoom(classroomId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    Type type = new TypeToken<List<ParentPreview>>() {
                    }.getType();
                    list = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    totalStudent = list.size();
                    adapter = new FeedbackAdapter(list, FeedbackFragment.this);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    rcv.setLayoutManager(manager);
                    rcv.setAdapter(adapter);
                    fcmTokens = (ArrayList<String>) list.stream().filter(item -> item.getFcmToken() != null && !item.getFcmToken().equals("")).map(ParentPreview::getFcmToken).collect(Collectors.toList());
                } else {
                    SnackbarUntil.showWarning(requireView(), "Danh sách trống");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                SnackbarUntil.showError(requireView(), "Không thể kết nối tới máy chủ!");
            }
        });
    }

    private void pushNotificationToParent() {
        if (fcmTokens.isEmpty()) {
            saveFeedback();
            Toast.makeText(requireContext(), "Không có phụ huynh nào sử dụng EasyEdu trong lớp!. Hệ thống vẫn sẽ lưu lại tin nhắn này!", Toast.LENGTH_SHORT).show();
        } else {
            FCMBodyRequest bodyRequest = new FCMBodyRequest();
            bodyRequest.setNotification("Bạn có thông báo từ giáo viên", edFeedback.getText().toString().trim());
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
    }

    private void saveFeedback() {
        JsonArray jsonElements = new JsonArray();
        for (ParentPreview detail : list) {
            Feedback feedback = new Feedback(edFeedback.getText().toString().trim(), getArguments().getInt("classroom_id"), detail.getStudentId());
            JsonObject object = new JsonObject();
            object.addProperty("feedback_content", feedback.getContent());
            object.addProperty("feedback_date", feedback.getDate());
            object.addProperty("classroom_id", feedback.getClassroomId());
            object.addProperty("student_id", feedback.getStudentId());
            jsonElements.add(object);
        }
        JsonObject body = new JsonObject();
        body.add("feedbacks", jsonElements);
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).sendMultiFeedback(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpsURLConnection.HTTP_CREATED) {
                    pbTextFieldLoader.setVisibility(View.INVISIBLE);
                    btnSendFeedback.setVisibility(View.VISIBLE);
                    edFeedback.setText("");
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

    @Override
    public void callWithPhoneNumber(ParentPreview parent) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + parent.getPhone()));
        startActivity(intent);
    }

    @Override
    public void onCheckStateChange(boolean isChecked) {
        actionMode.setTitle(adapter.getSelectedToken().size() + " / " + totalStudent);
        if (adapter.getSelectedToken().size() == 0) {
            actionMode.getMenu().getItem(0).setTitle("Chọn tất cả");
        } else {
            actionMode.getMenu().getItem(0).setTitle("Bỏ chọn tất cả");
        }
    }

    @Override
    public void smsTextWithPhoneNumber(ParentPreview parent) {
        if (parent.getFcmToken() == null || parent.getFcmToken().equals("")) {
            new MessageDialog("Thông báo", "Phụ huynh này chưa cài đặt EasyEdu. Bạn có thể gửi tin nhắn SMS nhưng sẽ phát sinh chi phí (tuỳ nhà mạng) vào chính thẻ sim bạn sử dụng. Bạn có muốn tiếp tục ?", "Tiếp tục", "Huỷ bỏ")
                    .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + parent.getPhone()));
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(requireContext(), "Không có ứng dụng mở tin nhắn!", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    }).show();
        } else {
            new MessageDialog("Thông báo", "Phụ huynh đã cài đặt EasyEdu và có thể nhận được thông báo từ app. Bạn có thể gửi tin nhắn SMS nhưng sẽ phát sinh chi phí (tuỳ nhà mạng) vào chính thẻ sim bạn sử dụng. Bạn có muốn tiếp tục ?", "Tiếp tục", "Huỷ bỏ")
                    .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                        @Override
                        public boolean onClick(MessageDialog dialog, View v) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("smsto:" + parent.getPhone()));
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(requireContext(), "Không có ứng dụng mở tin nhắn!", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    }).show();
        }
    }
}