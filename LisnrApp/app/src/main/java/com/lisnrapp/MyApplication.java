package com.lisnrapp;

import android.app.Application;

import com.lisnrapp.di.component.ApplicationComponent;
import com.lisnrapp.di.component.DaggerApplicationComponent;


public class MyApplication extends Application {

    ApplicationComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerApplicationComponent.create();
    }

    public ApplicationComponent getAppComponent(){
        return appComponent;
    }

}
