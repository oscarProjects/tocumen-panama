package com.lisnrapp.ui.permissions;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.lisnrapp.animation.ZoomOutPageTransformer;
import com.lisnrapp.databinding.ActivityPermissionsBinding;
import com.lisnrapp.ui.permissions.microphone.MicrophoneFragment;
import com.lisnrapp.ui.permissions.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionsActivity extends FragmentActivity {

    private ActivityPermissionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.tablayout.setupWithViewPager(binding.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new NotificationsFragment());
        adapter.addFrag(new MicrophoneFragment());

        binding.viewpager.setPageTransformer(true, new ZoomOutPageTransformer());
        binding.viewpager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (binding.viewpager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() - 1);
        }
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add("");
        }
    }
}