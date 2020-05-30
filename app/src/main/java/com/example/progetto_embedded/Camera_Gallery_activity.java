//NOTA BENE: quando si scarta la foto, si viene mandati ad una UI in cui sono presenti solo process e hear

package com.example.progetto_embedded;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Camera_Gallery_activity extends AppCompatActivity {
    private static final String TAG = "Camera_Gallery_activity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE=100;
    private ImageView imageView = null;
    private Uri imageUri;
    private String currentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_gallery_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int act = intent.getIntExtra("activity",0);
        Log.v(TAG, "valore di act: "+ act);
        switch (act){
            //Cattura immagine
            case 0: {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageView = findViewById(R.id.image_view_1);
                File image = null;
                try {
                    image = createImageFile();
                } catch (IOException e) {
                }
                if (image != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            image);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            }
            //Ricerca foto
            case 1: {
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //avvio l'activity per recuperare l'immagine dalla galleria
                startActivityForResult(gallery, PICK_IMAGE);
                break;
            }
        }


    }

    /*
    * Il metodo elabora l'immagine selezionata e ne trascrive il testo in una stringa
    * Viene poi richiamata Main Activity che gestisce
    * il fragment per la visualizzazione e la riproduzione audio del testo trascritto
    */
    public void onElaborateClick(View view) {
        if (currentPhotoPath == null) {
            Toast myToast = Toast.makeText(this, "No image found", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
            //when I press the button process, go to Text2Speech activity
            //make an intent
            StringBuilder st = new StringBuilder();
            st = staticOCR_t2s.elaborate_button(currentPhotoPath,this);
            String str = st.toString().replace("\n", " ");
            //Classe Text2Speech
            Intent t2s = new Intent(this, MainActivity.class);
            t2s.putExtra("result","true");
            t2s.putExtra("message", str);
            startActivityForResult(t2s,0);
        }
    }

    /*
    *   Crea un file temporaneo in cui salvare l'immagine scattata
    */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        image.deleteOnExit();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG,"Result code: "+resultCode);
        if(resultCode==0){
            startActivity(new Intent(this,MainActivity.class));
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
                imageUri = data.getData();
                Log.v(TAG,imageUri.getPath().substring(4));
                ImageView imageView = findViewById(R.id.image_view_1);
                imageView.setImageURI(imageUri);
                currentPhotoPath=imageUri.getPath().substring(5);//i primi 4 caratteri non mi servono ("/raw")
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if(!isChangingConfigurations()&&getExternalFilesDir(Environment.DIRECTORY_PICTURES)!=null) {
            deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        }
    }

    /*
    *   Routine per eliminare i file temporanei alla chiusura dell'activity
    */
    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }



}
