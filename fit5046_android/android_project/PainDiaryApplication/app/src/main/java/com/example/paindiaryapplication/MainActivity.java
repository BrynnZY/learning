package com.example.paindiaryapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;

import com.example.paindiaryapplication.databinding.ActivityMainBinding;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bindging;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindging = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bindging.getRoot();
        setContentView(view);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Data inputData = new Data.Builder().putString("current_user", email).build();

        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(UpdateDataWorker.class, 15, TimeUnit.MINUTES)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance().enqueue(request);
        Log.d("worker", "work init" );
        WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId()).observe(MainActivity.this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                Log.d("onChanged()->", "workInfo" + workInfo);
            }
        });

        setSupportActionBar(bindging.appBar.toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_fragment,
                R.id.nav_data_entry_fragment,
                R.id.nav_daily_records_fragment,
                R.id.nav_report_fragment,
                R.id.nav_map_fragment
        ).setOpenableLayout(bindging.drawerLayout)
                .build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment)fragmentManager.findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bindging.navView, navController);
        NavigationUI.setupWithNavController(bindging.appBar.toolbar, navController, mAppBarConfiguration);

    }
}