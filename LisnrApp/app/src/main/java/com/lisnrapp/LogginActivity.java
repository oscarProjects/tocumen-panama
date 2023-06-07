package com.lisnrapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LogginActivity extends AppCompatActivity implements View.OnClickListener {


    private static String TAG = LogginActivity.class.getSimpleName();

    private Button btnLoggin,btnRegistered;
    private EditText et_email, et_password;
    //    private TextView btnSkip;
    private ProgressBar pgb;
    private DatabaseHandler db;
    private SQLiteDatabase sqLiteDatabase;
    private InputMethodManager imm;
    private TextView recoverPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_loggin);


        pgb = findViewById(R.id.pgbLogin);
        et_email = findViewById(R.id.et_login_email);
        et_password = findViewById(R.id.et_password);
        btnLoggin = findViewById(R.id.btnLoggin);
        btnRegistered = findViewById(R.id.btnRegistered);
        recoverPassword = findViewById(R.id.recoverPassword);

        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogginActivity.this, RecoverPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLoggin.setOnClickListener(this);
        btnRegistered.setOnClickListener(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);



    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLoggin:
                btnLoggin.setClickable(false);
                btnRegistered.setClickable(false);
                pgb.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                validateFields();
                break;
            case R.id.btnRegistered:
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    private void validateFields() {
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(et_email.getText().toString())) {
            focusView = et_email;
            cancel = true;
            Toast.makeText(getApplicationContext(),"Por favor ingrese un correo electrónico",Toast.LENGTH_LONG).show();
        }
        else if (!isEmailValid(et_email.getText().toString())) {
            focusView = et_email;
            cancel = true;
            et_email.setText("");
            Toast.makeText(getApplicationContext(),"Correo electrónico inválido, por favor ingrese un correo válido",Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(et_password.getText().toString())) {
            focusView = et_password;
            cancel = true;
            Toast.makeText(getApplicationContext(),"Por favor ingrese una contraseña",Toast.LENGTH_LONG).show();

        }
        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();

            pgb.setVisibility(View.GONE);
            btnLoggin.setClickable(true);
            btnRegistered.setClickable(true);

        } else {
            //continue el camino
            loginRequest(et_email.getText().toString(), et_password.getText().toString());
        }
    }

    private void attemptLogin() {
        pgb.setVisibility(View.GONE);
        Intent forgotActivity = new Intent(LoginActivity.this, NavigationDrawerActivity.class);
        startActivity(forgotActivity);
        finish();
        Toast toast = Toast.makeText(this, "Bienvenido ", Toast.LENGTH_LONG);
        toast.show();

    }

    private void saveData(int id) {

        db = new DatabaseHandler(LoginActivity.this);
        sqLiteDatabase = db.getWritableDatabase();


        db.insertUsers(sqLiteDatabase,id);
        db.close();


        db = new DatabaseHandler(getApplicationContext());

//        db.insertUsers(db,id,getnameUser,getnumberPhone,getemail);


//        ContentValues values = new ContentValues();
//       values.put(Utilidades.CAMPO_ID, id);
//       values.put(Utilidades.CAMPO_NOMBRE, getnameUser);
//       values.put(Utilidades.CAMPO_TELEFONO, getnumberPhone);
//       values.put(Utilidades.CAMPO_EMAIL, getemail);
//
//       Long idResultante=db.insert(Utilidades.TABLA_USUARIO,Utilidades.CAMPO_ID,values);


//        DatabaseHandler databaseHandler = new DatabaseHandler(LoginActivity.this);
//        SQLiteDatabase db = databaseHandler.getWritableDatabase();

//        String id = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        ContentValues values = new ContentValues();
//        values.put(id , ID_UNIQ_DISP);
//        values.put(getnameUser, NAME_USER);
//        values.put(getnumberPhone, NUMBER_PHONE_USER);
//        values.put(getemail, EMAIL_USER);
//
//
//        return db.insert(CART_TABLE, null, values);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private void loginRequest(String email, String password) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("correo", email);
        jsonObject.addProperty("clave", password);

        ApiService apiService = ApiMain.getClient().create(ApiService.class);

        Call<LoginResponse> call = apiService.login(jsonObject);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    int id = loginResponse.getId();
                    String mensaje = loginResponse.getMensaje();
                    //mensaje de loggin exitoso
//                    showAlertDialog(mensaje);
                    saveData(id);
                    attemptLogin();
                } else {
                    pgb.setVisibility(View.GONE);
                    btnLoggin.setClickable(true);
                    btnRegistered.setClickable(true);
                    et_password.setText("");
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        String mensaje = jsonObject.getString("Message");
                        showAlertDialog(mensaje);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        showAlertDialog("Error inesperado, por favor intente nuevamente");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showAlertDialog("Error de conexión, por favor intente nuevamente");
                pgb.setVisibility(View.GONE);
                btnLoggin.setClickable(true);
                btnRegistered.setClickable(true);
                et_password.setText("");
            }
        });
    }



    private void showAlertDialog(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}