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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
 * Changelog:   First Version           4/04/17
 *              Added Gif Support       4/04/17
 *              Added ProgressBar       4/04/17
 *              Removed Discription     4/15/17
 */
public class ImageDetailActivity extends AppCompatActivity {

    private ImageView mGifImageView;
    private ProgressBar mGifLoadingProgressBar;

    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        // Link GUI Elements to code representations
        mGifImageView = (ImageView) findViewById(R.id.A_NIV_gifImageDetail);
        mGifLoadingProgressBar = (ProgressBar) findViewById(R.id.A_PB_gifLoading);

        // Grab intent
        Intent intent = getIntent();

        // Check if intent contains URL for the image
        if(intent.hasExtra(GifConstants.INTENT_ADDRESS)){

            mImageUrl = intent.getStringExtra(GifConstants.INTENT_ADDRESS);
        }

        // Use Glide to retrieve, and display animated gif image
        Glide.with(this).load(mImageUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
