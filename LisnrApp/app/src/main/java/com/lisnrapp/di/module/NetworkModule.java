package com.lisnrapp.di.module;

import com.lisnrapp.retrofit.ApiRetrofit;
import com.lisnrapp.retrofit.ClientRetrofit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    public ApiRetrofit provideRetrofitService(){
        return ClientRetrofit.getApiRetrofit(ClientRetrofit.getRetrofitInstance());
    }
}
