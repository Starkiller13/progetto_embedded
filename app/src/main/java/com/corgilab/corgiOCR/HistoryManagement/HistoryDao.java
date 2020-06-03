package com.corgilab.corgiOCR.HistoryManagement;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao{

    // allowing the insert of the same word multiple times by passing a 
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(History tba);

    @Query("DELETE FROM history_table")
    void deleteAll();

    @Delete
    void delete(History tbd);

    @Query("SELECT * FROM history_table ORDER BY id DESC")
    LiveData<List<History>> getOrderedTexts();

    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT 6")
    LiveData<List<History>> getLastOrderedTexts();
}