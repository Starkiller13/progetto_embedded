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
import androidx.fragment.app.FragmentTransaction;

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
    final String HOME_TAG = "home_tag";
    private DrawerLayout navDrawer;
    private NavigationView mNavigationView;

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
        //Imposto il fragment Home_Fragment nel FrameView
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
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

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(!item.isChecked());
        int id = item.getItemId();
        if(id == R.id.home){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).addToBackStack(HOME_TAG).commit();
        }
        else if (id == R.id.hist) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HistoryFragment()).addToBackStack(null).commit();
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
