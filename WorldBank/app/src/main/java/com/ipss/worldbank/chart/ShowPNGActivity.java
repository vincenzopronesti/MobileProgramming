package com.ipss.worldbank.chart;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ipss.worldbank.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/** Main class to show png chart files to the user */
public class ShowPNGActivity extends AppCompatActivity {
    private ImageView pngChartIv;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_show_png);
        pngChartIv = findViewById(R.id.pngChartIv);
        fileName = getIntent().getStringExtra("filename");
        loadImageFromStorage();

    }

    /** Method to convert png file into bitmap format
     * */
    private void loadImageFromStorage() {
        ContextWrapper cw = new ContextWrapper(this);

        // path to /data/data/worldbank/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File f = new File(directory, fileName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f)); // Create bitmap from png
            pngChartIv.setImageBitmap(b); // Insert the bitmap into the view

        } catch (FileNotFoundException e) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this,
                    R.style.Theme_AppCompat_Light_Dialog_Alert);
            alert.setTitle(R.string.error);
            alert.setMessage(R.string.error_msg);
            alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            alert.show();
        }

    }

}
