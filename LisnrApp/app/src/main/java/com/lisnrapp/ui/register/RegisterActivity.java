package com.lisnrapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lisnrapp.databinding.ActivityRegisterBinding;
import com.lisnrapp.ui.login.LogginActivity;
import com.lisnrapp.ui.permissions.PermissionsActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initListener();
    }

    private void initListener(){
        binding.btnNoAcountQuestion.setOnClickListener(v -> {
            Intent intentLogin = new Intent(RegisterActivity.this, LogginActivity.class);
            startActivity(intentLogin);
            finish();
        });

        binding.btnLoggin.setOnClickListener(v -> {
            Intent intentPermissions = new Intent(RegisterActivity.this, PermissionsActivity.class);
            startActivity(intentPermissions);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}