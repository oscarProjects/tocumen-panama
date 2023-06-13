package com.lisnrapp.ui.home.cupones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CuponesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CuponesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is cupones fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}