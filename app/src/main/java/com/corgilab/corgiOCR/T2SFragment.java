package com.corgilab.corgiOCR;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.corgilab.corgiOCR.HistoryManagement.History;
import com.corgilab.corgiOCR.HistoryManagement.HistoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Locale;
import java.util.Objects;

/**
 * Il fragment crea l'interfaccia in cui viene visualizzato il testo ottenuto
 * dalle API di vision e gestisce la modifica del testoo e la riproduzione dello
 * stesso con un motore di sintesi vocale
 */
public class T2SFragment extends Fragment {
    private View view;
    private TextToSpeech t2s;
    private EditText tw;
    private boolean fab_status = true;
    private boolean isRotating = false;
    private int j = 0;
    private FloatingActionButton fab;
    private HistoryViewModel mHistoryViewModel;

    /** Metodo per la creazione della view del fragment corrente. Il fragment
     * gestisce 4 bottoni (3 Buttons e un FAB).
     *
     * Parametri di default
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        view = inflater.inflate(R.layout.fragment_t2s, container, false);
        fab = view.findViewById(R.id.t2s_fab);
        t2s = ttsinitializer();
        tw = (EditText) view.findViewById(R.id.textbox);
        assert getArguments() != null;
        String txt = null;
        String imgPath =null;
        if(savedInstanceState==null) {
            txt = getArguments().getString("text");
            imgPath = getArguments().getString("imgPath");
        }
        else {
            txt = savedInstanceState.getString("text");
            imgPath = savedInstanceState.getString("imgPath");
        }
        ImageView imgview = view.findViewById(R.id.t2s_imageView);
        /*
        * Se arrivo alla app da Camera_Gallery_activity passo anche la preview dell'immagine(che verrà poi eliminata)
        * altrimenti metto una immagine di default
        */
        if(imgPath !=null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            imgview.setImageBitmap(bitmap);
        }else{
            imgview.setImageResource(R.drawable.t2s_header_img);
        }
        boolean hb_visible = getArguments().getBoolean("hb_visible");
        Log.v("hb_visible:", "" + hb_visible);
        tw.setText(txt);
        tw.setMovementMethod(new ScrollingMovementMethod());
        // Pulsante per la gestione del motore di sintesi vocale
        fab.setOnClickListener(v -> {
            if(fab_status){
                t2s.speak(tw.getText().toString(), TextToSpeech.QUEUE_FLUSH,null, "1");
                fab.setImageResource(R.drawable.ic_stop_black_24dp);
                fab_status = false;
            }else{
                t2s.stop();
                fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                fab_status=true;

            }

        });
        /*Bottone per l'aggiunta del testo nell'EditText alla history
        * Il bottone è nascosto se l'elemento è già presente nella history
        */
        Button add = (Button) view.findViewById(R.id.button_add_history);
        if(!hb_visible){
            add.setVisibility(View.INVISIBLE);
        }else{
            add.setVisibility(View.VISIBLE);
        }
        add.setOnClickListener(v -> {
            if (j == 0) {
                mHistoryViewModel.insert(new History(tw.getText().toString()));
                Toast.makeText(getContext(), "Added to History", Toast.LENGTH_SHORT).show();
                j++;
            } else
                Toast.makeText(getContext(), "Already added", Toast.LENGTH_SHORT).show();
        });
        /* Bottone per tornare alla schermata precedente(viene tolto un fragment dallo stack se è presente
        * oppure se non ci sono fragment nel backStack creo un nuovo HomeFragment e ci vado
        */
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
        /* Bottone per condividere il testo ottenuto tramite altre app.
        * Viene gestito da un Intent di tipo ACTION_SEND
        */
        Button share = (Button) view.findViewById(R.id.share);
        share.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "--CorgiOCR GO--\n"
                    + tw.getText().toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
        return view;
    }

    /** Per una questione di ordine nel codice ho un metodo in cui
     * inizializzo il motore di sintesi vocale
     * @return l'oggetto TextToSpeech inizializzato
     */
    private TextToSpeech ttsinitializer(){
        return new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                //if there isn't any error, set the language
                t2s.setLanguage(new Locale(requireActivity()
                        .getPreferences(Context.MODE_PRIVATE)
                        .getString("TextToSpeechLanguage","it")));
                t2s.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if(utteranceId.equals("1"))
                            view.getHandler().post(new Runnable() {
                                @Override
                                public final void run() {
                                    FloatingActionButton x = view.findViewById(R.id.t2s_fab);
                                    x.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                    fab_status=true;
                                }
                            });
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("changed",false);
        outState.putString("txt",getArguments().getString("text"));
        outState.putString("imgPath",getArguments().getString("imgPath"));
        isRotating=true;
    }

    /**Implementazione del metodo onPause in cui interrompo e spengo
     * il motore di sintesi.
     */
    public void onPause(){
        if(t2s !=null){
            t2s.stop();
            t2s.shutdown();
        }
        super.onPause();
    }

    /**Implementazione del metodo onDetach in cui interrompo e spengo
     * il motore di sintesi. In questo caso elimino anche il file temporaneo
     * in cui avevo salvato na foto della preview
     */
    @Override
    public void onDetach() {
        if(t2s !=null){
            t2s.stop();
            t2s.shutdown();
        }
        super.onDetach();
        if(!isRotating)
            Camera_Gallery_activity.deleteTempFiles(Objects.requireNonNull(requireContext()
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
    }
}

