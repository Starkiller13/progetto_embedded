package com.example.progetto_embedded;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Text2Speech extends AppCompatActivity {
    //prende la stringa passata tramite intent
    TextToSpeech t2s;
    TextView tw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text2_speech);

        Button b_play = findViewById(R.id.button_play);
        Button b_stop = findViewById(R.id.button_stop);

        String txt = getIntent().getStringExtra("message");

        tw = findViewById(R.id.textbox);
        tw.setText(txt);
        //EditText ed = findViewById(R.id.textbox);
        //ed.append(txt);

        t2s = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //if there isnt any error, set the language
                    t2s.setLanguage(Locale.UK);
                }
            }
        });

        //b_play press
        b_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the text from TextView
                String toSpeak = tw.getText().toString();
                //if there isnt text in textview
                if (toSpeak.equals("")) {
                    Toast.makeText(Text2Speech.this, "Please check the photo and try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    //speak text
                    t2s.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


        //b_stop press
        b_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t2s.isSpeaking()) {
                    //if it's speaking, then stop it
                    t2s.stop();
                    t2s.shutdown();
                }
                else {
                    Toast.makeText(Text2Speech.this, "Not speaking", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //controllare il metodo in modo da rendere solo pause e non uno stop
    public void onPause(){
        if(t2s !=null){
            t2s.stop();
            t2s.shutdown();
        }
        super.onPause();
    }
}
