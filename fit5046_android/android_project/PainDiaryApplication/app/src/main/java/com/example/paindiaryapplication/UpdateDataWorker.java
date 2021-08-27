package com.example.paindiaryapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.paindiaryapplication.dao.PainDAO;
import com.example.paindiaryapplication.database.PainDatabase;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.math3.distribution.LogNormalDistribution;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateDataWorker extends Worker {
    private PainViewModel painViewModel;
    public UpdateDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String email = getInputData().getString("current_user");
        Date date= new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String entryDate = df.format(date);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("upload", Context.MODE_PRIVATE);
        String uploadInfo = sharedPref.getString("uploadInfo", "" );
        if (compareCurrentHour(22)){
            if (!uploadInfo.equals(email+entryDate)){
                //in 10pm, have not upload yet
                SharedPreferences.Editor spEditor = sharedPref.edit();
                spEditor.putString("uploadInfo", email+entryDate);
                spEditor.apply();
            }else {
                //in 10pm, already upload
                return Result.retry();
            }
        }else {
            //not in 10pm, put upload flag to false
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putString("uploadInfo", "");
            spEditor.apply();
            return Result.retry();
        }
        //Get data from db
        PainDatabase db = PainDatabase.getInstance(getApplicationContext());
        PainDAO painDAO = db.painDAO();
        PainRecord painRecord = painDAO.getRecordByDateEmail(email, entryDate);
        if (!(painRecord == null)){
            Log.d("worker test", painRecord.userEmail + " uploading now");
            //Call Firebase class and sync/upload
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            ref.child(painRecord.uid + "").child(entryDate).setValue(painRecord)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("worker test", painRecord.userEmail + " uploading success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("worker test", painRecord.userEmail + " uploading failed " + e.getMessage());
                        }
                    });
            return Result.success();
        }
        else {
            //no records now
            SharedPreferences.Editor spEditor = sharedPref.edit();
            spEditor.putString("uploadInfo", "");
            spEditor.apply();
            return Result.retry();
        }
    }
    public boolean compareCurrentHour(int hour){
        int current = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return current == hour;
    }
}
