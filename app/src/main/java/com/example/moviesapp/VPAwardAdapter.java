package com.example.moviesapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VPAwardAdapter extends FragmentStateAdapter {

    private String[] titles = new String[] {"Oscars", "Golden Globes"};

    public VPAwardAdapter(@NonNull FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    public VPAwardAdapter(AwardFragment awardFragment) {
        super(awardFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OscarsFragment();
            case 1:
                return new GoldenFragment();
        }
        return new OscarsFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
    /*private final Fragment[] mFragments = new Fragment[] {//Initialize fragments views
        //Fragment views are initialized like any other fragment (Extending Fragment)
            new OscarsFragment(),//First fragment to be displayed within the pager tab number 1
            new GoldenFragment(),
    };
    public final String[] mFragmentNames = new String[] {//Tabs names array
            "Oscars",
            "Golden Globes"
    };*/

    /*public VPAwardAdapter(FragmentActivity fa){//Pager constructor receives Activity instance
        super(fa);
    }

    @Override
    public int getItemCount() {
        return mFragments.length;//Number of fragments displayed
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments[position];
    }*/
}
