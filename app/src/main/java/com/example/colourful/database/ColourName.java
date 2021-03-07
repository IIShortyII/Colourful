package com.example.colourful.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ColourName{
    @PrimaryKey @NonNull
    public String HEX_Code;

    @ColumnInfo(name ="ColourName")
    public String ColourName;


    public ColourName(String HEX_Code, String ColourName) {
        this.HEX_Code = HEX_Code;
        this.ColourName = ColourName;
    }
}
