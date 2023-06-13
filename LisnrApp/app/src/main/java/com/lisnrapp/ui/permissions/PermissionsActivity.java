package com.lisnrapp.ui.permissions;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.lisnrapp.animation.ZoomOutPageTransformer;
import com.lisnrapp.databinding.ActivityPermissionsBinding;
import com.lisnrapp.adapters.ViewPagerAdapter;
import com.lisnrapp.ui.permissions.microphone.MicrophoneFragment;
import com.lisnrapp.ui.permissions.notifications.NotificationsFragment;

public class PermissionsActivity extends FragmentActivity {

    private ActivityPermissionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this.getLifecycle());

        adapter.addFrag(new NotificationsFragment());
        adapter.addFrag(new MicrophoneFragment());

        binding.viewpager.setPageTransformer(new ZoomOutPageTransformer());
        binding.viewpager.setAdapter(adapter);

        new TabLayoutMediator(binding.tablayout, binding.viewpager,
                (tab, position) -> {
                    binding.viewpager.setCurrentItem(tab.getPosition(), true);
                }).attach();
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
}