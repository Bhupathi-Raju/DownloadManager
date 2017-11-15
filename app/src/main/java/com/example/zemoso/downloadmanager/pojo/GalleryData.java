package com.example.zemoso.downloadmanager.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zemoso on 14/11/17.
 */

public class GalleryData {

    private String id;
    private String  url;
    private boolean isDownloaded;

    public GalleryData(JSONObject data) throws JSONException{
        id = data.getString("id");
        url = data.getString("url");
        isDownloaded = false;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }


    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
