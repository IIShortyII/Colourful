package com.example.colourful.database;

import android.content.Context;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ColourName.class}, version = 1)
public abstract class ColourNameDataBase extends RoomDatabase {
    private static final String LOG_TAG = ColourNameDataBase.class.getSimpleName();
    private static final Object LOCK = new Object();


    public abstract ColourNameDAO ColourNameDAO();

    private static ColourNameDataBase INSTANCE;

    public static ColourNameDataBase geTDbInstance(final Context context) {
        if (INSTANCE == null) {
            Log.i("DB MELDET", "BUILDER GESTARTET");
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ColourNameDataBase.class, "DB_NAME")
                    .allowMainThreadQueries()
            //.createFromAsset()
            .build();
        }
        return INSTANCE;
    }
}