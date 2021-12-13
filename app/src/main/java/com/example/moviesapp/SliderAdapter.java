package com.example.moviesapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItems> sliderItems;
    private ViewPager2 viewPager2;


    SliderAdapter(List<SliderItems> sliderItems, ViewPager2 viewPager2) {

        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SliderViewHolder(

                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_item_container, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {

        holder.setImage(sliderItems.get(position));

        if (position == sliderItems.size()- 2){
            viewPager2.post(runnable);
        }
    }

    @Override

    public int getItemCount() {

        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;

        SliderViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(SliderItems sliderItems){

            getBitmap(sliderItems.getImageUrl(), imageView);
        }
    }
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public void getBitmap(@Nullable String url, ImageView image) {
        try {
            Activity a=(Activity)image.getContext();
            a.runOnUiThread(new SliderAdapter.ImageDownloaderTask(new URL(url), image));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static class ImageDownloaderTask extends AsyncTask<URL,Void, Bitmap> implements Runnable {
        private URL url;
        private ImageView imageView;

        public ImageDownloaderTask(@Nullable URL url,  @Nullable ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected synchronized Bitmap doInBackground(URL... urls) {
            Bitmap bitmap = null;
            try {
                InputStream input = urls[0].openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected synchronized void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        @Override
        public void run() {
            this.execute(this.url);
        }
    }
}