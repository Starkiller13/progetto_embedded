package com.example.progetto_embedded;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private HistoryRepository mRepository;
    private LiveData<List<History>> mAllTexts;

    public HistoryViewModel(Application application)
    {
        super(application);
        mRepository = new HistoryRepository(application);
        mAllTexts=mRepository.getAllTexts();
    }
    LiveData<List<History>> getAllTexts() {return mAllTexts;}

    void insert(History text){mRepository.insert(text);}
}