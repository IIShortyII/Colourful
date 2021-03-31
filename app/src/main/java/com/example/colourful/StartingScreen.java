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


static int ASK_PERMISSION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadTheme(getDefault());
        setContentView(R.layout.activity_starting_screen);


        if(Build.VERSION.SDK_INT >=23){
        getPermission();
        }else { (new Handler()).postDelayed(this::goToMainScreen,2000);}
    }

    private void getPermission(){
        if(ContextCompat.checkSelfPermission(StartingScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(StartingScreen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(StartingScreen.this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){

            //All Access Start Application
            //Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show();
            (new Handler()).postDelayed(this::goToMainScreen,2000);


        }else{ getAccess();
        }

    }











//-----Subroutines----

    public void goToMainScreen(){
        Intent goToMain = new Intent(StartingScreen.this, MainActivity.class);
        startActivity(goToMain);
        finish();
    }


    private void getAccess(){
        if(//Check if already asked
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
        { new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("Camera and File Access is needed for correct functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(StartingScreen.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},ASK_PERMISSION);
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
            ActivityCompat.requestPermissions(StartingScreen.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},ASK_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ASK_PERMISSION) {
            if(ContextCompat.checkSelfPermission(StartingScreen.this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(StartingScreen.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(StartingScreen.this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {

                //All Access Start Application
                //Toast.makeText(this, "Access Granted2", Toast.LENGTH_SHORT).show();
                goToMainScreen();



            }else{
                Toast.makeText(this, "Please allow all Permission", Toast.LENGTH_SHORT).show();
                goToMainScreen();
            }
        }
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



    //---------------END OF CODE---------------------
}