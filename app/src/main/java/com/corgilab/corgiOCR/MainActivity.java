package com.corgilab.corgiOCR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    //tag per  i permessi
    final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    //Variabili globali
    private List<String> languageAvailable = new ArrayList<String>();
    private List<String> languageAvailableTag = new ArrayList<String>();
    private Locale[] t2s_locales = Locale.getAvailableLocales();
    private TextToSpeech t2s;
    private boolean checked;
    private DrawerLayout navDrawer;

    //Asynctask per recuperare delle informazioni utili a LanguageFragment
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
    }

    /** Metodo per accedere all'array LanguageAvaible
     *
     * @return List<String> contenente l'array
     */
    public List<String> getLanguageAvailable() {
        return languageAvailable;
    }

    /** Metodo per accedere all'array LanguageAvaibleTag
     *
     * @return List<String> contenente l'array
     */
    public List<String> getLanguageAvailableTag() {
        return languageAvailableTag;
    }

    /**Metodo onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checked = sharedPreferences.getBoolean("DarkThemeOn", true);
        if (checked) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_main);

        //Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Setup Navigation Drawer
        navDrawer = findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, navDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.syncState();
        navDrawer.addDrawerListener(toggle);
        NavigationView mNavigationView = findViewById(R.id.navigation);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        //Vedo se è stato richiamato il metodo onsavedinstance prima della creazione dell'istanza
        if (savedInstanceState != null) {
            if(savedInstanceState.getBoolean("checked"))
                getSupportFragmentManager().popBackStack();
            else{
                Fragment t2s = new T2SFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("hb_visible", true);
                bundle.putString("text", savedInstanceState.getString("text"));
                bundle.putString("imgPath", savedInstanceState.getString("imgPath"));
                t2s.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(t2s,"T2SFragment").commit();
            }
            //Gestisco il caso di avvio della app oppure di ritorno da camera_gallery_activity
            //(anche il caso di refresh dovuto al cambio del tema nei settings)
        } else {
            Intent prev = getIntent();
            String i = prev.getStringExtra("message");
            int j = prev.getIntExtra("settingsChanged", 0);
            if (i != null) {
                //Caso Camera_Gallery_activity
                Fragment t2s = new T2SFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("hb_visible", true);
                bundle.putString("text", i);
                bundle.putString("imgPath", getIntent().getStringExtra("imgPath"));
                t2s.setArguments(bundle);
                FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
                manager.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right);
                manager.add(R.id.fragment_container, t2s, "T2SFragment").commit();
            } else if (j != 0)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment(), "SettingsFragment").commit();
            else {
                //Caso avvio la app
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeFragment(), "HomeFragment").commit();
                Camera_Gallery_activity.deleteTempFiles(Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
            }

        }
        /*inizializzo il motore TextToSpeech per trovare le lingue disponibili
        Lo faccio in main activity ed in background per non farlo in fragment language
        in quanto il caricamento è abbastanza oneroso e l'app laggherebe*/
        t2s = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // * - delay important tasks until TTS is initialized
                    new LanguageRetrievingTask().execute("T2Slang_retrieve");
                }
            }
        });
        new LanguageRetrievingTask().execute();
    }
    /**
    *   Se il drawer è aperto e se clicco back lo chiude
    */
    @Override
    public void onBackPressed(){
        if(navDrawer.isDrawerOpen(GravityCompat.START)){
            navDrawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    /**
     *   Vedo se la app ha i permessi di Archiviazione e, in caso positivo, avvio Camera_Gallery_Activity
     *   con una variabile ausiliaria negli extra (mi serve per uno switch case: "activity": 1 serve per marcare
     *   l'azione di Cattura nell'altra activity)
     *   @Param view la view che contiene il bottone
     */
    public void camera(View view)
    {
        //Controllo di avere i permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
        }else {
            //Avvio l'activity dedicata alla camera
            Intent i = new Intent(this, Camera_Gallery_activity.class);
            i.putExtra("Theme",checked);
            i.putExtra("activity", 0);
            startActivityForResult(i, 0);
        }
    }

    /**
    *   Vedo se la app ha i permessi di Archiviazione e, in caso positivo, avvio Camera_Gallery_Activity
    *   con una variabile ausiliaria negli extra (mi serve per uno switch case: "activity": 0 serve per marcare
    *   l'azione di Pick_Image nell'altra activity)
    *   @param view la view chr contiene il bottone
    */
    public void gallery(View view)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            //Avvio l'activity dedicata alla galleria
            Intent i = new Intent(this, Camera_Gallery_activity.class);
            i.putExtra("Theme",checked);
            i.putExtra("activity", 1);
            startActivityForResult(i, 0);
        }
    }

    /**
    *   Listener per gestire la navigazione tra fragment attraverso
    *   il menu laterale
    *   @param item il riferimento al menu laterale
    *
    *   @return in questo caso always true, non implemento funzionalità che mi richiedano
    *   un valore specifico di ritorno
    */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(!item.isChecked());
        FragmentTransaction manager =  getSupportFragmentManager().beginTransaction();

        int id = item.getItemId();
        if(id == R.id.home){
            manager.replace(R.id.fragment_container, new HomeFragment(),"HomeFragment").commit();
        }
        else if (id == R.id.hist) {
            manager.replace(R.id.fragment_container, new HistoryFragment(),"HistoryFragment").commit();

        } else if (id == R.id.lang) {
            manager.replace(R.id.fragment_container, new LanguageFragment(),"LanguageFragment").commit();
        } else if (id == R.id.settings) {
                manager.replace(R.id.fragment_container, new SettingsFragment(),"SettingsFragment").commit();

        }
        DrawerLayout drawer = findViewById(R.id.nav_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Metodo per la gestione dopo la richiesta dei permessi di accesso a camera o gallery
     *
     * @param requestCode codice della richiesta
     * @param permissions array con il tipo di permessi richiesti
     * @param grantResults risultato delle richieste
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //Permessi per l'uso della fotocamera
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                Intent i = new Intent(this,Camera_Gallery_activity.class);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted, avvio l'activity della camera
                    i.putExtra("Theme",checked);
                    i.putExtra("activity",0);
                    startActivityForResult(i,0);
                } else {
                    //Permission denied
                    Toast myToast = Toast.makeText(this, getResources().getString(R.string.permission_d), Toast.LENGTH_SHORT);
                    myToast.show();
                }
            }
            break;

            //richiesta dei permessi per l'accesso ai file del dispositivo
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                    // If request is cancelled, the result arrays are empty.
                    Intent i = new Intent(this,Camera_Gallery_activity.class);
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Permission granted, avvio l'activity della camera
                        i.putExtra("Theme",checked);
                        i.putExtra("activity", 1);
                        startActivityForResult(i,0);
                    } else {
                        //Permission denied
                        Toast myToast = Toast.makeText(this, getResources().getString(R.string.permission_d), Toast.LENGTH_SHORT);
                        myToast.show();
                    }
                }
            break;
        }
    }

    /** Metodo per la gestione del salvataggio dell'istanza in caso di rotazione schermo o altro
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "fragmentName", Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragment_container)));
        outState.putBoolean("changed",true);
    }

    /**
     * Override del metodo onStop per spegnere il motore t2s quando viene invocato
     */
    @Override
    protected void onStop() {
        super.onStop();
        t2s.shutdown();
    }
}
