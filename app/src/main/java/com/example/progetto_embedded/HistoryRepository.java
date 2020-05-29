package com.example.progetto_embedded;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class HistoryRepository {
    private HistoryDao mHistoryDao;
    private LiveData<List<History>> mAllTexts;

    HistoryRepository(Application application) {
        HistoryRoomDatabase db = HistoryRoomDatabase.getDatabase(application);
        mHistoryDao = db.historyDao();
        mAllTexts = mHistoryDao.getOrderedTexts();
    }

    LiveData<List<History>> getAllTexts() {
        return mAllTexts;
    }

    void insert(History text)
    {
        HistoryRoomDatabase.databaseWriteExecutor.execute(() -> {
            mHistoryDao.insert(text);});
    }
}