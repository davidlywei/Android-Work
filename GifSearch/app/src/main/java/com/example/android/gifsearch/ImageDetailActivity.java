package com.example.android.gifsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.gifsearch.utilities.GifConstants;

/**
 * Class Name:  ImageDetailActivity
 *
 * Purpose:     Defines Image Detail Activity
 *
 * Author:      David Wei
 * Created on:  4/4/17
 * Changelog:   First Version           4/4/17
 *              Added Gif Support       4/4/17
 *              Added ProgressBar       4/4/17
 */
public class ImageDetailActivity extends AppCompatActivity {

    private TextView mImageTextView;
    private ImageView mGifImageView;
    private ProgressBar mGifLoadingProgressBar;

    private String mImageUrl;
    private String mImageDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        // Link GUI Elements to code representations
        mImageTextView = (TextView) findViewById(R.id.A_TV_imageDescription);
        mGifImageView = (ImageView) findViewById(R.id.A_NIV_gifImageDetail);
        mGifLoadingProgressBar = (ProgressBar) findViewById(R.id.A_PB_gifLoading);

        // Grab intent
        Intent intent = getIntent();

        // Check if intent contains the description of the image
        if(intent.hasExtra(GifConstants.INTENT_DESCRIPTION)){

            // If so, store it
            mImageDescription = "Description:\n" +
                                    intent.getStringExtra(GifConstants.INTENT_DESCRIPTION);
        }

        // Check if intent contains URL for the image
        if(intent.hasExtra(GifConstants.INTENT_ADDRESS)){

            // If so, store downsized URL
            String downsizedUrl = intent.getStringExtra(GifConstants.INTENT_ADDRESS);

            // TODO: Add Gif Size checking / Mem Overflow handling
            // convert downsized URL to regular URL (May support later)
            //mImageUrl = downsizedUrl.replace("_s","");

            // No change from downsized URL
            mImageUrl = downsizedUrl;
        }

        // Set TextView with image description
        mImageTextView.setText(mImageDescription);

        // Use Glide to retrieve, and display animated gif image
        Glide.with(this).load(mImageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {


                    // Disappear progressbar on error or completion
                    @Override
                    public boolean onException(Exception e, String model,
                                               Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        mGifLoadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        mGifLoadingProgressBar.setVisibility(View.GONE);
                        return false;
                    }
        }).into(mGifImageView);
    }
}
