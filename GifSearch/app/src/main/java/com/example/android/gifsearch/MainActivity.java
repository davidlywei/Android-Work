package com.example.android.gifsearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.gifsearch.GiphyJson.Datum;
import com.example.android.gifsearch.GiphyJson.GiphyJson;
import com.example.android.gifsearch.utilities.GifAdapter;
import com.example.android.gifsearch.utilities.GifClickResponder;
import com.example.android.gifsearch.utilities.GifConstants;
import com.example.android.gifsearch.utilities.ToastHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * Class Name:  MainActivity
 *
 * Purpose:     Main entry point to GifSearch Application
 *
 * Author:      David Wei
 * Created on:  4/3/17
 * Changelog:   First Version           4/3/17
 *              Added RecyclerView      4/3/17
 *              Added Network support   4/3/17
 *              Added JSON parsing      4/4/17
 *              Added New Activity      4/4/17
 *              Added new Toaster       4/5/17
 */
public class MainActivity extends AppCompatActivity {

    private EditText mSearchEditText;
    private Button mSubmitButton;
    private GifAdapter mGifAdapter;
    private RecyclerView mGifRecyclerView;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Volley
        initializeVolley();

        // Initialize GUI Elements
        initializeGuiElements();
    }

    /**
     * Method to initialize all GUI Elements
     */
    private void initializeGuiElements() {

        // Link GUI search EditText with Code EditText
        mSearchEditText = (EditText) findViewById(R.id.A_ET_search);

        // Link GUI submit Button with code Button
        mSubmitButton = (Button) findViewById(R.id.A_BTN_submit);

        // Add submit button click listener
        mSubmitButton.setOnClickListener(new SubmitOnClickListener(this));
    }

    /**
     * Sets up the RecyclerView with
     * @param itemList
     */
    private void setupRecyclerView(ArrayList<Pair<String, String>> itemList) {

        // Link GUI RecyclerView with Code RecyclerView
        mGifRecyclerView = (RecyclerView) findViewById(R.id.A_RV_mainContent);

        // Create Linear Layout Manager for RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Create GifClickResponder
        ItemClickResponder listener = new ItemClickResponder(this);

        // Create Adapter for RecyclerView
        mGifAdapter = new GifAdapter(itemList, listener, mImageLoader);

        // Set RecyclerView layout manager and Adapter
        mGifRecyclerView.setLayoutManager(layoutManager);
        mGifRecyclerView.setAdapter(mGifAdapter);

        // Set RecyclerView to have a fixed size
        mGifRecyclerView.setHasFixedSize(true);
    }

    /**
     * Initialize components necessary for Volley
     */
    private void initializeVolley() {

        // Grab reference to RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);

        // Create ImageLoader
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap> mCache =
                    new LruCache<>(GifConstants.CACHE_SIZE);

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    //                                                                               Event Handlers
    //---------------------------------------------------------------------------------------------

    /**
     * Submit button click listener
     */
    private class SubmitOnClickListener implements View.OnClickListener{

        private Context context;

        SubmitOnClickListener(Context c) {
            context = c;
        }

        @Override
        public void onClick(View v) {

            // Create Giphy Search API call from Search parameter
            String url = createUrl(mSearchEditText.getText().toString(),
                    GifConstants.Endpoints.SEARCH);

            // Log the URL to double check it's constructed properly
            Log.d("Generated URL", url);

            // Create String request
            StringRequest request = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {

                    // Process response string
                    @Override
                    public void onResponse(String response) {

                        // Setup RecyclerView to store new images
                        setupRecyclerView(parseResponse(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Toast an Error
                        ToastHandler.makeToast(context, "Error");
                    }
                });

            mRequestQueue.add(request);
        }

        /**
         * Creates a Giphy url based off of the search item, and the endpoint
         *
         * @param searchItem: The string of the item you are searching for
         * @param endpoint: The type of endpoint you would like to use
         *                  (Only search is supported atm)
         *
         * @return a String representation of the URL
         */
        private String createUrl(String searchItem, GifConstants.Endpoints endpoint)
        {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme(GifConstants.GIPHY_API_SCHEME)
                    .authority(GifConstants.GIPHY_API_URL)
                    .appendPath(GifConstants.GIPHY_API_VERSION)
                    .appendPath(GifConstants.GIPHY_API_PATH)
                    .appendPath(endpoint.toString())
                    .appendQueryParameter("q", searchItem)
                    .appendQueryParameter("api_key", GifConstants.GIPHY_API_KEY);

            return uriBuilder.build().toString();
        }

        /**
         * Turns the JSON response from Giphy into an ArrayList of Image URLs and Image Description
         *
         * @param response: String representation of the JSON returned from Giphy
         *
         * @return  List of Pairs of <Image URLs, Image Descriptions>
         */
        private ArrayList<Pair<String, String>> parseResponse(String response)
        {
            // Create Arraylist to hold URLs from the JSON
            ArrayList<Pair<String, String>> gifUrls = new ArrayList<>();

            // Create a gson POJO builder
            Gson gson = new GsonBuilder().create();

            // Build POJO from JSON
            GiphyJson giphyJson = gson.fromJson(response, GiphyJson.class);

            // If gif is below a certain rating, add it to list of URLs
            for(Datum d : giphyJson.getData())
            {
                // Store the rating of the image
                String rating = d.getRating();

                // TODO: Make rating selectable by User
                // If image rating is pg or g
                if(rating.equals("pg") || rating.equals("g")) {

                    // Separate out image slug descriptions
                    String[] slug = d.getSlug().split("-");

                    String description = "";

                    // Remove last item from slug (ie image ID)
                    for(int i = 0; i < slug.length - 1; i++) {
                        description += slug[i] + " ";
                    }

                    // If no items are in slug, replace it with No Description
                    if(description.equals("")) {
                        description = "No Description";
                    }

                    // Store Image address, and created description into pair into arraylist
                    gifUrls.add(new Pair<>( d.getImages().getDownsized().getUrl(),
                                            description));
                }
            }

            // Return arraylist for RecyclerView
            return gifUrls;
        }
    }

    /**
     * Implements functionality to start a new activity when clicking on an item in the
     * RecyclerView
     */
    private class ItemClickResponder implements GifClickResponder {

        private Context context;

        ItemClickResponder(Context c)
        {
            context = c;
        }

        @Override
        public void onClickingViewHolder(Pair<String, String> gifImageProperties) {

            // Create Intent
            Intent imageDetailPg = new Intent(context, ImageDetailActivity.class);

            // Stash away Address and Description
            imageDetailPg.putExtra(GifConstants.INTENT_ADDRESS, gifImageProperties.first);
            imageDetailPg.putExtra(GifConstants.INTENT_DESCRIPTION, gifImageProperties.second);

            // Start activity
            startActivity(imageDetailPg);
        }
    }
}
