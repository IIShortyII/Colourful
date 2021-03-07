package com.example.colourful.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ColourNameDAO {

    //GET ColourName
    @Query("SELECT * FROM ColourName WHERE HEX_Code = (:HEX_CODE)")
    List<ColourName> findColourName(String HEX_CODE);

    //GET all Entries
    @Query("SELECT * FROM ColourName")
    List<ColourName> findAll();


    @Insert
    void insertColour(ColourName ColourName);



}
