package com.example.paindiaryapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.paindiaryapplication.entity.PainRecord;

import java.util.List;
//query to do
//select by email return list -show in daily record
//select by email and day -data entry screen
//insert -data entry, click save
//update -data edit, click edit and save
@Dao
public interface PainDAO {
    @Query("SELECT * FROM painrecord WHERE user_email = :userEmail ORDER BY entry_date DESC")
    LiveData<List<PainRecord>> getAllByEmail(String userEmail);

    @Query("SELECT * FROM painrecord WHERE user_email = :userEmail and entry_date = :date LIMIT 1")
    PainRecord getRecordByDateEmail(String userEmail, String date);

    @Query("SELECT pain_location || ' ' || CAST(COUNT(*) as TEXT) as location_count FROM painrecord " +
            "WHERE user_email = :userEmail GROUP BY pain_location ORDER BY COUNT(*)")
    LiveData<List<String>> getLocationCount(String userEmail);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Update
    void updatePainRecord(PainRecord painRecord);
}
