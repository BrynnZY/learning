package com.example.paindiaryapplication;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://api.openweathermap.org/";

    public static WeatherRetrofitInterface getRetrofitService(){
        Log.d("initial","5");
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherRetrofitInterface.class);
    }
}
