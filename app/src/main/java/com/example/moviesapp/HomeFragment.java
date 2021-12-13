package com.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements RequestOperator.RequestOperatorListener {


    private String title;
    ArrayList lstMovie;
    ArrayList newlstMovie;
    private String movieUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=6388d65378b5671a4bb1849e475856bb";
    RecyclerViewAdapter myAdapter;
    RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private ImageButton favBtn;
    private Button buttonNext;
    private Button buttonPrev;
    private int apiPage = 1;
    private int page = 1;
    private int totalMovies;
    private int movieLimit = 10;
    private AppDatabase db;
    private boolean sortByTitle = false;
    private boolean isFiltered = false;
    private int filterType;
    private String filterQuery;
    Button nextPage;
    Button prevPage;

    public HomeFragment(String title) {
        this.title = title;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        lstMovie = new ArrayList<Movie>();


        try {
            setRequest(new URL(movieUrl));
        } catch(Exception e) {

        }
        db = AppActivity.getDatabase(getActivity().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.recyclerviewlistfooter,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_movies);
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_container);
        setHasOptionsMenu(true);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_movies);
        myAdapter = new RecyclerViewAdapter(getActivity(), lstMovie, false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(myAdapter);

        prevPage = view.findViewById(R.id.buttonPrev);
        prevPage.setVisibility(View.INVISIBLE);
        nextPage = view.findViewById(R.id.buttonNext);


        nextPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                nextPage();
            }
        });

        prevPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                prevPage();
            }
        });


        return view;
    }

    public void nextPage() {
        page++;
        if(!isFiltered) {
            String url = movieUrl + "&page=" + page;
            try {
                setRequest(new URL(url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            getMoviesFromDb();
        }
    }

    public void prevPage() {
        page--;
        getMoviesFromDb();
    }

    private void togglePagination() {
        if(page == 1) {
            prevPage.setVisibility(View.INVISIBLE);
        } else {
            prevPage.setVisibility(View.VISIBLE);
        }

        if(isFiltered) {
            Log.i("Total movies", String.valueOf(totalMovies));
            if(page * movieLimit > totalMovies) {
                nextPage.setVisibility(View.INVISIBLE);
            } else {
                nextPage.setVisibility(View.VISIBLE);
            }
        }
    }
    // This method receives the current app bar menu and a MenuInflater as parameters.
    // Use the menu inflater to create an instance of your fragment's menu, and then merge it into the current menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void filter(String text) {
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Type in your text", Toast.LENGTH_SHORT).show();
        } else {
            isFiltered = true;
            filterQuery = text;
            page = 1;
            lstMovie.clear();
            //jei filtruoja pagal reitinga tai type yra 0, jei pagal pavadinima type = 1
            if(isNumeric(text)) {
                filterType = 0;
                lstMovie.addAll(db.movieDAO().filterMoviesByRating(Double.parseDouble(text), Double.parseDouble(text) + 1, 0, movieLimit));
                totalMovies = db.movieDAO().totalFilterMoviesByRating(Double.parseDouble(text), Double.parseDouble(text) + 1);
            } else {
                filterType = 1;
                lstMovie.addAll(db.movieDAO().filterMovies("%" + text + "%", 0, movieLimit));
                totalMovies = db.movieDAO().totalFilterMovies("%" + text + "%");
            }
            togglePagination();
            myAdapter.filterList(lstMovie);
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void success(ArrayList items) {
        lstMovie = items;
        updateList();
    }

    @Override
    public void failed(int responseCode) {
    }

    public void setRequest(URL url) {
        RequestOperator ro = new RequestOperator(url, getActivity().getApplicationContext(), 0);
        ro.setListener(this);
        ro.start();
    }

    public void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getMoviesFromDb();
            }
        });
    }

    public void getMoviesFromDb() {
        lstMovie.clear();
        if(!isFiltered) {
            lstMovie.addAll(db.movieDAO().getMovies(movieLimit * (page - 1), movieLimit));
            totalMovies = db.movieDAO().getTotalMovies();
        } else {
            if(filterType == 0) {
                lstMovie.addAll(db.movieDAO().filterMoviesByRating(Double.parseDouble(filterQuery), Double.parseDouble(filterQuery) + 1, movieLimit * (page - 1), movieLimit));
            } else {
                lstMovie.addAll(db.movieDAO().filterMovies("%" + filterQuery + "%", movieLimit * (page - 1), movieLimit));
            }
        }

        myAdapter.filterList(lstMovie);
        togglePagination();
    }
}