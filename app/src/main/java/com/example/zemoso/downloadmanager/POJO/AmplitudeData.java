package com.example.zemoso.downloadmanager.POJO;

/**
 * Created by zemoso on 14/11/17.
 */

public class AmplitudeData  {
   public long id;
   public String filePath;
   public long startTime,startBytes;
   public int downloadsAlreadyInProgress;
   public String url;

    public AmplitudeData(long id, String filePath, long startTime, long startBytes, int downloadsAlreadyInProgress,
                         String url) {
        this.id = id;
        this.url = url;
        this.filePath = filePath;
        this.startTime = startTime;
        this.startBytes = startBytes;
        this.downloadsAlreadyInProgress = downloadsAlreadyInProgress;
    }
}
