package com.lisnrapp.di.component;

import com.lisnrapp.di.module.NetworkModule;
import com.lisnrapp.ui.login.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = NetworkModule.class)
public interface ApplicationComponent {

    void inject(LoginActivity loginActivity);

}
