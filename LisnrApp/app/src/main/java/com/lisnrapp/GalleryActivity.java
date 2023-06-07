package com.lisnrapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.lisnrapp.database.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {

    private String urlImg;
    private WebView imgShowPromo;
    private Button btn_return, buttonSaveImg;
    private String lastSegmentUrl;
    private ProgressBar pgb;
    private LinearLayout containAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        urlImg = getIntent().getStringExtra("urlImg");
        imgShowPromo = findViewById(R.id.imgShowPromo);

        btn_return = findViewById(R.id.btn_return);
        buttonSaveImg = findViewById(R.id.buttonSaveImg);
        pgb = findViewById(R.id.pgb);
        containAll = findViewById(R.id.containAll);
        containAll.setVisibility(View.GONE);

        buttonSaveImg.setVisibility(View.GONE);


        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgShowPromo.canGoBack()) {
                    imgShowPromo.goBack();
                } else {
                    // Borrar la URL del WebView
                    imgShowPromo.loadData("", "text/html", null);
                    onBackPressed();
                    finish();
                }
            }
        });

        chargeUrl();
        startThreadProgressBar();

    }

    private void startThreadProgressBar() {
        new Handler().postDelayed(new Runnable(){
            public void run(){

                //----------------------------
                pgb.setVisibility(View.GONE);
                containAll.setVisibility(View.VISIBLE);

                //----------------------------

            }
        }, 5000); //5000 millisegundos = 5 segundos.


    }

    private void chargeUrl() {

        imgShowPromo.getSettings().setLoadWithOverviewMode(true);
        imgShowPromo.getSettings().setUseWideViewPort(true);
        imgShowPromo.getSettings().setJavaScriptEnabled(true);
        imgShowPromo.setWebViewClient(new WebViewClient());
        imgShowPromo.loadUrl(urlImg);
        //opcion para que se reproduzca al cargar sin darle click
//        imgShowPromo.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                if (progress == 100) {
//                    imgShowPromo.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
//                }
//            }
//        });


        //obtener el ultimo parametro de la url
        Uri uri = Uri.parse(urlImg);
        lastSegmentUrl = uri.getLastPathSegment();

        saveInDb();

    }


    private void saveInDb() {
        DatabaseHandler db;
        SQLiteDatabase sqLiteDatabase;
        db = new DatabaseHandler(GalleryActivity.this);
        sqLiteDatabase = db.getWritableDatabase();

        // Consultar si el URL ya existe en la base de datos
        String[] columns = {"url"};
        String selection = "url = ?";
        String[] selectionArgs = {urlImg};
        Cursor cursor = sqLiteDatabase.query("recibidosTable", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            // El URL ya existe en la base de datos
            System.out.printf(" Data123 Este URL ya existe en la base de datos");
        } else {
            // El URL no existe en la base de datos
            db.insertUsers(sqLiteDatabase, lastSegmentUrl, getHour(), urlImg);
            System.out.printf("Data123 inserted");
        }

        cursor.close();
        db.close();
    }


    private String getHour() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        SimpleDateFormat format = new SimpleDateFormat("h:mma", Locale.getDefault());
        String formattedTime = format.format(calendar.getTime());
        int hour = hourOfDay % 12;
        if (hour == 0) {
            hour = 12;
        }

        String amPm = hourOfDay < 12 ? "AM" : "PM";
        String result = String.format("%d:%02d%s", hour, minute, amPm);

        return result;
    }
}