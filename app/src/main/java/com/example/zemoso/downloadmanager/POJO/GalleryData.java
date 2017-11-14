package com.example.zemoso.downloadmanager.POJO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zemoso on 14/11/17.
 */

public class GalleryData {

    private String id;
    private String  url;

    public GalleryData(JSONObject data) throws JSONException{
        id = data.getString("id");
        url = data.getString("url");
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
