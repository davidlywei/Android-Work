package com.example.android.gifsearch.utilities;

import android.content.Context;
import android.widget.Toast;

/**
 * Class Name:  ToastHandler
 *
 * Purpose:     Create a common Toast creation class to keep track of
 *              toast requests, and ensure that only one toast is shown
 *              at a time. (Prevents queueing of Toasts)
 *
 * Author:      David Wei
 * Created on:  4/5/17
 * Changelog:   First Version       4/5/17
 */

public class ToastHandler {

    private static Toast mToaster;

    /**
     * Creates a toast, after checking if one already exists.
     * Always uses short Toast length
     *
     * @param context: The context in which to display the Toast
     * @param toast: The message to display
     */
    public static void makeToast(Context context, String toast){
        if(mToaster != null){
            mToaster.cancel();
        }

        mToaster = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        mToaster.show();
    }

    /**
     * Creates a toast, after checking if one already exists.
     * Allows for selectable length toasts
     *
     * @param context: The context in which to display the Toast
     * @param toast: The message to display
     * @param length: The length of time to display the message
     */
     public static void makeToast(Context context, String toast, int length){
        if(mToaster != null){
            mToaster.cancel();
        }

        mToaster = Toast.makeText(context, toast, length);
        mToaster.show();
    }
}
