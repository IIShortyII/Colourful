package com.example.colourful;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.colourful.database.ColourName;
import com.example.colourful.database.ColourNameDataBase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.*;



public class MainActivity extends AppCompatActivity {

    //Creating Figures
    Toolbar toolbar;
    Button btn_takePic;
    ImageButton btn_Settings;
    ImageView m_imgV_Main;
    TextView txt_RGB;
    TextView txt_HEX;
    TextView txt_ColourName;
    //String m_currentPhotoPath;
    Uri currentPath;
    Bitmap m_Bitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LoadTheme(getDefault());
        setContentView(R.layout.activity_main);

        //Find Figures and Implement them
        btn_takePic = findViewById(R.id.btn_takepic);
        btn_Settings = findViewById(R.id.btn_Settings);
        m_imgV_Main = findViewById(R.id.imgV_Main);
        txt_RGB = findViewById(R.id.txt_RGB);
        txt_HEX = findViewById(R.id.txt_HEX);
        txt_ColourName = findViewById(R.id.txt_ColourName);





//Settings Button
        btn_Settings.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent goToSettings = new Intent(MainActivity.this, Settings.class);
                startActivity(goToSettings);
            }
        });


//Taking Picture when Button is pressed
        btn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
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
                }else{
                    Toast.makeText(MainActivity.this, "No Access to Storage or Camera. Please give Access Manually over Android Settings.",Toast.LENGTH_LONG).show();
                }}
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
                m_Bitmap = lessResolution(correctBitMapURI(this.currentPath.toString()), 1000, 1000);
                m_imgV_Main.setImageBitmap(m_Bitmap);
                txt_HEX.setText("HEX Code: #ffffff");
                txt_RGB.setText("RGB Code: 0,0,0");
                txt_ColourName.setText("");
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
                  if (hex.length() != 8){hex="#ffffff";}else{
                  hex = "#" + hex.substring(2);}

             //Give User RGB and Hex Code of selected Colour
                  txt_RGB.setText("RGB Code: "+r +","+g +","+b);
                  txt_HEX.setText("HEX Code: " + hex);
                  String ColourNameString = getColourName(hex, r, g, b);
                  txt_ColourName.setText("Colour Name: "+ColourNameString);
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


    public String correctBitMapURI(String CurrentPath){
        Log.i("Old String", CurrentPath);
        CurrentPath = CurrentPath.replace("content://com.example.colourful.provider/external_files/Pictures","/sdcard/Pictures");
        Log.i("New String", CurrentPath);

        return CurrentPath;
    }



