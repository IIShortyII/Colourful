package com.example.colourful;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class StartingScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    static int ASK_PERMISSION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadTheme(getDefault());                            //Loading chosen Theme
        setContentView(R.layout.activity_starting_screen);



        //Check API Version before Asking for Permission
        //because in 22 and lower Access is automatically granted
        if (Build.VERSION.SDK_INT >= 23) {
            getPermission();
        } else {
            (new Handler()).postDelayed(this::goToMainScreen, 2000);
        }
    }




//-----Subroutines----

    public void setUpSecondTime(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("FirstTime", false);
        editor.apply();


    }
    public boolean checkFirstTime(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean FirstTime = sharedPreferences.getBoolean("FirstTime", true);
        return FirstTime;
    }

    //Checking if App has already Access, if yes jump to MainActivity
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            (new Handler()).postDelayed(this::goToMainScreen, 2000);


        } else {
            getAccess();
        }

    }
    //Jump to MainActivity
    public void goToMainScreen() {
        if (checkFirstTime()){
            //setUpSecondTime();
            Intent goToTest = new Intent(StartingScreen.this, CVD_Test.class);
            startActivity(goToTest);
        } else{
        Intent goToMain = new Intent(StartingScreen.this, MainActivity.class);
        startActivity(goToMain);
        }
        finish();
    }

    //getting Access for Camera and Storage
    private void getAccess() {
        if (//Check if already asked
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //Creating Alert when User have not give Permission at First Time, allows to give more detailed information about why Permission is needed
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Camera and File Access is needed for correct functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(StartingScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_PERMISSION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).create().show();
        } else {
            //Asking for Permission via Android Dialog Screen
            ActivityCompat.requestPermissions(StartingScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_PERMISSION);
        }
    }

    @Override
    //Checking after User Interaction if Permission is Granted or not
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ASK_PERMISSION) {
            if (ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(StartingScreen.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                goToMainScreen();


            } else {
                Toast.makeText(this, "Please allow all Permission", Toast.LENGTH_SHORT).show();
                goToMainScreen();
            }
        }
    }

    //Theme Loader
    public void LoadTheme(int Code) {
        switch (Code) {
            case 0:
                this.setTheme(R.style.AppTheme);
                Log.d("Themeloading_Main", "Default");
                break;
            case 1:
                this.setTheme(R.style.FirstTheme);
                Log.d("Themeloading_Main", "First");
                break;
            case 2:
                this.setTheme(R.style.SecondTheme);
                Log.d("Themeloading_Main", "Second");
                break;
            case 3:
                this.setTheme(R.style.ThirdTheme);
                Log.d("Themeloading_Main", "Third");
                break;

        }
    }
    //Getting chosen Theme from SharedPreferences
    public int getDefault() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean Appdefault = sharedPreferences.getBoolean("Standard", false);
        boolean FirstTheme = sharedPreferences.getBoolean("First", false);
        boolean SecondTheme = sharedPreferences.getBoolean("Second", false);
        boolean ThirdTheme = sharedPreferences.getBoolean("Third", false);

        if (Appdefault) {
            return 0;
        } else if (FirstTheme) {
            return 1;
        } else if (SecondTheme) {
            return 2;
        } else if (ThirdTheme) {
            return 3;
        } else {
            return 0;
        }
    }


    //---------------END OF CODE---------------------
}