package com.corgilab.corgiOCR;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    final String TAG = "main_act";
    final String HOME_TAG = "home_tag";
    private boolean checked;
    private int prev_frag = 0;
    private DrawerLayout navDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checked = sharedPreferences.getBoolean("DarkThemeOn", true);
        if(checked){
            setTheme(R.style.AppThemeDark);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.activity_main);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*  Gestisco 2 casi: sto avviando la app oppure sto tornando indietro da
        *   Camera_Gallery_activity
        */

        Intent prev = getIntent();
        String i = prev.getStringExtra("message");
        int j = prev.getIntExtra("settingsChanged",0);
        if(i!=null) {
            //Caso Camera_Gallery_activity
            Fragment t2s = new T2SFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("hb_visible", true);
            bundle.putString("text", i);
            bundle.putString("imgPath", getIntent().getStringExtra("imgPath"));
            t2s.setArguments(bundle);
            FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
            manager.setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right);
            manager.add(R.id.fragment_container, t2s).commit();
        }
        else if(j!=0)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).commit();
        else
            //Caso avvio la app
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new HomeFragment()).commit();

        //Setup Navigation Drawer
        navDrawer = findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,navDrawer,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.syncState();
        navDrawer.addDrawerListener(toggle);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }

    /*
    *   Se il drawer è aperto e se clicco back lo chiude
    */
    @Override
    public void onBackPressed(){
        if(navDrawer.isDrawerOpen(GravityCompat.START)){
            navDrawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    /*
     *   Vedo se la app ha i permessi di Archiviazione e, in caso positivo, avvio Camera_Gallery_Activity
     *   con una variabile ausiliaria negli extra (mi serve per uno switch case: "activity": 1 serve per marcare
     *   l'azione di Cattura nell'altra activity)
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
            i.putExtra("activity",(int)0);
            startActivityForResult(i, 0);
        }
    }

    /*
    *   Vedo se la app ha i permessi di Archiviazione e, in caso positivo, avvio Camera_Gallery_Activity
    *   con una variabile ausiliaria negli extra (mi serve per uno switch case: "activity": 0 serve per marcare
    *   l'azione di Pick_Image nell'altra activity)
    */
    public void gallery(View view)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            //Avvio l'activity dedicata alla galleria
            Intent i = new Intent(this, Camera_Gallery_activity.class);
            i.putExtra("activity",(int)1);
            startActivityForResult(i, 0);
        }
    }

    /*
    * Listener per gestire le transizioni dei fragment
    * quando seleziono un elemento nel menu laterale
    */
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(!item.isChecked());
        FragmentTransaction manager =  getSupportFragmentManager().beginTransaction();

        int id = item.getItemId();
        if(id == R.id.home){
            manager.replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        else if (id == R.id.hist) {
            manager.replace(R.id.fragment_container, new HistoryFragment()).commit();

        } else if (id == R.id.lang) {
            manager.replace(R.id.fragment_container, new LanguageFragment()).commit();
        } else if (id == R.id.settings) {
                manager.replace(R.id.fragment_container, new SettingsFragment()).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Check permission
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

                    i.putExtra("activity",0);
                    startActivityForResult(i,0);
                } else {
                    //Permission denied
                    Toast myToast = Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
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
                        i.putExtra("activity",1);
                        startActivityForResult(i,0);
                    } else {
                        //Permission denied
                        Toast myToast = Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
                        myToast.show();
                    }
                }
            break;
        }
    }


}