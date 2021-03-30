package com.example.colourful;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends AppCompatActivity {

    //Create Buttons
    Button Apply;
    RadioGroup ColourTheme;
    RadioButton Standard;
    RadioButton First;
    RadioButton Second;
    RadioButton Third;
    int Themecode;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LoadTheme(getDefault());
        setContentView(R.layout.activity_settings);

        Apply = findViewById(R.id.btn_Apply);
        ColourTheme = findViewById(R.id.rdG_ColourTheme);
        Standard = findViewById(R.id.rdb_Standard);
        First = findViewById(R.id.rdb_FirstOption);
        Second = findViewById(R.id.rdb_SecondOption);
        Third = findViewById(R.id.rdb_ThirdOption);

        Standard.setOnCheckedChangeListener(new Radio_check());
        First.setOnCheckedChangeListener(new Radio_check());
        Second.setOnCheckedChangeListener(new Radio_check());
        Third.setOnCheckedChangeListener(new Radio_check());

        loadRadioButtons();

        Apply.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                saveRadioButtons();
                Intent reset = new Intent(getApplicationContext(),Settings.class);
                startActivity(reset);
                Intent goBack = new Intent(Settings.this, MainActivity.class);
                startActivity(goBack);
                finish();
            }
        });
    }

    public class Radio_check implements CompoundButton.OnCheckedChangeListener{


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (Standard.isChecked()){
                Log.d("Radio","Standard");
                Themecode = 0;
                //saveRadioButtons();
            }
            else if (First.isChecked()){
                Log.d("Radio","First");
                Themecode = 1;
                //saveRadioButtons();
            }
            else if (Second.isChecked()){
                Log.d("Radio","Second");
                Themecode = 2;
                //saveRadioButtons();
            }
            else if (Third.isChecked()){
                Log.d("Radio","Third");
                Themecode = 3;
                //saveRadioButtons();
            }
        }
    }

    public void LoadTheme(int Code){
        switch(Code) {
            case 0:
                this.setTheme(R.style.AppTheme);
                Log.d("Themeloading","Default");
                break;
            case 1:
                this.setTheme(R.style.FirstTheme);
                break;
            case 2:
                this.setTheme(R.style.SecondTheme);
                break;
            case 3:
                this.setTheme(R.style.ThirdTheme);
                break;

        }
    }

    public int getDefault(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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



    public void saveRadioButtons(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Standard", Standard.isChecked());
        editor.putBoolean("First", First.isChecked());
        editor.putBoolean("Second", Second.isChecked());
        editor.putBoolean("Third", Third.isChecked());
        editor.apply();
    }

    public void loadRadioButtons(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Standard.setChecked(sharedPreferences.getBoolean("Standard", false));
        First.setChecked(sharedPreferences.getBoolean("First", false));
        Second.setChecked(sharedPreferences.getBoolean("Second", false));
        Third.setChecked(sharedPreferences.getBoolean("Third", false));

    }





    //End Of Code
}