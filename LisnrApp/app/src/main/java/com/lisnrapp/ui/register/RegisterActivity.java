package com.lisnrapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lisnrapp.ui.login.LogginActivity;

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
            Intent startmain = new Intent(RegisterActivity.this, LogginActivity.class);
            startActivity(startmain);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}