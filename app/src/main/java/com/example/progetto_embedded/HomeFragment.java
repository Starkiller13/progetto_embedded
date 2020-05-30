package com.example.progetto_embedded;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class HomeFragment extends Fragment{

    private View view;
    private RecyclerView recyclerView;
    private HistoryViewModel mHistoryViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.home_rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final HistoryListAdapter adapter = new HistoryListAdapter(context);
        recyclerView.setAdapter(adapter);


        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        if(mHistoryViewModel!=null)
            mHistoryViewModel.getAllTexts().observe(getViewLifecycleOwner(), new Observer<List<History>>() {

                @Override
                public void onChanged(@Nullable final List<History> words) {
                    Log.v("Mannaggia",words.toString());
                    // Update the cached copy of the words in the adapter.
                    adapter.setWords(words);
                }
            });
        // Inflate the layout for this fragment
        return view;
    }
}
