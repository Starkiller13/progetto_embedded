/*
PER GESTIRE PAUSE POSSO USARE UN FILE ESTERNO
private String envPath = Environment.getExternalStorageDirectory()
        .getAbsolutePath() + "/Download";
private Uri fileUri;

public void fileCreate() {
    String inputText = output.getText().toString();

    HashMap<String, String> myHashRender = new HashMap<String, String>();
    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, inputText);
    Log.d(TAG, "successfully created hashmap");

    String destFileName = envPath + "/" + "tts_file.wav";

    int sr = tts.synthesizeToFile(inputText, myHashRender, destFileName);
    Log.d(TAG, "synthesize returns = " + sr);
    File fileTTS = new File(destFileName);

    if (fileTTS.exists()) {
        Log.d(TAG, "successfully created fileTTS");
    }
    else {
        Log.d(TAG, "failed while creating fileTTS");
    }

    fileUri = Uri.fromFile(fileTTS);
    Log.d(TAG, "successfully created uri link: " + fileUri.getPath());
}

fileCreate();
    mp = MediaPlayer.create(this, fileUri);
    Log.d(TAG, "successfuly created mediaplayer");

    btnRead.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (mp.isPlaying()) {
                mp.pause();
                Log.d(TAG, "successfully paused");
            } else {
                mp.start();
                Log.d(TAG, "successfully started");
            }
        }

    });
 */
package com.example.progetto_embedded;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Text2Speech extends AppCompatActivity {
    //prende la stringa passata tramite intent
    final static String TAG = "Text2SpeechActivity";
    TextToSpeech t2s;
    TextView tw;
    Spinner lang_spinner;
    Locale[] locales = Locale.getAvailableLocales();
    ArrayAdapter<String> aa;
    List<Locale> localeList = new ArrayList<Locale>();
    List<String> country = new ArrayList<String>();

    //private Uri textUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text2_speech);
        String txt = getIntent().getStringExtra("message");
        tw = findViewById(R.id.textbox);
        tw.setText(txt);

        //Eventuale EditText
        //EditText ed = findViewById(R.id.textbox);
        //ed.append(txt);


        t2s = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //if there isn't any error, set the language
                    t2s.setLanguage(Locale.ITALIAN);
                }
            }
        });


        lang_spinner = (Spinner) findViewById(R.id.languages_spinner);
        for (Locale locale : locales) {
                localeList.add(locale);
                // * - 'country' is used to populate the adapter, so
                // this line must come first
                country.add(locale.getDisplayName());
        }
        Log.v("WXYZ",country.toString());
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(aa);
    }

    public void play(View view)
    {
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

    public void stop(View view)
    {
        if(t2s.isSpeaking()) {
            //if it's speaking, then stop it
            t2s.stop();
            t2s.shutdown();
        }
        else {
            Toast.makeText(Text2Speech.this, "Not speaking", Toast.LENGTH_SHORT).show();
        }
    }

    public void addHistory(View view)
    {
        //DA FARE
        //SE IL SALVATAGGIO VA A BUON FINE FAR VENIRE FUORI UN TOAST
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
