package com.example.progetto_embedded;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class T2SFragment extends Fragment {
    private View view;
    private TextToSpeech t2s;
    private TextView tw;
    private HistoryViewModel mHistoryViewModel;
    String txt = null;
    Spinner lang_spinner;
    Locale[] locales = Locale.getAvailableLocales();
    ArrayAdapter<String> aa;
    List<Locale> localeList = new ArrayList<Locale>();
    List<String> country = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        view = inflater.inflate(R.layout.fragment_t2s, container, false);
        t2s=new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                //if there isn't any error, set the language
                t2s.setLanguage(Locale.ITALIAN);
            }
        });
        tw = (TextView) view.findViewById(R.id.textbox);
        txt = getArguments().getString("text");
        tw.setText(txt);
        tw.setMovementMethod(new ScrollingMovementMethod());
        // Inflate the layout for this fragment
        ImageView play = (ImageView) view.findViewById(R.id.image_play);
        play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //get the text from TextView
                String toSpeak = tw.getText().toString();
                //if there isnt text in textview
                if (toSpeak.equals("")) {
                    Toast.makeText(getContext(), "Please check the photo and try again", Toast.LENGTH_SHORT).show();
                } else {
                    //speak text
                    t2s.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    return;
                }

            }
        });
        ImageView stop = (ImageView) view.findViewById(R.id.image_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("STOP","pulsante stop cliccato");
                if(t2s.isSpeaking()){
                    //if it's speaking, then stop it
                    t2s.stop();
                }
                else {
                    Toast.makeText(getContext(), "Not speaking", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button add = (Button) view.findViewById(R.id.button_add_history);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView t = v.findViewById(R.id.t2s_text);
                mHistoryViewModel.insert(new History(tw.getText().toString()));
            }
        });

        return view;
    }


    public void onPause(){
        if(t2s !=null){
            t2s.stop();
            t2s.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDetach() {
        if(t2s !=null){
            t2s.stop();
            t2s.shutdown();
        }
        super.onDetach();
    }
}

