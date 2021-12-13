package com.example.moviesapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class AwardFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String title;
    VPAwardAdapter vpAwardAdapter;
    private String[] titles = new String[] {"Oscars", "Golden Globes"};

    public AwardFragment(String title) {
        this.title = title;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_award, container, false);
        viewPager2 = (ViewPager2) view.findViewById(R.id.AwardViewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayoutAw);
        vpAwardAdapter = new VPAwardAdapter(this);
        viewPager2.setAdapter(vpAwardAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles[position]))).attach();
        return view;
    }
    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewPager2.setAdapter(new VPAwardAdapter(this));//Attach the adapter with our ViewPagerAdapter passing the host activity


        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.oscars);
                    break;
                case 1:
                    tab.setText(R.string.golden);
                    break;
            }
        }).attach();
    }*/

}