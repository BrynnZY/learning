package com.example.paindiaryapplication;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeatherResult(@Query("appid") String API_KEY,
                                           @Query("q") String cityName);
}
