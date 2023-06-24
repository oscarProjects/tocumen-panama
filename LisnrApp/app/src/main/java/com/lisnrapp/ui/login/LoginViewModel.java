package com.lisnrapp.ui.login;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lisnrapp.data.model.LoginModel;
import com.lisnrapp.data.repository.LoginRepository;

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

    public LiveData<LoginModel> responseLogin() {
        if(mutableLiveData==null){
            mutableLiveData = loginRepository.mutableLiveData;
        }
        return mutableLiveData;
    }

    public void requestLogin(String user, String password, Context context){
        loginRepository.requestLogin(user, password, context);
    }



}
