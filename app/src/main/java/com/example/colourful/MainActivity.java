package com.example.colourful;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
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
        txt_breite = (TextView) findViewById(R.id.txt_breite);
        txt_hohe = (TextView) findViewById(R.id.txt_hohe);


//Taking Picture when Button is pressed
        btn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri DummyUri = setDummyFile();
                //Todo: Delete Old Files if Cache is to big
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //Checking if Cam available
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    //Checking if Dummy File is available
                    if (DummyUri != null) {
                        //Define Saving Path for taken Picture
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, DummyUri);
                        //Open Camera
                        startActivityForResult(cameraIntent, 0);
                        }
                }
            }
        });
    }
//Receiving Taken Picture
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            //Show taken picture in ImageView
            try {
                m_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPath);
                m_imgV_Main.setImageBitmap(m_Bitmap);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ERROR ","Error while showing Picture in ImgView with Path: "+currentPath);
            }

//Creating OnTouchListener for User selected area of Colour
    m_imgV_Main.setOnTouchListener(new View.OnTouchListener(){
    @Override
    public boolean onTouch(View v, MotionEvent event) {
         if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
             m_imgV_Main.setDrawingCacheEnabled(true);
             m_imgV_Main.buildDrawingCache(true);
             Bitmap BitmapBuffer = m_imgV_Main.getDrawingCache();

             int pixelBuffer = BitmapBuffer.getPixel((int) event.getX(),(int)event.getY());

             //getting RGB values
                  int r = Color.red(pixelBuffer);
                  int g = Color.green(pixelBuffer);
                  int b = Color.blue(pixelBuffer);

             //getting Hex value
                  String hex = Integer.toHexString(pixelBuffer);
                  hex = "#" + hex.substring(2);

             //Give User RGB and Hex Code of selected Colour
                  txt_breite.setText("RGB Code: "+r +","+g +","+b);
                  txt_hohe.setText("HEX Code: " + hex);
                  Log.i("RGB Code",r +","+g +","+b);
                  Log.i("HEX Code", hex);
                    }
                    return true;
                }
            });
            }
        }




//--------------------------BEGIN OF SUBROUTINES----------------------------

//Create DummyFile to Check if file is already there
    private Uri setDummyFile(){
        Uri photoURI = null;
        try {
            photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR","DummyFile Path or Name is already used!");
        }
        currentPath = photoURI;
        return photoURI;}



//Creating FilePath for DummyFile and Manages FileName
    private File createImageFile() throws IOException {
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
        return image;
    }

















}