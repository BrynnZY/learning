package com.example.paindiaryapplication.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.repository.PainRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainViewModel  extends AndroidViewModel {
    private PainRepository painRepository;
    private LiveData<List<PainRecord>> painRecords;
    private LiveData<List<String>> locationCount;

    public PainViewModel(Application application){
        super(application);
        painRepository = new PainRepository(application);
    }

    public LiveData<List<String>> getLocationCount(final String email){
        locationCount = painRepository.getLocationCount(email);
        return locationCount;
    }

    public LiveData<List<PainRecord>> getPainRecords(final String email){
        painRecords = painRepository.getPainRecords(email);
        return painRecords;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateEmailFuture(final String email, final String date){
        return painRepository.findByDateEmailFuture(email, date);
    }

    public void insert(PainRecord painRecord){
        painRepository.insert(painRecord);
    }
    public void update(PainRecord painRecord){
        painRepository.updateRecord(painRecord);
    }
}
