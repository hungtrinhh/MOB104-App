package com.btcdteam.easyedu.fragments.teacher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.btcdteam.easyedu.R;
import com.btcdteam.easyedu.adapter.teacher.ViewPagerAdapter;
import com.btcdteam.easyedu.apis.ServerAPI;
import com.btcdteam.easyedu.models.StudentDetail;
import com.btcdteam.easyedu.network.APIService;
import com.btcdteam.easyedu.utils.FileUtils;
import com.btcdteam.easyedu.utils.ProgressBarDialog;
import com.btcdteam.easyedu.utils.ScoreFileUtils;
import com.btcdteam.easyedu.utils.SnackbarUntil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClassInfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View bgStudent, bgParent;
    private FrameLayout layoutStudent, layoutParent;
    private ViewPager2 viewPager2;
    private Toolbar toolbar, searchToolbar;
    private Menu search_menu;
    private MenuItem item_search;
    private ImageView icSearch, icSetting;
    private FloatingActionButton fabSendFeedback, fabAddStudent, fabAddStudentFile, fabImportScore, fabExportForm;
    private FloatingActionsMenu fabMenu;
    private NestedScrollView scrollView;
    private List<StudentDetail> studentDetailLis01, studentDetailLis02, studentDetailList;
    private int check = 0;
    private ScaleAnimation scaleUpAnimation = new ScaleAnimation(0f, 1.0f, 1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
    private ScaleAnimation scaleDownAnimation = new ScaleAnimation(1f, 0f, 1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
    private ProgressBarDialog progressBarDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        progressBarDialog = new ProgressBarDialog(requireActivity());
        return inflater.inflate(R.layout.fragment_class_info, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getListStudentSemester01();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bgStudent = view.findViewById(R.id.bg_student);
        bgParent = view.findViewById(R.id.bg_parent);
        layoutParent = view.findViewById(R.id.parent_layout);
        layoutStudent = view.findViewById(R.id.student_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        toolbar = view.findViewById(R.id.toolbar);
        icSearch = view.findViewById(R.id.ic_toolbar_search);
        icSetting = view.findViewById(R.id.ic_toolbar_setting);
        fabAddStudent = view.findViewById(R.id.fab_student_add);
        fabSendFeedback = view.findViewById(R.id.fab_student_send_feedback);
        fabAddStudentFile = view.findViewById(R.id.fab_student_add_file);
        fabExportForm = view.findViewById(R.id.fab_export_form);
        fabImportScore = view.findViewById(R.id.fab_import_score);
        fabMenu = view.findViewById(R.id.fab_menu);
        scrollView = view.findViewById(R.id.nsv_scroll);

        swipeRefreshLayout = view.findViewById(R.id.srl_student);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_primary);
        swipeRefreshLayout.setProgressViewOffset(false, 100, 400);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().onBackPressed()
        );
        setSearchToolbar(view);
        setMenuItemSelected();
        toolbar.setTitle(getArguments() != null ? getArguments().getString("classroom_name") : "Classroom");
        setViewPager();
        setBottomNav();


        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putInt("classroom_id", getArguments() != null ? getArguments().getInt("classroom_id") : 0);

        fabSendFeedback.setOnClickListener(v -> {
            if (studentDetailList == null || studentDetailList.size() == 0) {
                SnackbarUntil.showWarning(requireView(), "Không có học sinh nào!");
                return;
            }
            fabMenu.collapse();
            Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_classInfoFragment_to_feedbackFragment, bundle);
        });

        fabAddStudent.setOnClickListener(v -> {
            fabMenu.collapse();
            Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_classInfoFragment_to_editStudentFragment, bundle);
        });
        fabExportForm.setOnClickListener(v -> {
            if (studentDetailList == null || studentDetailList.size() == 0) {
                SnackbarUntil.showWarning(requireView(), "Không có học sinh nào!");
                return;
            }
            fabMenu.collapse();
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.putExtra(Intent.EXTRA_TITLE, getArguments() != null ? getArguments().getString("classroom_name") + ".xlsx" : "export.xlsx");
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
            exportFormLauncher.launch(intent);
        });
        fabAddStudentFile.setOnClickListener(v -> {
            fabMenu.collapse();
            Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_classInfoFragment_to_addFileXlsFragment2, bundle);
        });
        fabImportScore.setOnClickListener(v -> {
            if (studentDetailList == null || studentDetailList.size() == 0) {
                SnackbarUntil.showWarning(requireView(), "Không có học sinh nào!");
                return;
            }
            fabMenu.collapse();
            Navigation.findNavController(requireActivity(), R.id.nav_host_teacher).navigate(R.id.action_classInfoFragment_to_importScoreFragment, bundle);
        });
        //expanded, collapse floating button menu when scroll
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY + 20 && fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }

                if (scrollY < oldScrollY - 20 && fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }
            }
        });
    }

    private void setBottomNav() {
        layoutStudent.setOnClickListener(view -> {
            viewPager2.setCurrentItem(0);
        });

        layoutParent.setOnClickListener(view -> {
            viewPager2.setCurrentItem(1);
        });
    }

    private void setViewPager() {
        scaleDownAnimation.setDuration(200);
        scaleUpAnimation.setDuration(200);
        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity());
        viewPager2.setAdapter(adapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bgParent.startAnimation(scaleDownAnimation);
                        bgParent.setVisibility(View.GONE);

                        bgStudent.setVisibility(View.VISIBLE);
                        bgStudent.startAnimation(scaleUpAnimation);
                        break;
                    case 1:
                        bgParent.setVisibility(View.VISIBLE);
                        bgParent.startAnimation(scaleUpAnimation);

                        bgStudent.startAnimation(scaleDownAnimation);
                        bgStudent.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void setMenuItemSelected() {

        icSearch.setOnClickListener(v -> {
            circleReveal(R.id.searchtoolbar, 1, true, true);
            item_search.expandActionView();
        });

        icSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check == 0) {
                    icSetting.setImageResource(R.drawable.semeter02);
                    getListStudentSemester02();
                    Snackbar.make(view, "Kì 2", Toast.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.blue_primary, requireActivity().getTheme())).show();
                    check = 1;
                } else {
                    icSetting.setImageResource(R.drawable.semeter01);
                    getListStudentSemester01();
                    Snackbar.make(view, "Kì 1", Toast.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.blue_primary, requireActivity().getTheme())).show();
                    check = 0;
                }
            }
        });

    }

    void broadCast(List<StudentDetail> list) {
        Intent intent = new Intent("ACTION");
        Bundle bundle = new Bundle();
        bundle.putSerializable("Array", (Serializable) list);
        intent.putExtras(bundle);
        requireActivity().sendBroadcast(intent);
    }

    private void setSearchToolbar(@NonNull View view) {
        searchToolbar = view.findViewById(R.id.searchtoolbar);
        if (searchToolbar != null) {
            searchToolbar.inflateMenu(R.menu.menu_search);
            search_menu = searchToolbar.getMenu();

            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleReveal(R.id.searchtoolbar, 1, true, false);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    circleReveal(R.id.searchtoolbar, 1, true, false);
                    Intent intent = new Intent("SEARCH");
                    intent.putExtra("search", "");
                    requireActivity().sendBroadcast(intent);
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();

        } else
            Log.d("toolbar", "setSearchtollbar: NULL");
    }

    // bat su kien search view o day
    private void initSearchView() {
        final SearchView searchView =
                (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_round_close_24);


        // set hint and the text colors

        EditText txtSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        txtSearch.setHint("Tìm kiếm...");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.blue_primary, requireActivity().getTheme()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                Intent intent = new Intent("SEARCH");
                intent.putExtra("search", query);
                requireActivity().sendBroadcast(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                Log.e(">>>>>>>", "onQueryTextChange: " + newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                Log.i("query", "" + query);
            }

        });

    }

    private void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = requireView().findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();
    }

    private void getListStudentSemester01() {
        if(!swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(true);

        int classroomId = getArguments() != null ? getArguments().getInt("classroom_id") : 0;
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getListStudentByIdClassRoom(classroomId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    Type type = new TypeToken<List<StudentDetail>>() {
                    }.getType();
                    studentDetailList = new ArrayList<>();
                    studentDetailLis01 = new ArrayList<>();
                    studentDetailList = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    for (StudentDetail o : studentDetailList) {
                        if (o.getSemester() % 2 != 0) {
                            studentDetailLis01.add(o);
                        }
                    }
                    broadCast(studentDetailLis01);
                }
                if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListStudentSemester02() {
        int classroomId = getArguments() != null ? getArguments().getInt("classroom_id") : 0;
        Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getListStudentByIdClassRoom(classroomId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    Type type = new TypeToken<List<StudentDetail>>() {
                    }.getType();
                    studentDetailLis02 = new ArrayList<>();
                    studentDetailList = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                    for (StudentDetail o : studentDetailList) {
                        if (o.getSemester() % 2 == 0) {
                            studentDetailLis02.add(o);
                        }
                    }
                    broadCast(studentDetailLis02);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> exportFormLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    //no data present
                    return;
                }
                progressBarDialog.setMessage("Loading").show();
                int classroomId = getArguments() != null ? getArguments().getInt("classroom_id") : 0;
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).getListStudentByIdClassRoom(classroomId);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 200) {
                            Type type = new TypeToken<List<StudentDetail>>() {
                            }.getType();
                            studentDetailList = new Gson().fromJson(response.body().getAsJsonArray("data"), type);
                            String filePath = new FileUtils(requireActivity()).getPath(result.getData().getData());
                            new ScoreFileUtils(filePath, studentDetailList, new ScoreFileUtils.addOnCompleteListener() {
                                @Override
                                public void onComplete(Uri export) {
                                    progressBarDialog.dismiss();
                                    new MessageDialog("Thông báo", "Xuất bảng điểm thành công!", "Mở", "Đóng")
                                            .setOkButtonClickListener(new OnDialogButtonClickListener<MessageDialog>() {
                                                @Override
                                                public boolean onClick(MessageDialog dialog, View v) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(export, "*/*");
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    startActivity(intent);
                                                    return false;
                                                }
                                            }).show();

                                }

                                @Override
                                public void onError(Exception e) {
                                    progressBarDialog.dismiss();
                                    Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).exportScoreInputFile();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(requireContext(), "Lỗi kết nối tới máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    });

    @Override
    public void onRefresh() {
        getListStudentSemester01();
    }
}