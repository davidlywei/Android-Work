
package com.example.android.gifsearch.GiphyJson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Looping {

    @SerializedName("mp4")
    @Expose
    private String mp4;

    public String getMp4() {
        return mp4;
    }

    public void setMp4(String mp4) {
        this.mp4 = mp4;
    }

}
