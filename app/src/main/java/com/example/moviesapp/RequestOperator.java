package com.example.moviesapp;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RequestOperator extends Thread {
    public interface RequestOperatorListener {
        void success (ArrayList items);
        void failed(int responseCode);
    }

    private RequestOperatorListener listener;
    private int responseCode;
    private URL url;
    private int type;
    private Context context;

    public void setListener(RequestOperatorListener listener) {
        this.listener = listener;
    }

    public RequestOperator(URL url, Context context, int type) {
        this.url = url;
        this.context = context;
        this.type = type;
    }

    @Override
    public void run() {
        super.run();
        try {
            ArrayList<Movie> movies = request();
            if(movies != null) {
                success(movies);
            } else {
                failed(responseCode);
            }
        } catch(IOException e) {
            failed(-1);
        } catch(JSONException e) {
            failed(-2);
        }
    }

    private ArrayList request() throws IOException, JSONException {
        URL object = url;
        HttpsURLConnection connection = (HttpsURLConnection)object.openConnection();
        // using GET method
        connection.setRequestMethod("GET");
        // setting content type, for our app ir is JSON variable
        connection.setRequestProperty("Content-Type", "application/json");

        // make request and receive a response
        responseCode = connection.getResponseCode();
        //Log.i("Response Code", String.valueOf(responseCode));
        InputStreamReader inputStreamReader;

        // if response is okay use InputStream, if not - use ErrorStream
        if(responseCode == 200) {
            inputStreamReader = new InputStreamReader(connection.getInputStream());
        } else {
            inputStreamReader = new InputStreamReader(connection.getErrorStream());
        }

        BufferedReader bufferedReader = new BufferedReader((inputStreamReader));
        String inputLine;
        StringBuffer responseStringBuffer = new StringBuffer();

        while((inputLine = bufferedReader.readLine()) != null) {
            responseStringBuffer.append(inputLine);

        }
        bufferedReader.close();
        // printing the result
        Log.i("Response Result", responseStringBuffer.toString());
//        Log.i("Response Code", String.valueOf(responseCode));

        if(responseCode == 200) {
            return parsingJsonObject(responseStringBuffer.toString());
        } else {
            return null;
        }
    }

    public ArrayList parsingJsonObject(String response) throws JSONException {
        // attempts to create a json object of achieving a response
        JSONObject object = new JSONObject(response);
        AppDatabase db = AppActivity.getDatabase(this.context);
        ArrayList movieList = new ArrayList<Movie>();

        JSONArray arr = object.getJSONArray("results");
        for (int i = 0; i < arr.length(); i++) {
            long movieId = arr.getJSONObject(i).getLong("id");
            Movie movie = new Movie();
            movie.setMovieId(movieId);
            movie.setTitle(arr.getJSONObject(i).getString("title"));
            movie.setDescription(arr.getJSONObject(i).getString("overview"));
            movie.setReleaseDate(arr.getJSONObject(i).getString("release_date"));
            movie.setRating(arr.getJSONObject(i).getDouble("vote_average"));
            String posterPath;
            //
            if (type == 1) { //for the ucpoming movies poster path to be backdrop_path
                posterPath = "https://image.tmdb.org/t/p/w780/" +
                        arr.getJSONObject(i).getString("backdrop_path") +
                        "?api_key=6388d65378b5671a4bb1849e475856bb";
            } else {
                posterPath = "https://image.tmdb.org/t/p/w500/" +
                        arr.getJSONObject(i).getString("poster_path") +
                        "?api_key=6388d65378b5671a4bb1849e475856bb";
            }
            movie.setImageUrl(posterPath);
            movieList.add(movie);
            if (type != 1) {
                Movie oldMovie = db.movieDAO().getMovie(movieId);
                if (oldMovie != null) {
                    movie.setFavorite(oldMovie.getFavorite());
                    movie.setPhotoPath(oldMovie.getPhotoPath());
                    db.movieDAO().update(movie);
                } else {
                    db.movieDAO().insert(movie);
                }
            }
        }
        return movieList;
    }

    private void failed(int code) {
        if(listener != null) {
            listener.failed(code);
        }
    }

    private void success(ArrayList items) {
        if(listener != null) {
            listener.success(items);
        }
    }
}

