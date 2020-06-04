package com.corgilab.corgiOCR;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class Camera_Gallery_activity extends AppCompatActivity {
    private static final String TAG = "Camera_Gallery_activity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE=100;
    private static final int PIC_CROP = 2;
    private File image = null;
    private boolean first_click = true;
    private ImageView imageView = null;
    private Uri photoURI;
    private String currentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Gestisco l'intent mandato da MainActivity
        Intent intent = getIntent();
        //Gestisco il tema della app
        boolean theme = intent.getBooleanExtra("Theme",false);
        if(theme){
            setTheme(R.style.AppThemeDark);
        }else{
            setTheme(R.style.AppThemeLight);
        }
        setContentView(R.layout.camera_gallery_activity);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//Back action al posto di nav drawer open toggle

        //Gestisco l'intent mandato da Main_Activity
        int act = intent.getIntExtra("activity",0);
        Log.v(TAG, "valore di act: "+ act);
        switch (act){
            //Cattura immagine
            case 0: {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageView = findViewById(R.id.image_view_1);
                try {
                    image = createImageFile();
                } catch (IOException e) {
                    break;
                }
                photoURI = FileProvider.getUriForFile(this,
                        "com.corgilab.corgiOCR.fileprovider",
                        image);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                break;
            }
            //Ricerca foto
            case 1: {
                try {
                    image = createImageFile();
                } catch (IOException e) {
                    break;
                }
                photoURI = FileProvider.getUriForFile(this,
                        "com.corgilab.corgiOCR.fileprovider",
                        image);
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //avvio l'activity per recuperare l'immagine dalla galleria
                startActivityForResult(gallery, PICK_IMAGE);
                break;
            }
        }

    }

    /**
    * Il metodo elabora l'immagine selezionata e ne trascrive il testo in una stringa
    * Viene poi richiamata MainActivity con un Intent specifico per gestire
    * il fragment per la visualizzazione e la riproduzione audio del testo trascritto
    *(T2SFragment)
    * @Param view View passata tramite l'onClick del bottone di elaborazione
    */
    public void onElaborateClick(View view) {
        //Caso limite, non si verifica mai(in teoria)
        if (currentPhotoPath == null) {
            Toast myToast = Toast.makeText(this, "No image found", Toast.LENGTH_SHORT);
            myToast.show();
        }
        //Mi serve per far si che non si possa cliccare il bottone più volte facendo crashare l'app
        else if(first_click){
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            /*
            *   Elaboro l'immagine ottenuta
            */
            StringBuilder st = staticOCR_t2s.elaborate_button(currentPhotoPath,this);
            String str = st.toString().replace("\n", " ");
            /*
            *   Creo l'intent per MainActivity con degli extra per permettergli di capire
            *   quale fragment creare
            */
            Intent t2s = new Intent(this, MainActivity.class);
            t2s.putExtra("result","true");
            t2s.putExtra("message", str);
            t2s.putExtra("imgPath",currentPhotoPath);
            startActivityForResult(t2s,0);
            first_click = false;
        }
    }

    /** Crea un file temporaneo in cui salvare l'immagine scattata o scelta
     *
     * @return File ritorna un file temporaneo in cui salvare le immagini
     * @throws IOException da gestire se la creazione non va a buon fine
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

    /** Metodo rotateImage, mi serve per ruotare l'immagine nel caso che l'intent non mi restituisca
     * l'immagine orientata nel modo che voglio
     *
     * @param bitmap il bitmap non ruotato
     * @return Bitmap ruotato a seconda delle indicazioni trovate da ExifInterface
     */
    private Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(currentPhotoPath);
        }catch (IOException e){e.printStackTrace();}
        assert exifInterface != null;
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
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    /** Gestisco i diversi intent lanciati a seconda che io abbia scelto un'immagine
     * oppure la abbia scattata
     *
     * parametri di default:
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
            saveImage(BitmapFactory.decodeFile(currentPhotoPath));
        }
        /*
        * immagine scelta, la faccio visualizzare e poi la salvo in un file temporaneo
        * L'ultimo passaggio mi serve perchè le ultime versioni di android non mi
        * restituiscono un absolute path e nemmeno l'estensione del file
        */
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            saveImage(imageUri);
        }
        ImageView imageView = findViewById(R.id.image_view_1);
        imageView.setImageBitmap((Bitmap) BitmapFactory.decodeFile(currentPhotoPath));
    }

    /** L'immagine è già salvata ma è possibile che non sia dritta e che quindi debba essere ruotata
     *
     * @param input il Bitmap da ruotare
     * @return boolean true o false a seconda che le operazioni siano andate buon fine o meno
     */
    private boolean saveImage(Bitmap input){
        Bitmap bitmap = rotateImage(input);
        try {
            FileOutputStream fout = new FileOutputStream(currentPhotoPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
            return  true;
        }catch (Exception w){
            return false;
        }
    }

    /** L'immagine viene salvata in un file temporaneo e viene ruotata
     *
     * @param uri l'uri del file in cui salvare l'immagine
     * @return boolean true o false a seconda che le operazioni siano andate buon fine o meno
     */
    private boolean saveImage(Uri uri){
        FileOutputStream fout = null;
        Bitmap bitmap;
        try {
            fout = new FileOutputStream(image);
            bitmap = rotateImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            return true;
        }catch(Exception e){return false;}
    }
    /** Metodo static per eliminare i file temporanei(viene richiamato da altre classi)
    *   @Param file il file o la directory da eliminare
    */
    public static void deleteTempFiles(File file) {
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
        file.delete();
    }



}
