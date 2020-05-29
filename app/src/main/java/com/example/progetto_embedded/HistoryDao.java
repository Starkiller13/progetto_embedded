package com.example.progetto_embedded;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface HistoryDao{

    // allowing the insert of the same word multiple times by passing a 
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(History text);

    @Query("DELETE FROM history_table")
    void deleteAll();

    @Query("SELECT * FROM history_table ORDER BY id DESC")
    LiveData<List<History>> getOrderedTexts();
}