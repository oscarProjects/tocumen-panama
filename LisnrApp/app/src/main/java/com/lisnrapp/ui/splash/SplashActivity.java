package com.lisnrapp.ui.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lisnrapp.MainActivity;
import com.lisnrapp.R;
import com.lisnrapp.ui.register.RegisterActivity;

public class SplashActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_FILES = 102;
    private AlertDialog dialog;
    Cursor cursor;
    SQLiteDatabase sqLiteDatabase;

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

                    // After 5 seconds redirect to another intent
                    checkAppPermissions();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // start thread
        background.start();
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
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            //go_next_Main();
            openPermissionScreen();
        } else {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_WRITE_FILES);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_FILES) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //go_next_Main();
                openPermissionScreen();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
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
                            }
                        });
                dialog = builder.show();
            }
            return;
        }
    }

    private void go_next_Main() {

        Intent startmain = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(startmain);
        finish();

    }

    public void openPermissionScreen() {

        Intent startmain = new Intent(SplashActivity.this, RegisterActivity.class);
        startActivity(startmain);
        finish();

        // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                Uri.fromParts("package", SplashActivity.this.getPackageName(), null));
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

}