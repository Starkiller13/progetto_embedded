package com.example.progetto_embedded;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void camera(View view)
    {
        //Controllo di avere i permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
        }else {
            //Avvio l'activity dedicata alla camera
            Intent cameraV = new Intent(this, Camera_activity.class);
            startActivityForResult(cameraV, 0);
        }
    }

    public void gallery(View view)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }else {
            //Avvio l'activity dedicata alla galleria
            Intent galleryV = new Intent(this, Gallery_activity.class);
            startActivityForResult(galleryV, 0);
        }
    }

    public void language(View view)
    {

    }

    public void history(View view)
    {

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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted, avvio l'activity della camera
                    Intent cameraV = new Intent(this,Camera_activity.class);
                    startActivityForResult(cameraV,0);
                } else {
                    //Permission denied
                    Toast myToast = Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
                    myToast.show();
                }
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Permission granted, avvio l'activity della camera
                        Intent galleryV = new Intent(this,Gallery_activity.class);
                        startActivityForResult(galleryV,0);
                    } else {
                        //Permission denied
                        Toast myToast = Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
                        myToast.show();
                    }
                }
        }
    }


}
