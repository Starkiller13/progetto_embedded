package com.corgilab.corgiOCR.HistoryManagement;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryRepository {
    private HistoryDao mHistoryDao;
    private LiveData<List<History>> mAllTexts;
    private LiveData<List<History>> mLatTexts;

    HistoryRepository(Application application) {
        HistoryRoomDatabase db = HistoryRoomDatabase.getDatabase(application);
        mHistoryDao = db.historyDao();
        mAllTexts = mHistoryDao.getOrderedTexts();
        mLatTexts = mHistoryDao.getLastOrderedTexts();
    }

    LiveData<List<History>> getAllTexts() {
        return mAllTexts;
    }

    LiveData<List<History>> getLatestTexts() {
        return mLatTexts;
    }

    History getHistory(int id){

        List<History> tmp = mAllTexts.getValue();
        History k = null;
        for(History j:tmp){
            if(j.getId()==id)
                k = j;
        }
        return k;
    }

    void insert(History text)
    {
        HistoryRoomDatabase.databaseWriteExecutor.execute(() -> {
            mHistoryDao.insert(text);});
    }

    void delete(History history){
        HistoryRoomDatabase.databaseWriteExecutor.execute(() ->{
            mHistoryDao.delete(history);
        });
    }
}