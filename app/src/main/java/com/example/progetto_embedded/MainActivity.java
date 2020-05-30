package com.example.progetto_embedded;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    final String TAG = "main_act";
    private DrawerLayout navDrawer;
    private NavigationView mNavigationView;
    private FrameLayout f;
    private ConstraintLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        f = findViewById(R.id.fragment_container);
        params = (ConstraintLayout.LayoutParams) f.getLayoutParams();

        String i = getIntent().getStringExtra("result");
        /*Gestisco il ritorno a main activity da Camera Gallery Activity
        * l'interfaccia è gestita da fragment quindi devo capire quale fragment usare
        *
        * */
        if(i!=null){
            //viene passato un intent da Camera_Gallery_activity
            Log.v(TAG,i);
            //E' ridondante ma non so se dovrò fare altro più avanti
            if(i.compareTo("true")==0) {
                String txt = getIntent().getStringExtra("message");
                Bundle bundle = new Bundle();
                bundle.putString("text", txt);
                //Creo il fragment T"SFragment
                T2SFragment frag = new T2SFragment();
                frag.setArguments(bundle);
                params.verticalBias=0.1f;
                f.setLayoutParams(params);
                //Imposto il fragment nel FrameView
                getSupportFragmentManager().beginTransaction().replace(f.getId(), frag).commit();
            }
        }else{
            params.verticalBias=1f;
            f.setLayoutParams(params);
            //Imposto il fragment Home_Fragment nel FrameView
            getSupportFragmentManager().beginTransaction().replace(f.getId(),new HomeFragment()).commit();
        }
    }
    @Override
    public void onBackPressed(){
        if(navDrawer.isDrawerOpen(GravityCompat.START)){
            navDrawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

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

    public void language(View view)
    {

    }

    public void history(View view)
    {
        //PER LEGGERE LE ACTIVITY DAL DATABASE
        //https://www.youtube.com/watch?v=Dik-sGDWTrE

        //recyclerView
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.home){
            params.verticalBias=1f;
            f.setLayoutParams(params);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }
        else if (id == R.id.hist) {
            params.verticalBias=0f;
            f.setLayoutParams(params);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HistoryFragment()).commit();
        } else if (id == R.id.lang) {

        } else if (id == R.id.settings) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Check permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
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
