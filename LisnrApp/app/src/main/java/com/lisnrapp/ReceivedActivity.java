package com.lisnrapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.lisnrapp.database.DatabaseHandler;

import java.util.ArrayList;

public class ReceivedActivity extends AppCompatActivity {

    private RecyclerView rvPromosReceived;
    private ArrayList<Recibidos> recibidosList = new ArrayList<>();
    DatabaseHandler databaseHandler;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;
    private RecibidosAdapter adapter;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReceivedActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        rvPromosReceived = findViewById(R.id.rvPromosReceived);


        Button hamburgerButton = findViewById(R.id.hamburger_button);
        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        databaseHandler = new DatabaseHandler(ReceivedActivity.this);
        rvPromosReceived.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvPromosReceived.setAdapter(adapter);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.white));
        navigationView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.white));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.mainFragment) {
                    Intent intent = new Intent(ReceivedActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.receivedFragment) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        chargeRecibidos();
    }

    private void chargeRecibidos() {
        recibidosList.clear(); // Borra los datos existentes en la lista
        sqLiteDatabase = databaseHandler.getReadableDatabase();
        cursor = databaseHandler.getAllData(sqLiteDatabase);

        if(cursor.moveToFirst()){
            int idCounter = 1;
            do{
                String title, hour, url;

                title = cursor.getString(0);
                hour = cursor.getString(1);
                url = cursor.getString(2);

                Log.d("DATABASE", "Data123 received. Title: " + title + ", Hour: " + hour + ", URL: " + url);

                Recibidos recibido = new Recibidos(String.valueOf(idCounter),url, title, hour);
                recibidosList.add(recibido);
                idCounter++;

            } while (cursor.moveToNext());
        }

        adapter = new RecibidosAdapter(recibidosList, getApplicationContext(), new RecibidosAdapter.ItemClickListener(){
            @Override
            public void onItemClick(Recibidos recibidos) {
                String urlObtenido = recibidos.getUrl();
                Intent i = new Intent(ReceivedActivity.this, GalleryActivity.class);
                i.putExtra("urlImg", urlObtenido);
                startActivity(i);
            }
        });
        rvPromosReceived.setAdapter(adapter); // Establece el adaptador en la vista

        adapter.notifyDataSetChanged(); // Actualiza los datos en el adaptador
    }
}