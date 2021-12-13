package com.example.moviesapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MovieDAO {
    @Insert
    void insert(Movie movie);

    @Update
    void update(Movie movie);

    @Query("SELECT * FROM Movie LIMIT :count OFFSET :offset")
    List<Movie> getMovies(int offset, int count);

    @Query("SELECT * FROM Movie WHERE favorite = '1' LIMIT :count OFFSET :offset")
    List<Movie> getFavoriteMovies(int offset, int count);

    @Query("SELECT COUNT(*) FROM Movie")
    int getTotalMovies();

    @Query("SELECT COUNT(*) FROM Movie WHERE favorite = '1'")
    int getTotalFavoriteMovies();

    @Query("SELECT * FROM Movie WHERE movieId = :movieId")
    Movie getMovie(long movieId);

    @Query("SELECT * FROM Movie WHERE title LIKE :text LIMIT :count OFFSET :offset")
    List<Movie> filterMovies(String text, int offset, int count);

    @Query("SELECT * FROM Movie WHERE rating >= :ratingFrom AND rating < :ratingTo ORDER BY releaseDate DESC LIMIT :count OFFSET :offset")
    List<Movie> filterMoviesByRating(double ratingFrom, double ratingTo, int offset, int count);

    @Query("SELECT * FROM Movie WHERE title LIKE :text  AND favorite = '1' LIMIT :count OFFSET :offset")
    List<Movie> filterFavoriteMovies(String text, int offset, int count);

    @Query("SELECT * FROM Movie WHERE rating >= :ratingFrom AND rating < :ratingTo AND favorite = '1' ORDER BY releaseDate DESC LIMIT :count OFFSET :offset")
    List<Movie> filterFavoriteMoviesByRating(double ratingFrom, double ratingTo, int offset, int count);

    @Query("SELECT COUNT(*) FROM Movie WHERE title LIKE :text")
    int totalFilterMovies(String text);

    @Query("SELECT COUNT(*)FROM Movie WHERE rating >= :ratingFrom AND rating < :ratingTo")
    int totalFilterMoviesByRating(double ratingFrom, double ratingTo);

    @Query("SELECT COUNT(*) FROM Movie WHERE title LIKE :text AND favorite = '1'")
    int totalFilterFavoriteMovies(String text);

    @Query("SELECT COUNT(*)FROM Movie WHERE rating >= :ratingFrom AND rating < :ratingTo AND favorite = '1'")
    int totalFilterFavoriteMoviesByRating(double ratingFrom, double ratingTo);
}
