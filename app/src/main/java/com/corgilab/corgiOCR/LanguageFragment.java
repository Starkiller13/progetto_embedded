package com.corgilab.corgiOCR;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe per la selezione della lingua del motore di sintesi vocale
 */
public class LanguageFragment extends Fragment {

    //Variabili
    private List<String> languageAvailable = new ArrayList<String>();
    private List<String> languageAvailableTag = new ArrayList<String>();
    private View view;
    MainActivity activity;

    /** Metodo onCreateView di default del fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        //Gestisco il caso in cui sia stato chiamato oSavedInstance
        if(savedInstanceState!=null){
            languageAvailable = savedInstanceState.getStringArrayList("lingue");
            languageAvailableTag = savedInstanceState.getStringArrayList("lingueTag");
        }
        else{
            assert activity != null;
            //Carico i dati elaborati da mainactivity
            languageAvailable = activity.getLanguageAvailable();
            languageAvailableTag = activity.getLanguageAvailableTag();
        }
        view = inflater.inflate(R.layout.fragment_language, container, false);
        //Recupero i valori delle shared preferences che mi interessano
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Setup della recyclerview
        RecyclerView recyclerView = view.findViewById(R.id.lang_recview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        int pos = sharedPreferences.getInt("SpinnerPosition", 1);
        final LanguageViewAdapter adapter = new LanguageViewAdapter(getContext());
        adapter.setLastPos(pos);
        manager.scrollToPosition(pos);
        adapter.setLanguages(languageAvailable);
        adapter.setLanguagesTag(languageAvailableTag);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new LanguageViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                editor.putString("TextToSpeechLanguage", languageAvailableTag.get(position));
                editor.putInt("SpinnerPosition", position);
                editor.apply();
                adapter.setLastPos(position);
                adapter.notifyDataSetChanged();
                manager.scrollToPosition(position);
            }
        });

        return view;
    }

    /**
     * Metodo onSavedInstanceState per il salvataggio delle liste in caso di rotazione dello schermo o altro
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("lingue",(ArrayList<String>)languageAvailable);
        outState.putStringArrayList("lingueTag",(ArrayList<String>)languageAvailableTag);
    }
}
