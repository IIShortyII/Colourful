package com.example.colourful;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btn_takePic;
    ImageView m_imgV_Main;
    ImageView imgV_Point;
    TextView txt_breite;
    TextView txt_hohe;
    String m_currentPhotoPath;
    Bitmap m_Bitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn_takePic = (Button) findViewById(R.id.btn_takepic);
        m_imgV_Main = (ImageView) findViewById(R.id.imgV_Main);
        imgV_Point = (ImageView) findViewById(R.id.imgV_MiddleMarker);
        txt_breite = (TextView) findViewById(R.id.txt_breite);
        txt_hohe = (TextView) findViewById(R.id.txt_hohe);





        imgV_Point.setVisibility(View.INVISIBLE);

        btn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    // Create the File where the photo should go
                    Toast.makeText(getApplicationContext(),"Sind Drinnen",Toast.LENGTH_SHORT).show();
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        //Log.i(TAG, "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        }
                }
            }
        });
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        m_currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                m_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(m_currentPhotoPath));
                m_imgV_Main.setImageBitmap(m_Bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getApplicationContext(),"Drinnen",Toast.LENGTH_SHORT).show();
        if (requestCode == 0 && resultCode == Activity.RESULT_OK ) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                Bitmap Photo = BitmapFactory.decodeFile(currentPhotoPath);
                imgV_Main.setImageBitmap(Photo);
                imgV_Point.setVisibility(View.VISIBLE);
            }

        }}
*/




















}