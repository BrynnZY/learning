package com.example.paindiaryapplication.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PainRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "user_email")
    @NonNull
    public String userEmail;
    @ColumnInfo(name = "pain_intenity_level")
    public int painIntensityLevel;
    @ColumnInfo(name = "pain_location")
    public String painLocation;
    @ColumnInfo(name = "pain_mood_level")
    public String painModeLevel;
    @ColumnInfo(name = "steps_taken")
    public int stepsTaken;
    @ColumnInfo(name = "goal_setting")
    public int goalSetting;
    @ColumnInfo(name = "entry_date")
    @NonNull
    public String entryDate;
    public double temperature;
    public int humidity;
    public int pressure;
    public PainRecord( @NonNull String userEmail, int painIntensityLevel, String painLocation,
                       String painModeLevel, int stepsTaken, int goalSetting,@NonNull String entryDate, double temperature,
                       int humidity, int pressure) {
        this.userEmail = userEmail;
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.painModeLevel = painModeLevel;
        this.stepsTaken = stepsTaken;
        this.goalSetting = goalSetting;
        this.entryDate = entryDate;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }
    public String getUserEmail(){return userEmail;}
    public int getPainIntensityLevel(){return painIntensityLevel;}
    public String getPainLocation(){return painLocation;}
    public  String getPainModeLevel(){return painModeLevel;}
    public String getEntryDate(){return entryDate;}
    public int getStepsTaken(){return stepsTaken;}
    public int getGoalSetting(){return goalSetting;}
    public double getTemperature(){return temperature;}
    public int getHumidity(){return humidity;}
    public int getPressure(){return pressure;}
    public int getUid(){return uid;}

}
