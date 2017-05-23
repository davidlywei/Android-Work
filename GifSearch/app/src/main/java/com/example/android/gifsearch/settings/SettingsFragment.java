package com.example.android.gifsearch.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.android.gifsearch.R;

/**
 * Class Name:  SettingsFragment
 *
 * Purpose:     Defines fragment to hold settings
 *
 * Author:      David Wei
 * Created on:  4/15/17
 * Changelog:   First Version           4/15/17
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
