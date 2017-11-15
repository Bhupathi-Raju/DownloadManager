package com.example.zemoso.downloadmanager.Interface;

import com.example.zemoso.downloadmanager.pojo.GalleryData;

/**
 * Created by zemoso on 14/11/17.
 */

public interface DownloadManager {
    void setData(GalleryData galleryData);
    boolean isDownloadInProgress();
}
