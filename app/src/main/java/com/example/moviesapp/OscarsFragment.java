package com.example.moviesapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class OscarsFragment extends Fragment {

    ArrayList<Movie> lstMovie;
    RecyclerViewAdapter myAdapter;
    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_oscars, container, false);
        createMovieList();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_oscars);
        myAdapter = new RecyclerViewAdapter(getActivity(), lstMovie, false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recyclerView.setAdapter(myAdapter);
        return view;
    }
    private void createMovieList(){
       /* lstMovie = new ArrayList<>();
        lstMovie.add(new Movie("Title 1", "Category Movie", "Description movie1", "9.2", R.drawable.tomorrowwar));
        lstMovie.add(new Movie("Title 2", "Category Movie", "Description movie2", "9.6", R.drawable.cinderella_));
        lstMovie.add(new Movie("Title 3", "Category Movie", "Description movie3", "8.5",  R.drawable.spangebob));
        lstMovie.add(new Movie("Title 4", "Category Movie", "Description movie4", "7.5", R.drawable.jamie));
        lstMovie.add(new Movie("Title 5", "Category Movie", "Description movie1", "7.5",  R.drawable.tomorrowwar));
        lstMovie.add(new Movie("Title 6", "Category Movie", "Description movie2", "7.5", R.drawable.cinderella_));
        lstMovie.add(new Movie("Title 7", "Category Movie", "Description movie3","7.5", R.drawable.spangebob));
        lstMovie.add(new Movie("Title 8", "Category Movie", "Description movie4","7.5", R.drawable.jamie));
        lstMovie.add(new Movie("Title 8", "Category Movie", "Description movie4","8.5", R.drawable.jamie));*/
    }
}