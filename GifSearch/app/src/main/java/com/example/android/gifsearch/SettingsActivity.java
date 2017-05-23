package com.example.android.gifsearch;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Class Name:  SettingsActivity
 *
 * Purpose:     Defines Activity to set settings
 *
 * Author:      David Wei
 * Created on:  4/15/17
 * Changelog:   First Version                       4/15/17
 *              Added Item Selection Handling       4/15/17
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set back button to go back to main activity
        // --- Get action bar
        ActionBar actionBar = this.getSupportActionBar();

        // --- set home as up
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Grab selected item
        int id = item.getItemId();

        // If item == home button
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
