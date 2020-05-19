package com.example.progetto_embedded;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Gallery_activity extends AppCompatActivity {
    private static final int PICK_IMAGE=100;
    Uri imageUri;
    private String currentPhotoPath = null; //serve per memorizzare la posizione dell'immagine che uso


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //inizializzazione
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //nuovo intent
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //avvio l'activity per recuperare l'immagine dalla galleria
        startActivityForResult(gallery,PICK_IMAGE);
    }


    //metodo richiamato dal bottone per elaborare la foto nella image_view_2
    public void onElaborateClick(View view) {
        if (currentPhotoPath == null) {
            Toast myToast = Toast.makeText(this, "No image found", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
            StringBuilder st = staticOCR_t2s.elaborate_button(currentPhotoPath,this);
            TextView tw = findViewById(R.id.output_textview_2);
            tw.setText(st);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        //recupero l'immagine e la inserisco nella variabile currentPhotoPath
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            ImageView imageView = findViewById(R.id.image_view_2);
            imageView.setImageURI(imageUri);
            currentPhotoPath=imageUri.getPath().substring(4);//i primi 4 caratteri non mi servono ("/raw")
        }
    }

}
