package com.example.moviesapp;

import android.app.Application;
import androidx.room.Room;
import android.content.Context;

public class AppActivity extends Application {
    static AppDatabase db;

    public void onCreate() {
        super.onCreate();
    }

    public static AppDatabase getDatabase(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "movies_app_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        return db;
    }
}
