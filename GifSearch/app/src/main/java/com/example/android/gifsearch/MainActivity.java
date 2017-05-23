package com.example.android.gifsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * Changelog:   First Version           4/03/17
 *              Added RecyclerView      4/03/17
 *              Added Network support   4/03/17
 *              Added JSON parsing      4/04/17
 *              Added New Activity      4/04/17
 *              Added new Toaster       4/05/17
 *              Added Preferences       4/15/17
 */


public class MainActivity extends AppCompatActivity {

    private EditText mSearchEditText;
    private Button mSubmitButton;
    private GifAdapter mGifAdapter;
    private RecyclerView mGifRecyclerView;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private SharedPreferences mSharedPreferences;
    private PreferenceChangeListner mPrefListener;

    private boolean mRatingSelection[];

    // Rating Selection indicies
    private final int RATING_Y_IDX =        0;
    private final int RATING_G_IDX =        1;
    private final int RATING_PG_IDX =       2;
    private final int RATING_PG13_IDX =     3;
    private final int RATING_R_IDX =        4;
    private final int RATING_UNRATED_IDX =  5;
    private final int NUM_RATINGS =         6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ratings array and Ratings SharedPreferences
        initializeSettings();

        // Initialize Volley components (Request Queue, and Image Loader)
        initializeVolley();

        // Initialize GUI Elements (Edit text and buttons)
        initializeGuiElements();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mPrefListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Initialize inflater to inflate menu
        MenuInflater inflater = getMenuInflater();

        // Inflate menu from res
        inflater.inflate(R.menu.gif_menu, menu);

        return true;
    }

    /**
     * Attaches a menu item listener to listen for settings
     *
     * @param item: Menu Item
     * @return boolean to tell whether or not the action was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get id of item selected
        int id = item.getItemId();

        if(id == R.id.launch_settings){

            // Create launch settings intent
            Intent launchSettings = new Intent(this, SettingsActivity.class);

            // start the activity
            startActivity(launchSettings);

            // Handled item
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize components for shared preferences
     */
    private void initializeSettings(){

        // Get reference to shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize ratings selection
        mRatingSelection = new boolean[NUM_RATINGS];

        // create preference listener
        mPrefListener = new PreferenceChangeListner();

        // Register preference listener
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mPrefListener);

        // Initialize mRatingSelection array
        setSettings();
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
     * Sets up the RecyclerView
     * @param itemList: Contains list of image URL and Description pairs
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
    //                                                                       Submit Button Listener
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
            StringRequest request = new StringRequest(Request.Method.GET, url,
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
                // If image rating is pg or g
                if(checkRating(d.getRating())) {

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

                    // Append Rating to Description
                    description = "Rating: " + d.getRating() + "\n" + description;

                    // Store Image address, and created description into pair into arraylist
                    gifUrls.add(new Pair<>( d.getImages().getDownsized().getUrl(),
                                            description));
                }
            }

            // Return arraylist for RecyclerView
            return gifUrls;
        }
    }


    //---------------------------------------------------------------------------------------------
    //                                                                          Item Click Listener
    //---------------------------------------------------------------------------------------------

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


    //---------------------------------------------------------------------------------------------
    //                                                                   Preference Change Listener
    //---------------------------------------------------------------------------------------------

    /**
     * Handles changes in shared preferences
     */
    private class PreferenceChangeListner
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // On preference changes, set the settings array.
            setSettings();
        }
    }

    /**
     * Sets the ratings based off of the values stored in SharedPreferences file
     */
    private void setSettings(){

        mRatingSelection[RATING_Y_IDX] =
                mSharedPreferences.getBoolean(getString(R.string.rating_y_key),         true);
        mRatingSelection[RATING_G_IDX] =
                mSharedPreferences.getBoolean(getString(R.string.rating_g_key),         true);
        mRatingSelection[RATING_PG_IDX] =
                mSharedPreferences.getBoolean(getString(R.string.rating_pg_key),        true);
        mRatingSelection[RATING_PG13_IDX] =
                mSharedPreferences.getBoolean(getString(R.string.rating_pg13_key),      true);
        mRatingSelection[RATING_R_IDX] =
                mSharedPreferences.getBoolean(getString(R.string.rating_r_key),         false);
        mRatingSelection[RATING_UNRATED_IDX] =
                mSharedPreferences.getBoolean(getString(R.string.rating_unrated_key),   false);

    }

    /**
     * Method to check if requested rating should be displayed
     * @param rating: String representation of rating
     */
    private boolean checkRating(String rating){
        switch (rating){
            case "y":
                return mRatingSelection[RATING_Y_IDX];
            case "g":
                return mRatingSelection[RATING_G_IDX];
            case "pg":
                return mRatingSelection[RATING_PG_IDX];
            case "pg-13":
                return mRatingSelection[RATING_PG13_IDX];
            case "r":
                return mRatingSelection[RATING_R_IDX];
            default:
                return mRatingSelection[RATING_UNRATED_IDX];
        }
    }

}
