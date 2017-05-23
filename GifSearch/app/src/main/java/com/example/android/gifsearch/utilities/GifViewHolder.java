package com.example.android.gifsearch.utilities;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.android.gifsearch.R;

/**
 * Class Name:  GifViewHolder
 *
 * Purpose:     ViewHolder to store references relevant to Gif View
 *
 * Author:      David Wei
 * Created on:  4/3/17
 * Changelog:   First Version           4/3/17
 */
class GifViewHolder extends RecyclerView.ViewHolder {

    private TextView mGifDescription;
    private NetworkImageView mGifImage;
    private Pair<String, String> mGifAddressText;
    private ImageLoader mGifImageLoader;

    GifViewHolder(View itemView, GifClickResponder listener, ImageLoader loader) {
        super(itemView);

        // Create new GifViewHolderClickListener
        GifViewHolderClickListener clickListener = new GifViewHolderClickListener(listener);

        // Set the onclick listener of the view to this method
        itemView.setOnClickListener(clickListener);

        // Link GUI gif Address TextView to Code TextView
        mGifDescription = (TextView) itemView.findViewById(R.id.A_TV_itemView);

        // Link GUI ImageView to Code NetworkImageView
        mGifImage = (NetworkImageView) itemView.findViewById(R.id.A_NIV_gifImage);

        // Set Image loader
        mGifImageLoader = loader;
    }

    /**
     * Takes in a pair, and sets the image's properties
     *
     * @param imageProperties: A pair of Url and Image Description
     *
     */
    void setProperties(Pair<String, String> imageProperties)
    {
        // Store image properties for later use
        mGifAddressText = imageProperties;

        // Set the text in TextView
        mGifDescription.setText(imageProperties.second);

        // Load image from URL
        mGifImage.setImageUrl(mGifAddressText.first, mGifImageLoader);
    }

    /**
     * Implements click listener to pass listen event back to MainActivity
      */
    private class GifViewHolderClickListener implements View.OnClickListener{

        private GifClickResponder mGifClickResponder;

        GifViewHolderClickListener(GifClickResponder listener)
        {
            mGifClickResponder = listener;
        }

        @Override
        public void onClick(View v) {

            mGifClickResponder.onClickingViewHolder(mGifAddressText);
        }
    }
}
