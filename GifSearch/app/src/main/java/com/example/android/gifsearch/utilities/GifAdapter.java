package com.example.android.gifsearch.utilities;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.example.android.gifsearch.R;

import java.util.ArrayList;

/**
 * Class Name:  GifAdapter
 *
 * Purpose:     Adapter for creating Views for RecyclerView
 *
 * Author:      David Wei
 * Created on:  4/3/17
 * Changelog:   First Version           4/3/17
 */
public class GifAdapter extends RecyclerView.Adapter<GifViewHolder>{

    private int mNumAddresses;
    private ArrayList<Pair<String, String>> mGifAddressArray;
    private GifClickResponder mGifClickResponder;
    private ImageLoader mImageLoader;

    public GifAdapter(ArrayList<Pair<String, String>> addressArray,
                      GifClickResponder listener, ImageLoader loader) {

        // Store the reference to the GifClickResponder
        mGifClickResponder = listener;

        // Store the addresses for each gif
        mGifAddressArray = addressArray;

        // Grab the number of addresses
        mNumAddresses = mGifAddressArray.size();

        mImageLoader = loader;
    }

    @Override
    public GifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Grab context from parent
        Context context = parent.getContext();

        // Create Layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        // Get reference to the layout
        int layoutRef = R.layout.gif_item_view;

        // Define whether or not to attach to a parent immediately
        Boolean attachImmediately = false;

        // Inflate View (false because we do not want to attach it to the parent immediately)
        View gifItemView = inflater.inflate(layoutRef, parent, attachImmediately);


        // Return GifViewHolder
        return new GifViewHolder(gifItemView, mGifClickResponder, mImageLoader);
    }

    @Override
    public void onBindViewHolder(GifViewHolder holder, int position) {

        holder.setProperties(mGifAddressArray.get(position));
    }

    @Override
    public int getItemCount() {

        // Verify mGifAddressArray is set
        if(mGifAddressArray != null) {
            return mNumAddresses;
        }
        else {
            // if not return 0
            return 0;
        }
    }
}
