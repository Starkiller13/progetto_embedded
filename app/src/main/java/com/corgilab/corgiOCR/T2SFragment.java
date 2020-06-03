package com.corgilab.corgiOCR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
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

import com.corgilab.corgiOCR.HistoryManagement.History;
import com.corgilab.corgiOCR.HistoryManagement.HistoryViewModel;
import com.corgilab.corgiOCR.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class T2SFragment extends Fragment {
    private View view;
    private TextToSpeech t2s;
    private TextView tw;
    private boolean fab_status = true;
    private int j = 0;
    private HistoryViewModel mHistoryViewModel;
    private String txt = null;
    private String imgPath = null;
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
                t2s.setLanguage(new Locale(requireActivity().getPreferences(Context.MODE_PRIVATE).getString("TextToSpeechLanguage","it")));
            }
        });
        tw = (TextView) view.findViewById(R.id.textbox);
        assert getArguments() != null;
        txt = getArguments().getString("text");
        imgPath = getArguments().getString("imgPath");
        ImageView imgview = view.findViewById(R.id.t2s_imageView);
        if(imgPath!=null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            imgview.setImageBitmap(bitmap);
        }else{
            imgview.setImageResource(R.drawable.t2s_header_img);
        }
        boolean hb_visible = getArguments().getBoolean("hb_visible");
        Log.v("hb_visible:", "" + hb_visible);
        tw.setText(txt);
        tw.setMovementMethod(new ScrollingMovementMethod());
        // Inflate the layout for this fragment
        FloatingActionButton fab = view.findViewById(R.id.t2s_fab);
        fab.setOnClickListener(v -> {
            if(fab_status){
                t2s.speak(tw.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                fab.setImageResource(R.drawable.ic_stop_black_24dp);
                fab_status = false;
            }else{
                t2s.stop();
                fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                fab_status=true;
            }
        });

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
        Camera_Gallery_activity.deleteTempFiles(Objects.requireNonNull(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
    }
}

