package com.example.moviesapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UpcomingFragment extends Fragment implements RequestOperator.RequestOperatorListener {

    private String title;
    private Button viewAll;
    private ViewPager2 viewPagerThis;
    private ViewPager2 viewPagerNext;
    private String upcomingUrl = "https://api.themoviedb.org/3/movie/upcoming?api_key=6388d65378b5671a4bb1849e475856bb&language=en-US&page=1";
    List<Movie> lstMovie;
    private Handler sliderHandler = new Handler();
    public UpcomingFragment(String title) {
        this.title = title;
    }
    List<SliderItems> sliderItems;
    List<SliderItems> sliderItemsNext;
    SliderAdapter adapter;
    SliderAdapter adapterNext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        lstMovie = new ArrayList<>();
        viewAll = (Button) view.findViewById(R.id.viewAll);
        viewPagerThis = (ViewPager2)view.findViewById(R.id.viewPagerThisYear);
        viewPagerNext = (ViewPager2)view.findViewById(R.id.viewPagerNextYear);
        viewAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new HomeFragment(""));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        sliderItems = new ArrayList<>();
        adapter = new SliderAdapter(sliderItems,viewPagerThis);
        viewPagerThis.setAdapter(adapter);
        sliderItemsNext = new ArrayList<>();
        adapterNext = new SliderAdapter(sliderItemsNext,viewPagerThis);
        viewPagerNext.setAdapter(adapterNext);
        viewPagerThis.setClipToPadding(false);
        viewPagerThis.setClipChildren(false);
        viewPagerThis.setOffscreenPageLimit(3);
        viewPagerThis.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPagerNext.setClipToPadding(false);
        viewPagerNext.setClipChildren(false);
        viewPagerNext.setOffscreenPageLimit(3);
        viewPagerNext.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        try {
            setRequest(new URL(upcomingUrl));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {

                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPagerThis.setPageTransformer(compositePageTransformer);
        viewPagerThis.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override

            public void onPageSelected(int position) {

                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000); // slide duration 2 seconds

            }
        });
        viewPagerNext.setPageTransformer(compositePageTransformer);
        viewPagerNext.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override

            public void onPageSelected(int position) {

                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000); // slide duration 2 seconds
            }
        });
        return view;
    }
    private Runnable sliderRunnable = new Runnable() {

        @Override
        public void run() {
            viewPagerThis.setCurrentItem(viewPagerThis.getCurrentItem()+1);
            viewPagerNext.setCurrentItem(viewPagerNext.getCurrentItem()+1);
        }
    };
    @Override
    public void success(ArrayList items) {
        sliderItems.clear();
        sliderItemsNext.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < items.size(); i++) {
            Movie movie = (Movie)items.get(i);
            try {
                if(dateFormat.parse(movie.getReleaseDate()).compareTo(new Date()) < 0) {
                    //Log.e("Date:", String.valueOf(new Date()));
                    sliderItemsNext.add(new SliderItems(movie.getImageUrl()));
                } else {
                    sliderItems.add(new SliderItems(movie.getImageUrl()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapterNext.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void failed(int responseCode) {
    }

    public void setRequest(URL url) {
        RequestOperator ro = new RequestOperator(url, getActivity().getApplicationContext(), 1);
        ro.setListener(this);
        ro.start();
    }
}