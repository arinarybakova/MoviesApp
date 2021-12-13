package com.example.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Random;

@Entity
public class Movie implements Parcelable{

    @PrimaryKey
    private long movieId;
    private String title;
    private String releaseDate;
    private String description;
    private String imageUrl;
    private Boolean favorite;
    private String photoPath;
    private double rating;

    public Movie(String title, long movieId, String releaseDate,double rating, String description, String imageUrl) {
        this.title = title;
        this.movieId = movieId;
        this.releaseDate = releaseDate;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.favorite = false;
        this.photoPath = "";
    }
    @Ignore
    public Movie(){
        Random rand = new Random();
        this.movieId = rand.nextInt();
        this.title = "Test title";
        this.releaseDate = "N/A";
        this.description = "Test description";
        this.rating = 0;
        this.favorite = false;
        this.photoPath = "";
    }
    @Ignore
    protected Movie(Parcel in) {
        this.title = in.readString();
        this.movieId = in.readLong();
        this.releaseDate = in.readString();
        this.description = in.readString();
        this.rating = in.readDouble();
    }
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeLong(this.movieId);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.description);
        parcel.writeDouble(this.rating);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(@NonNull long movieId) {
        this.movieId = movieId;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

}
