package com.example.progetto_embedded;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    final String TAG = "main_act";
    final String HOME_TAG = "home_tag";
    private DrawerLayout navDrawer;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup Navigation Drawer
        navDrawer = findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,navDrawer,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        toggle.syncState();
        navDrawer.addDrawerListener(toggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        /*  Gestisco 2 casi: sto avviando la app oppure sto tornando indietro da
        *   Camera_Gallery_activity
        */
        String i = getIntent().getStringExtra("message");
        if(i!=null){
            //Caso Camera_Gallery_activity
            Fragment t2s = new T2SFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("hb_visible",true);
            bundle.putString("text",i);
            t2s.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,t2s).commit();
        }else
            //Caso avvio la app
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /*
    *   Se il drawer è aperto e schiaccio back lo chiude
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
        int id = item.getItemId();
        if(id == R.id.home){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,new HomeFragment())
                    .addToBackStack(HOME_TAG).commit();
        }
        else if (id == R.id.hist) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,new HistoryFragment())
                    .addToBackStack(null).commit();
        } else if (id == R.id.lang) {

        } else if (id == R.id.settings) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .addToBackStack(null).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Check permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
