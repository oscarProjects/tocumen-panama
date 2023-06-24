package com.lisnrapp.ui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lisnrapp.MainActivity;
import com.lisnrapp.MyApplication;
import com.lisnrapp.database.DatabaseHandler;
import com.lisnrapp.databinding.ActivityLoginBinding;
import com.lisnrapp.ui.permissions.PermissionsActivity;
import com.lisnrapp.ui.register.RegisterActivity;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {


    private static String TAG = LoginActivity.class.getSimpleName();

    private DatabaseHandler db;
    private SQLiteDatabase sqLiteDatabase;
    private InputMethodManager imm;
    private ActivityLoginBinding binding;
    @Inject
    LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((MyApplication) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnLogin.setOnClickListener(this::loginValidations);

        binding.btnAccountRegister.setOnClickListener(v -> navigateToRegister());

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void loginValidations(View v){
        showProgress(true);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        validateFields();
    }

    private void showProgress(Boolean show){
        binding.pgbLogin.setVisibility((show ? View.VISIBLE : View.GONE));
        if(show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void validateFields() {
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(binding.etUserName.getText().toString().trim())) {
            focusView = binding.etUserName;
            cancel = true;
            Toast.makeText(getApplicationContext(),"Por favor ingrese un correo electrónico",Toast.LENGTH_LONG).show();
        } else if (!isEmailValid(binding.etUserName.getText().toString().trim())) {
            focusView = binding.etUserName;
            cancel = true;
            binding.etUserName.setText("");
            Toast.makeText(getApplicationContext(),"Correo electrónico inválido, por favor ingrese un correo válido",Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
            focusView = binding.etPassword;
            cancel = true;
            Toast.makeText(getApplicationContext(),"Por favor ingrese una contraseña",Toast.LENGTH_LONG).show();
        }
        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
            showProgress(false);
        } else {
            requestLogin();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void requestLogin(){
        initObservers();
        loginViewModel.requestLogin(binding.etUserName.getText().toString().trim(), binding.etPassword.getText().toString().trim(), this);
    }

    private void initObservers(){
        loginViewModel.responseLogin().observe(this, loginModel -> {
            if(loginModel != null){
                showProgress(false);
                loginSuccess();
            }
        });
    }

    private void loginSuccess() {
        Intent forgotActivity = new Intent(LoginActivity.this, PermissionsActivity.class);
        startActivity(forgotActivity);
        finish();
        Toast.makeText(this, "Bienvenido ", Toast.LENGTH_LONG).show();
    }

    private void navigateToRegister(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveData(int id) {

        db = new DatabaseHandler(LoginActivity.this);
        sqLiteDatabase = db.getWritableDatabase();


        db.insertUsers(sqLiteDatabase,"titulo", "hora", "url");
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

    /*private void loginRequest(String email, String password) {

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
                    showProgress(false);
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
                showProgress(false);
                binding.etUserName.setText("");
            }
        });
    }*/



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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}