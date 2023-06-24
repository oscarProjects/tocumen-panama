package com.lisnrapp.retrofit;

import com.google.gson.JsonElement;

public interface CallbackCall {
    void onSuccess(boolean isSuccessful, JsonElement element, JsonElement element2);
    void onFailed(Throwable throwable);
}
