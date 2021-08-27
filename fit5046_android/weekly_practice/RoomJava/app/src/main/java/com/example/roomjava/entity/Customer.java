package com.example.roomjava.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Customer {
    @PrimaryKey (autoGenerate = true)
    public int uid;

    @ColumnInfo (name = "first_name")
    @NonNull
    public String firstName;

    @ColumnInfo (name = "last_name")
    @NonNull
    public String lastName;

    public double salary;

    public Customer(@NonNull String firstName, @NonNull String lastName, double salary){
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }
}
