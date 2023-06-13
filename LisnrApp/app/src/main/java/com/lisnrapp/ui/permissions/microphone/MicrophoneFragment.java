package com.lisnrapp.ui.permissions.microphone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lisnrapp.databinding.FragmentMicrophoneBinding;
import com.lisnrapp.ui.home.HomeActivity;

public class MicrophoneFragment extends Fragment {

    private FragmentMicrophoneBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMicrophoneBinding.inflate(inflater, container, false);
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        binding.textView5.setOnClickListener(this::goToHomeActivity);
    }

    public void goToHomeActivity(View v) {
        Intent intent = new Intent(requireActivity(), HomeActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}