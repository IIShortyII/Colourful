package com.example.colourful;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CVD_Test extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    int counter = 1;
    int Ans;
    int right = 0;
    ArrayList<Integer> Solutions = new ArrayList<Integer>();
    ImageView Testplates;
    TextView Description;
    TextView Result;
    EditText Answer;
    Button goToSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadTheme(getDefault());                            //Loading chosen Theme
        setContentView(R.layout.activity_cvd_test);

        Description = findViewById(R.id.txt_TopDesc);
        Testplates = findViewById(R.id.imgV_Testplates);
        Answer = findViewById(R.id.inp_answer);
        Result = findViewById(R.id.txt_Results);
        goToSettings = findViewById(R.id.btn_GoToSettings);

        goToSettings.setVisibility(View.INVISIBLE);
        goToSettings.setEnabled(false);
        goToSettings.setFocusable(false);

        populateSolutions();

        Testplates.setImageResource(nextPlate());
        TestAlert();

        Description.setText("Write down the Number you can see \n  (If you cant see a number write 0)");


        Answer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("counter", "" + counter);

                    try {
                        Ans = Integer.parseInt(Answer.getText().toString());

                    } catch (NumberFormatException e) {
                        Ans = 100;
                    }

                    if (checkAnswer(Ans, counter)) {
                        right++;
                    }

                    counter++;
                    Testplates.setImageResource(nextPlate());
                    Answer.setText("");

                    if (counter > 25) {
                        Description.setText("Write down how many lines you can see");
                    }
                    if (counter == 39) {
                        Description.setText("Result");
                        Answer.setEnabled(false);
                        Answer.setFocusable(false);
                        Answer.setVisibility(View.INVISIBLE);

                        goToSettings.setVisibility(View.VISIBLE);
                        goToSettings.setEnabled(true);
                        goToSettings.setFocusable(true);
                        setUpSecondTime();
                        if (right < 6) {//Possible Colourblind
                            Result.setText("You could be Colourblind \n We suggest to select the Colourblind Theme");
                        } else if (right < 25) {//Some Kind of Red-Green CVD
                            Result.setText("You could have some kind of Red-Green CVD \n We suggest to select the Red-Green Theme");
                        } else if (right < 30) {//Possible Yellow-Blue CVD
                            Result.setText("You could have some kind of Yellow-Blue CVD \n We suggest to select the Yellow-Blue Theme");
                        } else { //Normal Colourvision
                            Result.setText("Normal Colour Vision \n We suggest to select the Standard Theme");
                        }
                    }
                }
                return false;
            }
        });

        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(CVD_Test.this, Settings.class);
                startActivity(goToSettings);
                finish();
            }
        });


    }











//-----Subroutines----
public void setUpSecondTime(){
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("FirstTime", false);
    editor.apply();}


    public boolean checkAnswer(int Ans, int Counter){
        int realcounter = Counter-1;
        if (Ans ==Solutions.get(realcounter)){
            return true;
        }else{return false;}
    }

    public void populateSolutions(){
        Solutions.add(12);
        Solutions.add(8);
        Solutions.add(6);
        Solutions.add(29);
        Solutions.add(57);
        Solutions.add(5);
        Solutions.add(3);
        Solutions.add(15);
        Solutions.add(74);
        Solutions.add(2);
        Solutions.add(6);
        Solutions.add(97);
        Solutions.add(45);
        Solutions.add(5);
        Solutions.add(7);
        Solutions.add(16);
        Solutions.add(73);
        Solutions.add(0);
        Solutions.add(0);
        Solutions.add(0);
        Solutions.add(0);
        Solutions.add(26);
        Solutions.add(42);
        Solutions.add(35);
        Solutions.add(96);
        Solutions.add(2);
        Solutions.add(2);
        Solutions.add(0);
        Solutions.add(0);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
        Solutions.add(1);
    }

    public int nextPlate(){
        int id = CVD_Test.this.getResources().getIdentifier("mipmap/ishi_plate_"+counter,null,CVD_Test.this.getPackageName());
        return id;
    }

    //First Alert
    public void TestAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("This test is not a substitute for a professional eye test by an ophthalmologist, " +
                        "nor is it representative of a diagnosis of any kind. If you suspect you to have CVD, consult an ophthalmologist.")
                .setPositiveButton("I understand ", (dialog, which) -> {

                }
                ).create().show();
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