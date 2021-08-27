package com.example.paindiaryapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiaryapplication.databinding.HomeFragmentBinding;
import com.example.paindiaryapplication.viewmodel.WeatherViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private HomeFragmentBinding homeBinding;
    //weather api
    private static final String API_KEY = "0a8440a6323bde2cae39b6fab868f1c0";
    private String location;
    //private WeatherRetrofitInterface retrofitInterface;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = homeBinding.getRoot();
        //retrofitInterface = RetrofitClient.getRetrofitService();
        location = "Melbourne";
        WeatherRetrofitInterface retrofitInterface = RetrofitClient.getRetrofitService();
        Call<WeatherResponse> callAsync = retrofitInterface.getWeatherResult(API_KEY, location);
        callAsync.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        Main main = response.body().main;
                        homeBinding.messageText.setText("Current Location: " + location);
                        homeBinding.temperature.setText(String.format("%.2f", (main.getTemp()- 273.15)) + " ℃");
                        homeBinding.humidiry.setText("Current Humidity: " + Integer.toString(main.getHumidity()) + " %");
                        homeBinding.pressure.setText("Current Pressure: " + Integer.toString(main.getPressure()) + " hPa");
                        WeatherViewModel model = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
                        model.temp = main.getTemp() - 273.15;
                        model.humidiry = main.getHumidity();
                        model.pressure = main.getPressure();

                    }else {
                        homeBinding.messageText.setText("No result, please enter correct city name");
                    }
                }else {
                    homeBinding.messageText.setText("Search failed, no such location");
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                homeBinding.messageText.setText(t.getMessage());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }
}
