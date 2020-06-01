package com.example.progetto_embedded;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private HistoryRepository mRepository;
    private LiveData<List<History>> mAllTexts;
    private LiveData<List<History>> mLatTexts;

    public HistoryViewModel(Application application)
    {
        super(application);
        mRepository = new HistoryRepository(application);
        mAllTexts=mRepository.getAllTexts();
        mLatTexts=mRepository.getLatestTexts();
    }
    LiveData<List<History>> getAllTexts() {return mAllTexts;}

    void deleteList(List<Integer> list){
        for(int i:list){
            mRepository.delete(mRepository.getHistory(i));
        }
    }

    LiveData<List<History>> getLatestTexts() {return mLatTexts;}

    void insert(History text){mRepository.insert(text);}
}