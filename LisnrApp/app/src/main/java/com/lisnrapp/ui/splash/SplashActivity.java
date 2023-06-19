package com.lisnrapp.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lisnrapp.R;
import com.lisnrapp.ui.register.RegisterActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(2 * 1000);

                    go_to_Register();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // start thread
        background.start();
    }

    private void go_to_Register() {
        Intent startmain = new Intent(SplashActivity.this, RegisterActivity.class);
        startActivity(startmain);
        finish();
    }


}