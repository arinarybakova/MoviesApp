package com.example.moviesapp;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieActivity extends AppCompatActivity {
    List<Movie> lstMovie;
    TextView titleTextView;
    TextView descriptionTextView;
    ImageView imageView;
    ImageButton changePic;
    Movie movie;
    private String currentPhotoPath;
    private long movieId;
    private TextView releaseDateTextView;
    private RatingBar rating;
    private TextView ratingTextView;
    private AppDatabase db;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);
        Intent intent = getIntent();

        movieId = intent.getLongExtra("movieId", -1);
        db = AppActivity.getDatabase(getApplicationContext());
        movie = db.movieDAO().getMovie(movieId);
        String ratingText= String.valueOf(movie.getRating());
        titleTextView = (TextView)findViewById(R.id.title);
        descriptionTextView = (TextView)findViewById(R.id.description);
        imageView = (ImageView) findViewById(R.id.moviesImage);
        releaseDateTextView = (TextView) findViewById(R.id.releaseDate);
        rating = (RatingBar)findViewById(R.id.rating);
        ratingTextView = (TextView) findViewById(R.id.ratingText);
        titleTextView.setText(movie.getTitle());
        descriptionTextView.setText(movie.getDescription());
        releaseDateTextView.setText(movie.getReleaseDate());
        ratingTextView.setText(String.valueOf(movie.getRating()));

        File file = new File(movie.getPhotoPath());
        if(movie.getPhotoPath() != "" && file.exists()) {
            // Get the dimensions of the View
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(movie.getPhotoPath(), bmOptions);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int targetW = imageView.getWidth() != 0 ? imageView.getWidth() : photoW;
            int targetH = imageView.getHeight() != 0 ? imageView.getHeight() : photoH;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(movie.getPhotoPath(), bmOptions);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        } else {
            getBitmap(movie.getImageUrl(), movieId, imageView);
        }

        ArrayList<Movie> movies = new ArrayList<>();
        movies.add(movie);

        // setting part of the stars in the rating bar depending on the ratingText
        float ratingValue = (float) (Float.parseFloat(ratingText));
        if(ratingValue == 0) {
            rating.setVisibility(View.GONE);
        } else {
            rating.setRating(Float.parseFloat(ratingText));
        }


        changePic = (ImageButton) findViewById(R.id.changePic);
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 110);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void setPic() throws IOException {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        File storageDir = getApplicationContext().getFilesDir();
//        File imageBackground = File.createTempFile(
//                "background",  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
        File imageBackground = new File(storageDir.getAbsolutePath() + "/background.jpg");
        FileInputStream inStream = new FileInputStream(currentPhotoPath);
        FileOutputStream outStream = new FileOutputStream(imageBackground.getAbsolutePath());
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
        Log.i("Background file", imageBackground.getAbsolutePath());
        imageView.setImageBitmap(bitmap);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
            try {
                setPic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        movie.setPhotoPath(currentPhotoPath);
        db.movieDAO().update(movie);

        return image;
    }

    public void getBitmap(@Nullable String url, long movieId, ImageView image) {
        try {
            Activity a=(Activity)image.getContext();
            a.runOnUiThread(new RecyclerViewAdapter.ImageDownloaderTask(new URL(url), movieId, image));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    //Todo: write methods to implement next movie and previous movie switch
    public Object getNext(String uid) {
        int idx = lstMovie.indexOf(uid);
        if (idx < 0 || idx+1 == lstMovie.size()) return "";
        return lstMovie.get(idx + 1);
    }
    public Object getPrevious(String uid) {
        int idx = lstMovie.indexOf(uid);
        if (idx <= 0) return "";
        return lstMovie.get(idx - 1);
    }
    @Override
    //Todo: add method to add movies to the favorites list
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                // do something
                //getNext(lstMovie.indexOf());
                return true;
            case R.id.action_previous:
                // do something
                //getPrevious();
                return true;
            case R.id.action_favorite:
                // do something
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
