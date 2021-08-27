package com.example.paindiaryapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class MapSettingViewModel extends ViewModel {
    private MutableLiveData<String> latLong;

    public MapSettingViewModel(){
        latLong = new MutableLiveData<>();
    }

    public void setLatLong(String latLong){
        this.latLong.setValue(latLong);
    }
    public LiveData<String> getText(){
        return latLong;
    }
}
