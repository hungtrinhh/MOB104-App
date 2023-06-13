package com.btcdteam.easyedu.adapter.teacher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.btcdteam.easyedu.fragments.teacher.subfragment.ParentFragment;
import com.btcdteam.easyedu.fragments.teacher.subfragment.StudentFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new StudentFragment();
            case 1:
                return new ParentFragment();
            default:
                return new StudentFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
