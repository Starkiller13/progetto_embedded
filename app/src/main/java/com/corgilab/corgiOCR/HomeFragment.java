package com.corgilab.corgiOCR;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corgilab.corgiOCR.HistoryManagement.History;
import com.corgilab.corgiOCR.HistoryManagement.HistoryListAdapter;
import com.corgilab.corgiOCR.HistoryManagement.HistoryViewModel;
import com.corgilab.corgiOCR.R;

import java.util.List;

public class HomeFragment extends Fragment{
    private static final String TAG = "HomeFragment";
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
        adapter.setOnItemClickListener(new HistoryListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position){
                Log.v(TAG,"clicked");
                String txt = adapter.getText(position);
                Bundle bundle = new Bundle();
                bundle.putString("text", txt);
                T2SFragment frag = new T2SFragment();
                frag.setArguments(bundle);
                FragmentTransaction manager =  getParentFragmentManager().beginTransaction();
                manager.setCustomAnimations(R.anim.enter_right,R.anim.exit_left,R.anim.enter_left,R.anim.exit_right);
                manager.replace(R.id.fragment_container,frag).addToBackStack(null).commit();
            }

        });


        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        if(mHistoryViewModel!=null)
            mHistoryViewModel.getLatestTexts().observe(getViewLifecycleOwner(), new Observer<List<History>>() {

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
