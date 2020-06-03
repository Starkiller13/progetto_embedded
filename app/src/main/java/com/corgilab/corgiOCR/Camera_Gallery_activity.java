package com.corgilab.corgiOCR;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

import com.corgilab.corgiOCR.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Camera_Gallery_activity extends AppCompatActivity {
    private static final String TAG = "Camera_Gallery_activity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE=100;
    private static final int PIC_CROP = 2;
    private boolean first_click = true;
    private ImageView imageView = null;
    private Uri imageUri;
    private String currentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_gallery_activity);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Back action al posto di nav drawer open toggle

        //Gestisco l'intent mandato da Main_Activity
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
                            "com.corgilab.corgiOCR.fileprovider",
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
        Boolean theme = intent.getBooleanExtra("Theme",false);
        if(!theme){
            setTheme(R.style.AppThemeDark);
            findViewById(R.id.layout_cga).setBackgroundResource(R.color.DarkBackground);
        }else{
            setTheme(R.style.AppThemeLight);
        }
    }

    /*
    * Il metodo elabora l'immagine selezionata e ne trascrive il testo in una stringa
    * Viene poi richiamata Main Activity che gestisce
    * il fragment per la visualizzazione e la riproduzione audio del testo trascritto
    */
    public void onElaborateClick(View view) {
        //Caso limite, non si verifica mai(in teoria)
        if (currentPhotoPath == null) {
            Toast myToast = Toast.makeText(this, "No image found", Toast.LENGTH_SHORT);
            myToast.show();
        } else if(first_click){
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            //when I press the button process, go to Text2Speech activity
            //make an intent
            StringBuilder st = new StringBuilder();
            st = staticOCR_t2s.elaborate_button(currentPhotoPath,this);
            String str = st.toString().replace("\n", " ");
            //Classe Text2Speech
            Intent t2s = new Intent(this, MainActivity.class);
            t2s.putExtra("result","true");
            t2s.putExtra("message", str);
            t2s.putExtra("imgPath",currentPhotoPath);
            startActivityForResult(t2s,0);
            first_click = false;
        }
    }
    /*private void cropIntent(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(Uri.parse(currentPhotoPath), "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 1500);
        cropIntent.putExtra("outputY", 1500);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, PIC_CROP);
    }*/
    /*
    *   Crea un file temporaneo in cui salvare l'immagine scattata o scelta
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
        //image.deleteOnExit();
        return image;
    }
    /*
    *   Metodo rotateImage, mi serve per ruotare l'immagine nel caso l'intent mi dia robe brutte
    */
    private Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(currentPhotoPath);
        }catch (IOException e){e.printStackTrace();}
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case  ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
            default:
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return newBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG,"Result code: "+resultCode);
        //Non è stata fatta alcuna azione, torno a mainactivity
        if(resultCode==0){
            startActivity(new Intent(this,MainActivity.class));
        }
        //Immagine Scattata, la faccio visualizzare nella view apposita
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = rotateImage(BitmapFactory.decodeFile(currentPhotoPath));
            try {
                FileOutputStream fout = new FileOutputStream(currentPhotoPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fout);
                fout.flush();
                fout.close();
            }catch (Exception w){}
                imageView.setImageBitmap(bitmap);
        }
        /*
        * immagine scelta, la faccio visualizzare e poi la salvo in un file temporaneo
        * L'ultimo passaggio mi serve perchè le ultime versioni di android non mi
        * restituiscono un absolute path e nemmeno l'estensione del file
        */
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri =data.getData();
            ImageView imageView = findViewById(R.id.image_view_1);
            imageView.setImageURI(imageUri);
            File f=null;
            FileOutputStream fout = null;
            Bitmap bitmap;
            try {
                 f = createImageFile();
                 fout = new FileOutputStream(f);
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fout);

            }catch(Exception e){}
            currentPhotoPath= f.getAbsolutePath();
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations() && getExternalFilesDir(Environment.DIRECTORY_PICTURES) != null) {
            //deleteTempFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        }
    }

    /*
    *   Routine per eliminare i file temporanei alla chiusura dell'activity
    */
    public static boolean deleteTempFiles(File file) {
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
