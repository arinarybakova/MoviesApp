package com.example.moviesapp;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class}, version = 51)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieDAO movieDAO();
}