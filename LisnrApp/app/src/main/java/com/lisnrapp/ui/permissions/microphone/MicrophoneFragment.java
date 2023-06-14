package com.lisnrapp.ui.permissions.microphone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lisnrapp.databinding.FragmentMicrophoneBinding;
import com.lisnrapp.ui.home.HomeActivity;
import com.lisnrapp.ui.splash.SplashActivity;

import java.util.Arrays;

public class MicrophoneFragment extends Fragment {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_FILES = 102;

    private FragmentMicrophoneBinding binding;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMicrophoneBinding.inflate(inflater, container, false);
        initListener();
        return binding.getRoot();
    }

    private void initListener() {
        binding.textView5.setOnClickListener(v -> checkAppPermissions());
    }

    public void goToHomeActivity() {
        Intent intent = new Intent(requireActivity(), HomeActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public void checkAppPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_ADMIN
        };

        boolean allPermissionsGranted = true;

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            goToHomeActivity();
        } else {
            requestPermissions(permissions, MY_PERMISSIONS_REQUEST_WRITE_FILES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_FILES) {
            if (grantResults.length > 0
                    && (grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[7] == PackageManager.PERMISSION_GRANTED)) {
                goToHomeActivity();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("App required some permission please enable it")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                openPermissionScreen();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                                requireActivity().finish();
                            }
                        });
                AlertDialog dialog = builder.show();
            }
            return;
        }
    }

    public void openPermissionScreen() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireActivity().getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}