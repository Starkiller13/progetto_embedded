package com.corgilab.corgiOCR.HistoryManagement;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

/**
 * History Repository
 */
public class HistoryRepository {
    private HistoryDao mHistoryDao;
    private LiveData<List<History>> mAllTexts;
    private LiveData<List<History>> mLatTexts;

    /** Costruttore
     *
     * @param application
     */
    HistoryRepository(Application application) {
        HistoryRoomDatabase db = HistoryRoomDatabase.getDatabase(application);
        mHistoryDao = db.historyDao();
        mAllTexts = mHistoryDao.getOrderedTexts();
        mLatTexts = mHistoryDao.getLastOrderedTexts();
    }

    /**
     * Metodo di accesso per il fetching di tutti gli elementi
     * @return
     */
    LiveData<List<History>> getAllTexts() {
        return mAllTexts;
    }

    /**
     * Metodo di accesso per il fetching degli ultimi 6 elementi
     * @return
     */
    LiveData<List<History>> getLatestTexts() {
        return mLatTexts;
    }

    /**
     * Metodo per recuperare l'istanza History partendo dall'id
     * @param id
     * @return
     */
    History getHistory(int id){
        List<History> tmp = mAllTexts.getValue();
        History k = null;
        for(History j:tmp){
            if(j.getId()==id)
                k = j;
        }
        return k;
    }

    /**
     * Metodo per l'inserimento di un'istanza History
     * @param text
     */
    void insert(History text)
    {
        HistoryRoomDatabase.databaseWriteExecutor.execute(() -> {
            mHistoryDao.insert(text);});
    }

    /**
     * Metodo per eliminare un'istanza History dal database
     * @param history
     */
    void delete(History history){
        HistoryRoomDatabase.databaseWriteExecutor.execute(() ->{
            mHistoryDao.delete(history);
        });
    }
}