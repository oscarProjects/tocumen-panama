package com.lisnrapp.retrofit;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {

    @Inject ApiRetrofit apiRetrofit;

    CallbackCall callbackCall;

    @Inject
    public ApiManager(){

    }

    public void injectCallback(CallbackCall callbackCall){
        this.callbackCall = callbackCall;
    }

    public void executePost(String service, HashMap<String, String> headerMap, HashMap<String, Object> bodyRequest){
        Call<ResponseBody> call = apiRetrofit.post(service, headerMap, bodyRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null ) {
                    try {
                        JsonObject jo = new JsonParser().parse(response.body().string()).getAsJsonObject();
                        callbackCall.onSuccess(true, jo.get("data"), jo.get("access_token"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                        if(response.body() != null){
                            JsonObject jo = new JsonParser().parse(response.body().string()).getAsJsonObject();
                            callbackCall.onSuccess(false, jo.get("message"), jo.get("errors"));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callbackCall.onFailed(t);
            }
        });
    }


}
