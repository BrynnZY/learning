package com.example.paindiaryapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiaryapplication.databinding.DataEntryFragmentBinding;
import com.example.paindiaryapplication.entity.PainRecord;
import com.example.paindiaryapplication.viewmodel.PainViewModel;
import com.example.paindiaryapplication.viewmodel.WeatherViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class DataEntryFragment extends Fragment {
    private DataEntryFragmentBinding entryBinding;

    //pain data
    private int painLevel = -1;
    private String painLocation = "";
    private String painMood = "";
    private int goal = -1;
    private int steps = -1;
    private String entryDate = "";
    private String email;

    //viewmodel
    private PainViewModel painViewModel;

    public DataEntryFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        entryBinding = DataEntryFragmentBinding.inflate(inflater, container, false);
        View view = entryBinding.getRoot();

        String[] spinnerItems = {"Back", "Neck", "Head", "Knees", "Hips", "Abdomen",
                "Elbows", "Shoulders", "Shins", "Jaw", "Facial"};
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(spinnerItems));
        final ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, list);
        entryBinding.spinner.setAdapter(spinnerAdapter);
        //time picker
        Calendar cal = Calendar.getInstance();
        //alarmManager
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), myReceiver.class);
        intent.setAction("Alarm");
        intent.putExtra("msg","Please click EDIT to enter daily data");
        PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //using share preference to save alarm setting for that day
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        int hour = sharedPref.getInt("hour", -1);
        int min = sharedPref.getInt("min", -1);
        String alarmDate = sharedPref.getString("date","");
        String user = sharedPref.getString("user","");
        //current date
        Date date= new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        entryDate = df.format(date);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //current weather
        WeatherViewModel model = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        //first put all input not enabled
        inputEnables(false);
        entryBinding.editButton.setEnabled(false);
        entryBinding.saveButton.setEnabled(false);
        //viewmodel
        painViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainViewModel.class);
        //alarm not set for today
        if (!user.equals(email) || min == -1 || hour == -1 || !alarmDate.equals(entryDate)){
            entryBinding.messageText.setText("Please set the alarm to input your data");
            new TimePickerDialog(DataEntryFragment.this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    c.set(Calendar.HOUR, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND,0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 120 * 1000, pi);
                    Toast.makeText(DataEntryFragment.this.getActivity(), "time setting: "+hourOfDay+" : "+minute, Toast.LENGTH_SHORT).show();
                    //Store time setting in sharedPreference
                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("Alarm", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sharedPref.edit();
                    //also to store the date for alarm
                    Date date= new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    spEditor.putInt("hour", hourOfDay);
                    spEditor.putInt("min", minute - 2);
                    spEditor.putString("date", df.format(date));
                    spEditor.putString("user", email);
                    spEditor.apply();
                    entryBinding.saveButton.setEnabled(true);
                    entryBinding.editButton.setEnabled(true);
                    entryBinding.messageText.setText("");
                }
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();}
        //alarm set for today
        else {
            entryBinding.editButton.setEnabled(true);
            entryBinding.saveButton.setEnabled(true);
            //alarm set but not reach the time for alarm, inputs block
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            if (c.get(Calendar.HOUR_OF_DAY) < hour
                    || (c.get(Calendar.HOUR_OF_DAY) == hour && c.get(Calendar.MINUTE) < min)){
                Toast.makeText(DataEntryFragment.this.getActivity(),
                        "Input block until " + hour + ":" + min, Toast.LENGTH_SHORT).show();
            }
            //alarm set and reach the time for alarm, inputs unblock
            else {
                //inputEnables(true);
                //loading current day record
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                    CompletableFuture<PainRecord> painRecordCompletableFuture =
                            painViewModel.findByDateEmailFuture(email, entryDate);
                    painRecordCompletableFuture.thenApply(painRecord -> {
                        //already has record vs not has record
                        if (painRecord != null){
                            entryBinding.seekBar.setProgress(painRecord.painIntensityLevel);
                            for (int i = 0 ; i < entryBinding.spinner.getCount(); i++){
                                if (painRecord.painLocation.equals(entryBinding.spinner.getItemIdAtPosition(i))){
                                    entryBinding.spinner.setSelection(i,true);
                                    break;
                                }
                            }
                            switch (painRecord.painModeLevel){
                                case "Very good":
                                    entryBinding.veryGoodButton.setImageDrawable(getResources().getDrawable(R.drawable.very_good_clicked));
                                    break;
                                case "Good":
                                    entryBinding.goodButton.setImageDrawable(getResources().getDrawable(R.drawable.good_clicked));

                                    break;
                                case "Average":
                                    entryBinding.averageButton.setImageDrawable(getResources().getDrawable(R.drawable.average_clicked));
                                    break;
                                case "Low":
                                    entryBinding.lowButton.setImageDrawable(getResources().getDrawable(R.drawable.low_clicked));
                                    break;
                                case "Very low":
                                    entryBinding.veryLowButton.setImageDrawable(getResources().getDrawable(R.drawable.very_low_clicked));
                                    break;
                            }
                            painMood = painRecord.painModeLevel;
                            entryBinding.goalSetting.setText(Integer.toString(painRecord.goalSetting));
                            entryBinding.stepsEntering.setText(Integer.toString(painRecord.stepsTaken));
                        }else{
                            inputEnables(true);
                        }
                        return painRecord;
                    });
                }
            }
        }

        entryBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                painLevel = progress;
                entryBinding.painLevelMessage.setText("current pain level: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        entryBinding.veryGoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsUnClicked();
                entryBinding.veryGoodButton.setImageDrawable(getResources().getDrawable(R.drawable.very_good_clicked));
                painMood = "Very good";
            }
        });
        entryBinding.goodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsUnClicked();
                entryBinding.goodButton.setImageDrawable(getResources().getDrawable(R.drawable.good_clicked));
                painMood = "Good";
            }
        });
        entryBinding.averageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsUnClicked();
                entryBinding.averageButton.setImageDrawable(getResources().getDrawable(R.drawable.average_clicked));
                painMood = "Average";
            }
        });
        entryBinding.lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsUnClicked();
                entryBinding.lowButton.setImageDrawable(getResources().getDrawable(R.drawable.low_clicked));
                painMood = "Low";
            }
        });
        entryBinding.veryLowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsUnClicked();
                entryBinding.veryLowButton.setImageDrawable(getResources().getDrawable(R.drawable.very_low_clicked));
                painMood = "Very low";
            }
        });

        entryBinding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                int hour = sharedPref.getInt("hour", -1);
                int min = sharedPref.getInt("min", -1);
                if (c.get(Calendar.HOUR_OF_DAY) < hour
                        || (c.get(Calendar.HOUR_OF_DAY) == hour && c.get(Calendar.MINUTE) < min)) {
                    Toast.makeText(DataEntryFragment.this.getActivity(),
                            "Input block until " + hour + ":" + min, Toast.LENGTH_SHORT).show();
                } else {
                    if (!entryBinding.goalSetting.getText().toString().equals("")) {
                        goal = Integer.parseInt(entryBinding.goalSetting.getText().toString());
                    }
                    if (!entryBinding.stepsEntering.getText().toString().equals("")) {
                        steps = Integer.parseInt(entryBinding.stepsEntering.getText().toString());
                    }
                    if (!entryBinding.spinner.getSelectedItem().toString().equals("")) {
                        painLocation = entryBinding.spinner.getSelectedItem().toString();
                    }

                    if (painLevel != -1 && !painLocation.equals("") &&
                            !painMood.equals("") && goal != -1 && steps != -1) {
                        inputEnables(false);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            CompletableFuture<PainRecord> painRecordCompletableFuture =
                                    painViewModel.findByDateEmailFuture(email, entryDate);
                            painRecordCompletableFuture.thenApply(painRecord -> {
                                //already has record vs not has record
                                if (painRecord != null) {
                                    painRecord.painModeLevel = painMood;
                                    painRecord.painLocation = painLocation;
                                    painRecord.painIntensityLevel = painLevel;
                                    painRecord.goalSetting = goal;
                                    painRecord.stepsTaken = steps;
                                    painRecord.pressure = model.pressure;
                                    painRecord.humidity = model.humidiry;
                                    painRecord.temperature = model.temp;
                                    painViewModel.update(painRecord);
                                } else {
                                    PainRecord pRecord = new PainRecord(email, painLevel, painLocation,
                                            painMood, steps, goal, entryDate, model.temp, model.humidiry,
                                            model.pressure);
                                    painViewModel.insert(pRecord);
                                }
                                return painRecord;
                            });
                        }
                        Toast.makeText(DataEntryFragment.this.getActivity(), "Save succeed", Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuilder sb = new StringBuilder(7);
                        sb.append("Save failed\n");
                        if (painLevel == -1) {
                            sb.append("Pain level is missing\n");
                        }
                        if (painLocation.equals("")) {
                            sb.append("Pain location is missing\n");
                        }
                        if (painMood.equals("")) {
                            sb.append("Pain mood is missing\n");
                        }
                        if (goal == -1) {
                            sb.append("Goal is missing\n");
                        }
                        if (steps == -1) {
                            sb.append("Steps token is missing\n");
                        }
                        sb.append("Please enter information that is missing");
                        Toast.makeText(DataEntryFragment.this.getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        entryBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                int hour = sharedPref.getInt("hour", -1);
                int min = sharedPref.getInt("min", -1);
                if (c.get(Calendar.HOUR_OF_DAY) < hour
                        || (c.get(Calendar.HOUR_OF_DAY) == hour && c.get(Calendar.MINUTE) < min)){
                    Toast.makeText(DataEntryFragment.this.getActivity(),
                            "Input block until " + hour + ":" + min, Toast.LENGTH_SHORT).show();
                }
                else {
                    inputEnables(true);
                }
            }
        });

        return view;
    }

    public void inputEnables(boolean enable){
        entryBinding.seekBar.setEnabled(enable);
        entryBinding.spinner.setEnabled(enable);
        entryBinding.veryGoodButton.setEnabled(enable);
        entryBinding.goodButton.setEnabled(enable);
        entryBinding.averageButton.setEnabled(enable);
        entryBinding.lowButton.setEnabled(enable);
        entryBinding.veryLowButton.setEnabled(enable);
        entryBinding.goalSetting.setEnabled(enable);
        entryBinding.stepsEntering.setEnabled(enable);
    }

    public void buttonsUnClicked(){
        entryBinding.veryGoodButton.setImageDrawable(getResources().getDrawable(R.drawable.very_good));
        entryBinding.goodButton.setImageDrawable(getResources().getDrawable(R.drawable.good));
        entryBinding.averageButton.setImageDrawable(getResources().getDrawable(R.drawable.average));
        entryBinding.lowButton.setImageDrawable(getResources().getDrawable(R.drawable.low));
        entryBinding.veryLowButton.setImageDrawable(getResources().getDrawable(R.drawable.very_low));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        entryBinding = null;
    }
}
