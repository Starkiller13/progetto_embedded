package com.corgilab.corgiOCR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageFragment extends Fragment {
    private Locale[] t2s_locales = Locale.getAvailableLocales();
    private List<String> languageAvailable = new ArrayList<String>();
    private List<String> languageAvailableTag = new ArrayList<String>();
    private TextToSpeech t2s;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_language, container, false);
        t2s = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // * - delay important tasks until TTS is initialized
                    new LanguageRetrievingTask().execute("T2Slang_retrieve");
                }
            }
        });

        return view;
    }
    /*
    *   Il task è pesante, conviene fare la fase di retrieving in background e quando ha finito mostrare i risultati nella Ui
    */
    @SuppressLint("StaticFieldLeak")
    private class LanguageRetrievingTask extends AsyncTask<String, Integer, Long> {
        @Override
        public Long doInBackground(String... strings) {
            for (Locale locale : t2s_locales) {
                int res = t2s.isLanguageAvailable(locale);
                if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    languageAvailable.add(locale.getDisplayLanguage());
                    languageAvailableTag.add(locale.toString());
                }
            }

        return 0L;
        }
        @Override
        protected void onPostExecute(Long result) {
            SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            RecyclerView recyclerView = view.findViewById(R.id.lang_recview);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(manager);
            int pos = sharedPreferences.getInt("SpinnerPosition",1 );
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
        }
    }
}