//Scale Down BitMap for ImageView
    public static Bitmap lessResolution (String filePath, int width, int height) {
        int reqHeight = height;
        int reqWidth = width;
        BitmapFactory.Options options = new BitmapFactory.Options();

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }








    private String getColourName(String HEX_Code, int red, int green, int blue){
        String ColourName = null;
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);

        float hue = hsv[0];
        float saturation = hsv[1]*100.0f;
        float value = hsv[2]*100.0f;

        Log.i("hue", String.valueOf(hue));
        Log.i("saturation", String.valueOf(saturation));
        Log.i("value", String.valueOf(value));

        //Colour Area Matrix in the HSV Colour Circle
        //Define white spot and Gray Area
        if (saturation<5){
            if (value >=95){
            ColourName = "White";}
            else if (value >=83){
                ColourName = "Grey 75%";
            }
            else if (value >=50){
                ColourName = "Grey 50%";
            }
            else if (value >=10){
                ColourName = "Grey 10%";
            }
            else { ColourName ="Black";}
        }
        //Define black line
        else if (value < 10){
            ColourName ="Black";
        }
        //Define rough colour directions (24 pcs)
        else {

            //Red Area ( Hue 347 to 15)
            if (hue > 347 || hue <= 15) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Red";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Pale Raspberry";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Ham";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Dark Red";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Full Red";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Old Pink";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Fire Red";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Red";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Red";
                }
            }
            //Orange Area (Hue 16 to 36)
            else if (hue > 15 && hue <= 36) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Orange";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Peanut Butter";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Parmesan Cheese";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Orange Brown";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Ochre Brown";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Grey Brown";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Darker Orange Brown";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Brown";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Darker Orange Brown";
                }
            }
            //Turmeric Area (Hue 37 to 46)
            else if (hue > 36 && hue <= 46) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Yellow Ochre";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Turmeric";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Buff";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Tan";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Ochre";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Lemon Grey";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Milk Chocolate";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Ochre";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Darker Lemon Grey";
                }
            }
            //Yellow Cheese Area (Hue 47 to 55)
            else if (hue > 46 && hue <= 55) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Olive Oil";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Yellow Cheese";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Wheat Ear";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Cane Toad";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Light Olive Oil";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Gray Yellow";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Cow Dung";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Brown Yellow";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Dark Gray Yellow";
                }
            }
            //Yellow Area (Hue 55 to 63)
            else if (hue > 55 && hue <= 63) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Wasabi";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Yellow";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Butter";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Olive";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Yellow Olive";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Olive Yellow";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Olive Drab";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Olive Yellow";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Olive Yellow ";
                }
            }
            //Yellow-Green Area (Hue 64 to 70)
            else if (hue > 64 && hue <= 70) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Green Grape";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Golden Delicious";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Champagne";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Light Kelp";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Yellow Green Apple";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Yellow Green";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Dark Kelp";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Yellow Green";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Yellow Green";
                }
            }
            //Charitreuse Area (Hue 71 to 82)
            else if (hue > 70 && hue <= 82) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Celery";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Chartreuse";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Avocado";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Sage";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Apple Green";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Apple Green";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Oak Leaf";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Apple Green";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Apple Green";
                }
            }
            //Green-Pea Area (Hue 83 to 105)
            else if (hue > 82 && hue <= 105) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Basil";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Green Pea";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Cabbage Green";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Spinach";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Cactus Green";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Cactus Green";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Rhubarb";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Cactus Green";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Cactus Green";
                }
            }
            //Green Area (Hue 106 to 133)
            else if (hue > 105 && hue <= 133) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Green";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Granny Smith";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Green Hellebore";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Green Grass";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Granny Smith";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Granny Smith";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Zucchini";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Granny Smith";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Granny Smith";
                }
            }
            //Clover Area (Hue 134 to 153)
            else if (hue > 133 && hue <= 153) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Clover";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Chayote";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Celadon";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Shaded Fern";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Mint Green";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Mint Green";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Cucumber";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Mint Green";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Mint Green";
                }
            }
            //Emerald Area (Hue 154 to 166)
            else if (hue > 153 && hue <= 166) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Light Emerald";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Chrysolite";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Variscite";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Emerald";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Emerald";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Emerald";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Brunswick Green";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Emerald";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Emerald";
                }
            }
            //Malachite Area (Hue 167 to 176)
            else if (hue > 166 && hue <= 176) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Shallow Sea Green";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Verdigris";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Blue Agave";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Broccoli";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Light Grey Green";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Grey Green";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Malachite";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Grey Green";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Dark Grey Green";
                }
            }
            //Cyan Area (Hue 177 to 185)
            else if (hue > 176 && hue <= 185) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Dark Byan";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Cyan";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Blue Spruce Light";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Blue Spruce Dark";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Light Blue";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Blue";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Pthalo Green";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Grey Blue";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Light Grey Blue";
                }
            }
            //Turquoise Area (Hue 186 to 195)
            else if (hue > 185 && hue <= 195) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Blue Topaz";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Turquoise";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Uranus Blue";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Sea Green";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Sea Blue";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Sea Grey";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Dark Sea Green";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Sea Blue";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Dark Blue Grey";
                }
            }
            //Azure Area (Hue 196 to 207)
            else if (hue > 195 && hue <= 207) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Dark Azure";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Light Azure";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Powder Blue";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Cobalt Blue";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Azure";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Azure";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Prussian Blue";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Azure";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Azure";
                }
            }
            //Royal Blue Area (Hue 208 to 227)
            else if (hue > 207 && hue <= 227) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Delphinium Blue";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Sky Blue";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Pale Sky Blue";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Royal Blue";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Royal Blue";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Royal Blue";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Dark Royal Blue";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Royal Blue";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Royal Blue";
                }
            }
            //Blue Area (Hue 228 to 253)
            else if (hue > 228 && hue <= 253) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Light Blue";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Cornflower";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Forget-Me-Not";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Blue";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Blue";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Lilac";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Dark Blue";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Blue";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Lilac";
                }
            }
            //Dioxazine Area (Hue 254 to 273)
            else if (hue > 253 && hue <= 273) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Dark Lavender";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Lavender";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Rose De France";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Han Purple";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Lavender";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Lavender";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Dioxazine";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Lavender";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Lavender";
                }
            }
            //Violet Area (Hue 274 to 285)
            else if (hue > 273 && hue <= 285) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Violet";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Kunzite";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Mauve";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Dark Violet";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Violet";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Violet";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Spectral Violet";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Violet";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Violet";
                }
            }
            //Aniline Area (Hue 286 to 295)
            else if (hue > 285 && hue <= 295) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Purple Daisy";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Rose Of Sharon";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Lilac";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Aniline";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Primrose";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Primrose";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Amethyst";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Primrose";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Primrose";
                }
            }
            //Magenta Area (Hue 296 to 305)
            else if (hue > 295 && hue <= 305) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Dark Magenta";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Magenta";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Musk";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Light Purple";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Magenta";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Magenta";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Purple";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Magenta";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Magenta";
                }
            }
            //Bougainvillea Area (Hue 306 to 315)
            else if (hue > 305 && hue <= 315) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Shocking Pink";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Purple Loosestrife";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Doge Rose";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Purple Bougainvillea";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Pink";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Pink";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Purple Bean";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Blue Pink";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Blue Pink";
                }
            }
            //Pink Area (Hue 316 to 326)
            else if (hue > 315 && hue <= 326) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Dark Pink";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Pink";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Light Pink";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Prickly Pear";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Shocking Pink";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Shocking Pink";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Elderberry";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Shocking Pink";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Ute Shocking Pink";
                }
            }
            //Red-Plum Area (Hue 327 to 347)
            else if (hue > 326 && hue <= 347) {
                //Get saturation and value range
                if (saturation>=83 && value >=83){
                    ColourName ="Dragon Fruit";
                }else if (saturation>=50 && value >=83){
                    ColourName ="Pink Hydrangea";
                }else if (saturation>=5 && value >=83){
                    ColourName ="Baby Pink";
                }else if (saturation>=83 && value >=50){
                    ColourName ="Chinese Strawberry";
                }else if (saturation>=50 && value >=50){
                    ColourName ="Red Plum";
                }else if (saturation>=5 && value >=50){
                    ColourName ="Light Red Plum";
                }else if (saturation>=83 && value >=10){
                    ColourName ="Red Plum";
                }else if (saturation>=50 && value >=10){
                    ColourName ="Dark Red Plum";
                }else if (saturation>=5 & value >=10){
                    ColourName ="Grey Red Plum";
                }
            }
            else {ColourName ="Undefined Colour Name ";
            Log.e("Colour Name","Colour Range not found. Error in Matrix");}
        }
        Log.i("ColourName", ColourName);
        return ColourName;
    }


    public void LoadTheme(int Code){
        switch(Code) {
            case 0:
                this.setTheme(R.style.AppTheme);
                Log.d("Themeloading_Main","Default");
                break;
            case 1:
                this.setTheme(R.style.FirstTheme);
                Log.d("Themeloading_Main","First");
                break;
            case 2:
                this.setTheme(R.style.SecondTheme);
                Log.d("Themeloading_Main","Second");
                break;
            case 3:
                this.setTheme(R.style.ThirdTheme);
                Log.d("Themeloading_Main","Third");
                break;

        }
    }

    public int getDefault(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean Appdefault = sharedPreferences.getBoolean("Standard", false);
        boolean FirstTheme = sharedPreferences.getBoolean("First", false);
        boolean SecondTheme = sharedPreferences.getBoolean("Second", false);
        boolean ThirdTheme = sharedPreferences.getBoolean("Third", false);

        if (Appdefault){
            return 0;
        } else if (FirstTheme){
            return 1;
        } else if (SecondTheme){
            return 2;
        } else if (ThirdTheme){
            return 3;
        } else{
            return 0;
        }
    }

// Colour Name with Database (NOT FINAL)
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