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
    //Creating Figures
    Button btn_takePic;
    ImageView m_imgV_Main;
    ImageView imgV_Point;
    TextView txt_breite;
    TextView txt_hohe;
    //String m_currentPhotoPath;
    Uri currentPath;
    Bitmap m_Bitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Find Figures and Implement them
        btn_takePic = (Button) findViewById(R.id.btn_takepic);
        m_imgV_Main = (ImageView) findViewById(R.id.imgV_Main);
        imgV_Point = (ImageView) findViewById(R.id.imgV_MiddleMarker);
        txt_breite = (TextView) findViewById(R.id.txt_breite);
        txt_hohe = (TextView) findViewById(R.id.txt_hohe);




        //Pre-creating Dummy File to save pictures
        imgV_Point.setVisibility(View.INVISIBLE);


        //Taking Picture when Button is pressed
        btn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri DummyUri = setDummyFile();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {          //Checking if Cam available
                    if (DummyUri != null) {                                                        //Checking if Dummy File is available
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, DummyUri);                  //Define Saveing Path for taken Pic
                        //Toast.makeText(getApplicationContext(),DummyUri.toString(),Toast.LENGTH_LONG).show();
                        startActivityForResult(cameraIntent, 0);                    //Open Camera

                        }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            //Show taken picture in ImageView
            try {
                m_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPath);
                m_imgV_Main.setImageBitmap(m_Bitmap);
                imgV_Point.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(),currentPath.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            BitmapFactory.Options bitMapOption=new BitmapFactory.Options();
            bitMapOption.inJustDecodeBounds=true;

            //TODO: File Not found exception kommt obwohl datei erstellt wird. 
            BitmapFactory.decodeFile(currentPath.getPath(), bitMapOption);
            Log.d("Path of Bitmap Factory:", currentPath.getPath());


            txt_breite.setText(String.valueOf( bitMapOption.outWidth));
            txt_hohe.setText(String.valueOf( bitMapOption.outHeight));
            Log.d("Breite",""+bitMapOption.outWidth);
            Log.d("Höhe",""+bitMapOption.outHeight);
            }

        }




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BMP_" + timeStamp + "_";
        //TODO: Change DummyFile Directory to Internal App Temp File Directory? getFilesDir()??
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".bmp",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        //m_currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private Uri setDummyFile(){
        Uri photoURI = null;
        try {
            photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPath = photoURI;
        return photoURI;}



















}