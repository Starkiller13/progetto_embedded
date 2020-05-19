package com.example.progetto_embedded;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Gallery_activity extends AppCompatActivity {

    private static final int PICK_IMAGE=100;
    private static final String TAG = "Gallery_activity";
    Uri imageUri;
    String currentPhotoPath = null;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(gallery,PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            ImageView imageView = findViewById(R.id.image_view_2);
            imageView.setImageURI(imageUri);
            currentPhotoPath=imageUri.getPath().substring(4);
            Log.v(TAG,imageUri.getPath().substring(5));
        }
    }

}
