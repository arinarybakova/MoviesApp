package com.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogoActivity extends AppCompatActivity {

    private ImageView imgLogo;
    private ImageView shine;
    private Button buttonC;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        imgLogo = findViewById(R.id.imgLogo);
        buttonC = (Button)findViewById(R.id.buttonC);
        //shine = (ImageView)findViewById(R.id.shine);

        buttonC.setOnClickListener(startMainActivity);
        buttonC.setOnLongClickListener(startMainActivityLong);
        /*ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shineStart();
                    }
                });
            }
        },1,3, TimeUnit.SECONDS);*/

    }
    public void runMainActivity(boolean flag){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("flag", flag);
        context.startActivity(intent);
    }

    View.OnClickListener startMainActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            runMainActivity(true);
        }
    };
    /*to display list items*/
    View.OnLongClickListener startMainActivityLong = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) {
            runMainActivity(false);
            return true;
        }
    };
    private void shineStart(){
        Animation animation = new TranslateAnimation
                (0, (buttonC.getWidth()+shine.getWidth()),0, 0);
        animation.setDuration(550);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        shine.startAnimation(animation);
    }
}