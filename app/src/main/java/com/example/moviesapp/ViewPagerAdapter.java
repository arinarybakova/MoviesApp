package com.example.moviesapp;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    //private String data [] = new String[]{"Oscar", "Golden"};
    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @Override
    public Fragment createFragment(int position) {
        return new UpcomingFragment("");
    }
    @Override
    public int getItemCount() {
        return 3;
    }
    /*public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment(data[position]);
            case 1:
                return new UpcomingFragment(data[position]);
            case 2:
                return new AwardFragment(data[position]);
            default:
                return null;
        }
    }*/



}
