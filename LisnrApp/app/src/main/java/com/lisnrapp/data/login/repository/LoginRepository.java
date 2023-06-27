package com.lisnrapp.data.login.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lisnrapp.R;
import com.lisnrapp.data.login.model.LoginModel;
import com.lisnrapp.retrofit.ApiManager;
import com.lisnrapp.retrofit.CallbackCall;
import com.lisnrapp.utils.UtilsLisnr;

import java.util.HashMap;

import javax.inject.Inject;

public class LoginRepository implements CallbackCall {

    private final String TAG = getClass().getSimpleName();

    ApiManager apiManager;

    @Inject
    public LoginRepository(ApiManager apiManager){
        this.apiManager = apiManager;
    }

    public MutableLiveData<LoginModel> mutableLiveData = new MutableLiveData<>();

    public void requestLogin(String user, String password, Context context) {

        HashMap<String, Object> bodyRequest = new HashMap<>();
        bodyRequest.put("email", user);
        bodyRequest.put("password", password);
        bodyRequest.put("device_name", UtilsLisnr.getDeviceName());

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept", "application/json");

        apiManager.injectCallback(this);

        apiManager.executePost(context.getString(R.string.service_login), headerMap, bodyRequest);
    }


    @Override
    public void onSuccess(boolean isSuccessful, JsonElement element, JsonElement element2) {
        if(isSuccessful){
            LoginModel loginResponse = new Gson().fromJson(element, LoginModel.class);
            mutableLiveData.postValue(loginResponse);
        }else{
            mutableLiveData.postValue(null);
        }
    }

    @Override
    public void onFailed(Throwable throwable) {
        mutableLiveData.postValue(null);
    }
}