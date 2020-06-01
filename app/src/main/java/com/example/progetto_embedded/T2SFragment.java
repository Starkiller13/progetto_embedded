package com.example.progetto_embedded;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.speech.tts.TextToSpeech;
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
    private boolean hb_visible = true;
    private int j = 0;
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
        hb_visible = getArguments().getBoolean("hb_visible");
        Log.v("hb_visible:", "" + hb_visible);
        Button b = view.findViewById(R.id.button_add_history);
        if(!hb_visible){
            b.setVisibility(View.INVISIBLE);
        }else{
            b.setVisibility(View.VISIBLE);
        }
        tw.setText(txt);
        tw.setMovementMethod(new ScrollingMovementMethod());
        // Inflate the layout for this fragment
        ImageView play = (ImageView) view.findViewById(R.id.image_play);
        play.setOnClickListener(v -> {
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

        });
        ImageView stop = (ImageView) view.findViewById(R.id.image_stop);
        stop.setOnClickListener(v -> {
            Log.v("STOP","pulsante stop cliccato");
            if(t2s.isSpeaking()){
                //if it's speaking, then stop it
                t2s.stop();
            }
            else {
                Toast.makeText(getContext(), "Not speaking", Toast.LENGTH_SHORT).show();
            }
        });

        Button add = (Button) view.findViewById(R.id.button_add_history);
        add.setOnClickListener(v -> {
            if (j == 0) {
                mHistoryViewModel.insert(new History(tw.getText().toString()));
                Toast.makeText(getContext(), "Added to History", Toast.LENGTH_SHORT).show();
                j++;
            } else
                Toast.makeText(getContext(), "Already added", Toast.LENGTH_SHORT).show();
        });

        Button back = (Button) view.findViewById(R.id.button_back);
        back.setOnClickListener(v -> {
            if(getParentFragmentManager().getBackStackEntryCount()>0)
                getParentFragmentManager().popBackStack();
            else {
                FragmentTransaction manager = getParentFragmentManager().beginTransaction();
                manager.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
                manager.replace(R.id.fragment_container, new HomeFragment()).commit();
            }
        });

        Button share = (Button) view.findViewById(R.id.share);
        share.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "--CorgiOCR GO--\n" + txt);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
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

