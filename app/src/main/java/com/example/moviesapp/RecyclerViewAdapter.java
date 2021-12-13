package com.example.moviesapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private Context mContext;
    private ArrayList<Movie> mData;
    private ArrayList<Movie> moviesListFiltered;
    private HomeFragment mSeriesListener;
    private AppDatabase db;
    private Boolean removeMovie;
    int position = 1;
    int lastPosition = -1; // the last position that was displayed
    public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    private BitmapDrawable bitmapDrawable;
    private Bitmap bitmap1;

    public RecyclerViewAdapter(Context mContext, ArrayList<Movie> mData, Boolean removeMovie) {
        this.mContext = mContext;
        this.mData = mData;
        this.removeMovie = removeMovie;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_movie, parent, false);
        return new MyViewHolder(view);

    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Movie movie = (Movie)mData.get(position);

        if(holder instanceof MyViewHolder) {
            if(!removeMovie) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_item);
                holder.itemView.startAnimation(animation);
            }
            lastPosition = holder.getAdapterPosition();
            //Todo: SET IMAGE TAKEN BY CAMERA ON THE BACKGROUND FOR 3 SEC AFTER IT REMOVE IT

            holder.tv_movie_title.setText(movie.getTitle());
            holder.tv_movie_rating.setText(String.valueOf(movie.getRating()));
            if (movie.getRating() == 0) {
                holder.tv_movie_rating.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_movie_rating.setVisibility(View.VISIBLE);
            }
            if(movie.getFavorite()) {
                holder.btnFav.setImageResource(R.drawable.baseline_favorite_black_24);
            } else {
                holder.btnFav.setImageResource(R.drawable.baseline_favorite_border_black_24);
            }
            File file = new File(movie.getPhotoPath());
            if(movie.getPhotoPath() != "" && file.exists()) {
                // Get the dimensions of the View
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(movie.getPhotoPath(), bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                int targetW = holder.img_movie_thumbnail.getWidth() != 0 ? holder.img_movie_thumbnail.getWidth() : photoW;
                int targetH = holder.img_movie_thumbnail.getHeight() != 0 ? holder.img_movie_thumbnail.getHeight() : photoH;

                // Determine how much to scale down the image
                int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(movie.getPhotoPath(), bmOptions);
                holder.img_movie_thumbnail.setImageBitmap(bitmap);
                holder.img_movie_thumbnail.setVisibility(View.VISIBLE);
            } else {
                getBitmap(movie.getImageUrl(), movie.getMovieId(), holder.img_movie_thumbnail);
            }
        }
        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movie.getFavorite() == true) {
                    movie.setFavorite(false);
                    holder.btnFav.setImageResource(R.drawable.baseline_favorite_border_black_24);
                    if(removeMovie) {
                        mData.remove(movie);
                        notifyDataSetChanged();
                    }
                    Toast.makeText(mContext.getApplicationContext(), "Movie '" + movie.getTitle() + "' was removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    movie.setFavorite(true);
                    holder.btnFav.setImageResource(R.drawable.baseline_favorite_black_24);
                    Toast.makeText(mContext.getApplicationContext(), "Movie '" + movie.getTitle() + "' was added to favorites", Toast.LENGTH_SHORT).show();
                    // share on social media
                    /*Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "I added movie '" + movie.getTitle() + "' to favorites");
                    mContext.startActivity(Intent.createChooser(intent, "Share favorites"));*/
                    // getting from image view
                    bitmapDrawable = (BitmapDrawable) holder.img_movie_thumbnail.getDrawable();
                    bitmap1 = bitmapDrawable.getBitmap();
                    String imgBitmapPath = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap1, "title", null);
                    Uri imgBitmapUri = Uri.parse(imgBitmapPath);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    /*shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);*/
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "I added movie '" + movie.getTitle() + "' to favorites");
                    mContext.startActivity(Intent.createChooser(shareIntent, "Share title + image"));

                    // try save pic
                    BitmapDrawable draw = (BitmapDrawable) holder.img_movie_thumbnail.getDrawable();
                    Bitmap bitmap = draw.getBitmap();

                    FileOutputStream outStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
                    dir.mkdirs();
                    String fileName = String.format("%d.jpg", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);
                    try {
                        outStream = new FileOutputStream(outFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    try {
                        outStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outFile));
                    mContext.sendBroadcast(intent);
                }
               db = AppActivity.getDatabase(view.getContext());
               db.movieDAO().update(movie);
            }
        });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                //setting click listener for a cardview item click
                @Override
                public void onClick(View v){


                    //Log.e("movie", movie.getTitle());
                    // Log.e("movie", movie.getImageUrl());
                    // Log.e("rating", String.valueOf(movie.getRating()));
                    // passing data to the home fragment
                    //Bundle bundle = new Bundle();
                    Intent intent = new Intent(v.getContext(), MovieActivity.class);
                    intent.putExtra("movieId", movie.getMovieId());
                    // start the activity
                    mContext.startActivity(intent);

                   /* long itemId = movie.getMovieId();
                    mData.remove(itemId);
                    if (itemId != 0){
                        holder.cardView.setVisibility(View.INVISIBLE);
                        //mData.notifyItemRemoved();
                        notifyItemRangeChanged(position,mData.size());
                        Toast.makeText(mContext,"Removed : " + itemId,Toast.LENGTH_SHORT).show();
                    }*/

                }
            });
        /*private void removeItem(int position) {
            int newPosition = holder.getAdapterPosition();
            mData.remove(newPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition, mData.size());
        }*/
    }

    // method for filtering our recyclerview items.
    public void filterByTitle(ArrayList<Movie> filterByTitle) {
        // below line is to add our filtered
        // list in our moview array list.
        mData = filterByTitle;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void filterList(ArrayList<Movie> filteredList){
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void setFilter(ArrayList<Movie> newList){
        mData = new ArrayList<>();
        mData.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_movie_title;
        TextView tv_movie_rating;
        ImageView img_movie_thumbnail;
        ImageButton btnFav;
        CardView cardView;

        public MyViewHolder(View itemView){
            super(itemView);
            tv_movie_title = (TextView) itemView.findViewById(R.id.movie_title);
            img_movie_thumbnail = (ImageView) itemView.findViewById(R.id.movie_img);
            tv_movie_rating = (TextView) itemView.findViewById(R.id.rating);
            btnFav = (ImageButton) itemView.findViewById(R.id.favButton);
            cardView = (CardView) itemView.findViewById(R.id.cardView);

        }
    }

    public void getBitmap(@Nullable String url, long movieId, ImageView image) {
        try {
            Activity a=(Activity)image.getContext();
            a.runOnUiThread(new ImageDownloaderTask(new URL(url), movieId, image));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static class ImageDownloaderTask extends AsyncTask<URL,Void, Bitmap> implements Runnable {
        private URL url;
        private long movieId;
        private ImageView imageView;

        public ImageDownloaderTask(@Nullable URL url, long movieId, @Nullable ImageView imageView) {
            this.url = url;
            this.movieId = movieId;
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
            imageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void run() {
            this.execute(this.url);
        }
    }

}
