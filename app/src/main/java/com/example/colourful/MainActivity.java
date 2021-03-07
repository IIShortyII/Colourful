package com.example.colourful;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.example.colourful.database.ColourName;
import com.example.colourful.database.ColourNameDataBase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.*;



public class MainActivity extends AppCompatActivity {
    //Creating Figures
    Button btn_takePic;
    ImageView m_imgV_Main;
    TextView txt_RGB;
    TextView txt_HEX;
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
        txt_RGB = (TextView) findViewById(R.id.txt_RGB);
        txt_HEX = (TextView) findViewById(R.id.txt_HEX);


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
                txt_HEX.setText("HEX Code: #ffffff");
                txt_RGB.setText("RGB Code: 0,0,0");
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
                  txt_RGB.setText("RGB Code: "+r +","+g +","+b);
                  txt_HEX.setText("HEX Code: " + hex);
                  String ColourNameString = getColourName(hex, r, g, b);
                  Log.i("ColourName", ColourNameString);
                  Log.i("RGB Code",r +","+g +","+b);
                  Log.i("HEX Code", hex);




              /*
             List<ColourName> ColourList= getColourName(hex); //Getting Colour Name from Database
             List<ColourName> Test= getAll();
                //insertAll();
                Log.i("Ausgabe",ColourList.toString());
                Log.i("Ausgabe All",Test.toString());

               */
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



    private String getColourName(String HEX_Code, int red, int green, int blue){
        String ColourName = null;
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        Log.i("floatausgabe",hsv.toString());

        float hue = hsv[0];
        float saturation = hsv[1]*10.0f;
        float value = hsv[2]*10.0f;

        Log.i("hue", String.valueOf(hue));
        Log.i("saturation", String.valueOf(saturation));
        Log.i("value", String.valueOf(value));

        //Colour Area Matrix in the HSV Colour Circle
        //Define white spot
        if (saturation<5 && value >95){
            ColourName = "White";
        }
        //Define black line
        else if (value < 5){
            ColourName ="Black";
        }
        else {
            //Define rough colour directions (16 pcs)
            //Red Area ( Hue 347 to 15)
            if (hue > 347 || hue <= 15) {

            }
            //Orange Area (Hue 16 to 36)
            else if (hue > 15 && hue <= 36) {

            }
            //Turmeric Area (Hue 37 to 46)
            else if (hue > 36 && hue <= 46) {

            }
            //Yellow Cheese Area (Hue 47 to 55)
            else if (hue > 46 && hue <= 55) {

            }
            //Yellow Area (Hue 55 to 63)
            else if (hue > 55 && hue <= 63) {

            }
            //Yellow-Green Area (Hue 64 to 70)
            else if (hue > 64 && hue <= 70) {

            }
            //Charitreuse Area (Hue 71 to 82)
            else if (hue > 70 && hue <= 82) {

            }
            //Green-Pea Area (Hue 83 to 105)
            else if (hue > 82 && hue <= 105) {

            }
            //Green Area (Hue 106 to 133)
            else if (hue > 105 && hue <= 133) {

            }
            //Clover Area (Hue 134 to 153)
            else if (hue > 133 && hue <= 153) {

            }
            //Emerald Area (Hue 154 to 166)
            else if (hue > 153 && hue <= 166) {

            }
            //Malachite Area (Hue 167 to 176)
            else if (hue > 166 && hue <= 176) {

            }
            //Cyan Area (Hue 177 to 185)
            else if (hue > 176 && hue <= 185) {

            }
            //Turquoise Area (Hue 186 to 195)
            else if (hue > 185 && hue <= 195) {

            }
            //Azure Area (Hue 196 to 207)
            else if (hue > 195 && hue <= 207) {

            }
            //Royal Blue Area (Hue 208 to 227)
            else if (hue > 207 && hue <= 227) {

            }
            //Blue Area (Hue 228 to 253)
            else if (hue > 228 && hue <= 253) {

            }
            //Dioxazine Area (Hue 254 to 273)
            else if (hue > 253 && hue <= 273) {

            }
            //Violet Area (Hue 274 to 285)
            else if (hue > 273 && hue <= 285) {

            }
            //Aniline Area (Hue 286 to 295)
            else if (hue > 285 && hue <= 295) {

            }
            //Magenta Area (Hue 296 to 305)
            else if (hue > 295 && hue <= 305) {

            }
            //Bougainvillea Area (Hue 306 to 315)
            else if (hue > 305 && hue <= 315) {

            }
            //Pink Area (Hue 316 to 326)
            else if (hue > 315 && hue <= 326) {

            }
            //Red-Plum Area (Hue 327 to 347)
            else if (hue > 326 && hue <= 347) {

            }
        }




        return ColourName;
    }


// Colour Name with Database (NOTFINAL)
    /*

        private List getColourName(String HEX_CODE){
        ColourNameDataBase db = ColourNameDataBase.geTDbInstance(this.getApplicationContext());
        List<ColourName> ColourList =  db.ColourNameDAO().findColourName(HEX_CODE);
        return ColourList;
    }

    private List getAll() {
        ColourNameDataBase db = ColourNameDataBase.geTDbInstance(this.getApplicationContext());
        List<ColourName> ColourList = db.ColourNameDAO().findAll();
        return ColourList;
    }
        private void insertAll(){
            ColourNameDataBase db = ColourNameDataBase.geTDbInstance(this.getApplicationContext());
             db.ColourNameDAO().insertColour(new ColourName("#ffffff","schwarz"));

    }

*/


//END OF CODE
}