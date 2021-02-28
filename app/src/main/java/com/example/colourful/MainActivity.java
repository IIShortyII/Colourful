package com.example.colourful;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
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
    ImageView imgV_Main;
    ImageView imgV_Point;
    TextView txt_breite;
    TextView txt_hohe;
    String currentPhotoPath;
    private Object Bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn_takePic = (Button) findViewById(R.id.btn_takepic);
        imgV_Main = (ImageView) findViewById(R.id.imgV_Main);
        imgV_Point = (ImageView) findViewById(R.id.imgV_MiddleMarker);
        txt_breite = (TextView) findViewById(R.id.txt_breite);
        txt_hohe = (TextView) findViewById(R.id.txt_hohe);

        imgV_Point.setVisibility(View.INVISIBLE);

        btn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK ) {

            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                Bitmap Photo = BitmapFactory.decodeFile(currentPhotoPath);
                imgV_Main.setImageBitmap(Photo);
                imgV_Point.setVisibility(View.VISIBLE);
            }

        }}

    //TODO: String vom Bild auslesen und in die Methode eingeben!

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        txt_breite.setText("Button geht");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BMP_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".bmp",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
/*
     public void scanningImage(Bitmap Photo, Intent data){
         Photo = (Bitmap)data.getExtras().get("data");
         Bitmap picture = BitmapFactory.decodeFile(toString(Photo));
         int Width = picture.getWidth();
         int Height = picture.getHeight();
         txt_breite.setText(Width);
         txt_hohe.setText(Height);
     }*/









}