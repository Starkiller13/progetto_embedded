package com.example.progetto_embedded;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class staticOCR_t2s extends AppCompatActivity {
    //metodi statici
    public static StringBuilder elaborate_button(String currentPhotoPath, Context context)
    {
        StringBuilder st = new StringBuilder();
        Log.v("PIPPO", currentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            TextRecognizer rec = new TextRecognizer.Builder(context).build();
            if(!rec.isOperational()){
                //st.append("ERROR: Google vision API are not responding\n");
                st.append("ERROR: I'm not able to elaborate your image!\n");
            }else {
                Frame f = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = rec.detect(f);
                for(int i=0; i<items.size();i++){
                    TextBlock tb = items.valueAt(i);
                    st.append(tb.getValue());
                    st.append("\n");
                }
            }
            return st;
    }
}
