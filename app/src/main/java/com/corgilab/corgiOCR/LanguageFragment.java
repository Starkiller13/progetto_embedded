package com.corgilab.corgiOCR;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.corgilab.corgiOCR.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

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
                    startWhenTTSIsInitialized();
                }
            }
    });

        return view;
    }

    private void startWhenTTSIsInitialized(){
        for (Locale locale : t2s_locales) {
            int res = t2s.isLanguageAvailable(locale);
            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE){
                languageAvailable.add(locale.getDisplayLanguage() +" "+ locale.toString());
                languageAvailableTag.add(locale.toString());
            }
        }
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, languageAvailable);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(requireActivity().getPreferences(Context.MODE_PRIVATE).getInt("SpinnerPosition",1));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TextToSpeechLanguage",languageAvailableTag.get(position));
                editor.putInt("SpinnerPosition",position);
                editor.apply();
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
