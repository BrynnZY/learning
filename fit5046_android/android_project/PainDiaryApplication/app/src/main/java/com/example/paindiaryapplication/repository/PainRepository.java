package com.example.paindiaryapplication.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.paindiaryapplication.dao.PainDAO;
import com.example.paindiaryapplication.database.PainDatabase;
import com.example.paindiaryapplication.entity.PainRecord;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PainRepository {
    private PainDAO painDAO;
    private LiveData<List<PainRecord>> painRecords;
    private LiveData<List<String>> locatinCount;

    public PainRepository(Application application){
        PainDatabase db = PainDatabase.getInstance(application);
        painDAO = db.painDAO();
    }

    public LiveData<List<PainRecord>> getPainRecords(final String email){
        painRecords = painDAO.getAllByEmail(email);
        return painRecords;
    }

    public LiveData<List<String>> getLocationCount(final String email){
        locatinCount = painDAO.getLocationCount(email);
        return locatinCount;
    }

    public void insert(final PainRecord painRecord){
        PainDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painDAO.insert(painRecord);
            }
        });
    }

    public void updateRecord(final PainRecord painRecord){
        PainDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painDAO.updatePainRecord(painRecord);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateEmailFuture(final String email, final String date){
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painDAO.getRecordByDateEmail(email,date);
            }
        }, PainDatabase.databaseWriteExecutor);
    }
}
