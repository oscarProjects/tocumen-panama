package com.lisnrapp.ui.home.tickets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TicketsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tickets fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}