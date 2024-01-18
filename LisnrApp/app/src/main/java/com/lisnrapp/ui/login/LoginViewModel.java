package com.lisnrapp.ui.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lisnrapp.data.login.model.LoginModel;
import com.lisnrapp.data.login.repository.LoginRepository;
import com.lisnrapp.databinding.ActivityLoginBinding;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private MutableLiveData<LoginModel> mutableLiveData;

    private final LoginRepository loginRepository;

    @Inject
    public LoginViewModel(LoginRepository loginRepository) {
        mText = new MutableLiveData<>();
        mText.setValue("Cupones");

        this.loginRepository = loginRepository;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void validateFields(ActivityLoginBinding binding, Context context, Activity activity) {
        showProgress(binding,true, activity);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(binding.etUserName.getText().toString().trim())) {
            focusView = binding.etUserName;
            cancel = true;
            Toast.makeText(context,"Por favor ingrese un correo electrónico",Toast.LENGTH_LONG).show();
        } else if (!isEmailValid(binding.etUserName.getText().toString().trim())) {
            focusView = binding.etUserName;
            cancel = true;
            binding.etUserName.setText("");
            Toast.makeText(context,"Correo electrónico inválido, por favor ingrese un correo válido",Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
            focusView = binding.etPassword;
            cancel = true;
            Toast.makeText(context,"Por favor ingrese una contraseña",Toast.LENGTH_LONG).show();
        }
        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
            showProgress(binding,false, activity);
        } else {
            loginRepository.requestLogin(binding.etUserName.getText().toString().trim(), binding.etPassword.getText().toString().trim(), context);
        }
    }

    public void showProgress(ActivityLoginBinding binding, Boolean show, Activity activity){
        binding.pgbLogin.setVisibility((show ? View.VISIBLE : View.GONE));
        if(show) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public LiveData<LoginModel> responseLogin() {
        if(mutableLiveData==null){
            mutableLiveData = loginRepository.mutableLiveData;
        }
        return mutableLiveData;
    }

}
