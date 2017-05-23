package com.example.android.gifsearch.utilities;

import android.support.v4.util.Pair;

/**
 * Class Name:  GifClickResponder
 *
 * Purpose:     Interface to standardize click listeners for ViewHolder
 *
 * Author:      David Wei
 * Created on:  4/3/17
 * Changelog:   First Version           4/3/17
 */
public interface GifClickResponder {

    /**
     * On Clicking a view holder, the ViewHolder will return:
     *
     * @param gifImageProperties: A pair containing two strings: Image URL, Image Description
     *
     */
    void onClickingViewHolder(Pair<String, String> gifImageProperties);
}
