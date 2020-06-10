package com.corgilab.corgiOCR.HistoryManagement;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * Interfaccia del Dao
 */
@Dao
public interface HistoryDao{

    // allowing the insert of the same word multiple times by passing a 
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(History tba);

    //Non viene mai utilizzato
    @Query("DELETE FROM history_table")
    void deleteAll();

    //Metodo per eliminare un'istanza
    @Delete
    void delete(History tbd);

    //Restituisce tutte le istanze ordinate per id decrescente
    @Query("SELECT * FROM history_table ORDER BY id DESC")
    LiveData<List<History>> getOrderedTexts();

    //Testituisce le ultime 6 istanze orinate per id derescente
    @Query("SELECT * FROM history_table ORDER BY id DESC LIMIT 6")
    LiveData<List<History>> getLastOrderedTexts();
}