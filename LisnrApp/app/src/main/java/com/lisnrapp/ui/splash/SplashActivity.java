package com.lisnrapp.ui.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lisnrapp.MainActivity;
import com.lisnrapp.R;
import com.lisnrapp.ui.register.RegisterActivity;

import java.util.Arrays;

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