package com.example.android.gifsearch.utilities;


/**
 * Class Name:  GifConstants
 *
 * Purpose:     Class to store constants used in Application
 *
 * Author:      David Wei
 * Created on:  4/3/17
 * Changelog:   First Version           4/3/17
 *              Added Giphy API consts  4/3/17
 *              Added Intent Key consts 4/4/17
 *              Added Cache Size const  4/4/17
 */
public class GifConstants {

    //----------------------------------------------------------------------------------------------
    //                                                                           Giphy API Constants
    //----------------------------------------------------------------------------------------------

    // Giphy API Reference: https://github.com/Giphy/GiphyAPI

    // Constants for Giphy API access
    public static final String GIPHY_API_SCHEME = "http";
    public static final String GIPHY_API_URL = "api.giphy.com";
    public static final String GIPHY_API_VERSION = "v1";
    public static final String GIPHY_API_PATH = "gifs";

    // Developer API key. DO NOT USE FOR ANYTHING BESIDES TESTING
    public static final String GIPHY_API_KEY = "dc6zaTOxFJmzC";

    public enum Endpoints {
        SEARCH("search"),
        TRENDING("trending"),
        TRANSLATE("translate"),
        RANDOM("random");

        private final String endPoint;

        Endpoints(final String ePoint){
            endPoint = ePoint;
        }

        @Override
        public String toString() {
            return endPoint;
        }
    }

    //----------------------------------------------------------------------------------------------
    //                                                                          Intent Key Constants
    //----------------------------------------------------------------------------------------------

    public static final String INTENT_ADDRESS = "ADDRESS";
    public static final String INTENT_DESCRIPTION = "DESCRIPTION";

    //----------------------------------------------------------------------------------------------
    //                                                                           Cache Size Constant
    //----------------------------------------------------------------------------------------------

    public static final int CACHE_SIZE = 20;
}
