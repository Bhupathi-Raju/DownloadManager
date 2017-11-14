package com.example.zemoso.downloadmanager.Utils;

import android.net.TrafficStats;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amplitude.api.Amplitude;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @author Atif
 * Created on 12/11/17.
 * Helper class to log Amplitude data
 */

public class AmplitudeLogManager {

    private final static String TAG = AmplitudeLogManager.class.getSimpleName();
    public static final String AMPLITUDE_API_KEY = "e6b1a2c85ad0d4386d394dc996323aae";

    /**
     * <p>Logs the file download status to the Amplitude</p>
     * @param file actual downloaded file, null if download failed
     * @param fileUrl url of the File downloaded
     * @param type type of the file (mp4/png/zip)
     * @param startTime start time of the download
     * @param startBytes start bytes before the download
     * @param downloadStatus true if download was success, false otherwise
     * @param failReason reason for the failure, can be null if download succeeded
     */
   public static void logFileDownloadStatus(
            @Nullable File file,
            @NonNull String fileUrl,
            @NonNull String type,
            long startTime,
            long startBytes,
            boolean downloadStatus,
            int downloadsAlreadyInQueue,
            @Nullable String failReason){
        long endTime = System.currentTimeMillis();
        long endBytes = TrafficStats.getTotalRxBytes();
        float timeTakenInSecs = ((endTime - startTime) / 1000.0f);
        float mbReceived = (endBytes - startBytes) / 1000000.0f;

        JSONObject downloadProperties = new JSONObject();

        if(downloadStatus && file != null && file.exists()) {
            long size = file.length();
            float sizeInMb = (size / 1000000.0f);
            try {
                downloadProperties.put(Keys.MEDIA_SIZE, sizeInMb);
                downloadProperties.put(Keys.DOWNLOADS_ALREADY_IN_QUEUE,downloadsAlreadyInQueue);
                downloadProperties.put(Keys.MB_RECEIVED, mbReceived);
                downloadProperties.put(Keys.AMPLITUDE_MEDIA_TYPE, type);
                downloadProperties.put(Keys.NETWORK_SPEED, (mbReceived / timeTakenInSecs));
                downloadProperties.put(Keys.TIME_TAKEN, timeTakenInSecs);
                downloadProperties.put(Keys.MEDIA_URL, fileUrl);
                downloadProperties.put(Keys.DOWNLOAD_STATUS, downloadStatus);

                Amplitude.getInstance().logEvent(Keys.MEDIA_DOWNLOAD_EVENT, downloadProperties);
            } catch (JSONException e) {
                Log.e("Utils#Amplitude", "Amplitude JSON error", e);
            }
        }else{
            try {
                downloadProperties.put(Keys.MB_RECEIVED, mbReceived);
                downloadProperties.put(Keys.AMPLITUDE_MEDIA_TYPE, type);
                downloadProperties.put(Keys.TIME_TAKEN, timeTakenInSecs);
                downloadProperties.put(Keys.MEDIA_URL, fileUrl);
                downloadProperties.put(Keys.DOWNLOAD_STATUS, downloadStatus);
                downloadProperties.put(Keys.FAIL_REASON, failReason);

                Amplitude.getInstance().logEvent(Keys.MEDIA_DOWNLOAD_EVENT, downloadProperties);
            } catch (JSONException e) {
                Log.e("Utils#Amplitude", "Amplitude JSON error", e);
            }
        }
    }

    private static class Keys {

        private static final String BURST_DOWNLOAD_EVENT = "Burst Download";
        private static final String MEDIA_DOWNLOAD_EVENT = "Media Download";
        private static final String BURST_UPLOAD_EVENT = "Burst Upload";
        private static final String USER_CONTACT_EVENT = "User Contact";

        private static final String AMPLITUDE_MEDIA_TYPE = "MediaType";
        private static final String MEDIA_SIZE = "MediaSize";
        private static final String TIME_TAKEN = "TimeTaken";
        private static final String NETWORK_SPEED = "NetworkSpeed";
        private static final String MB_RECEIVED = "MbReceived";
        private static final String MH_ID = "MHID";
        private static final String MH_NAME = "MHName";
        private static final String BURST_ID = "BurstId";
        private static final String BURST_LOCAL_ID = "BurstLocalId";
        private static final String MEDIA_URL = "Mediaurl";
        private static final String NETWORK_TYPE_BEGIN = "NetworkTypeBegin";
        private static final String NETWORK_TYPE_END = "NetworkTypeEnd";
        private static final String TOTAL_DATA_SIZE = "TotalDataSize";
        private static final String BURST_DATA_SIZE = "BurstDataSize";
        private static final String BATTERY_SAVER = "BatterySaverEnabled";
        private static final String DATA_RESTRICTION = "IsDataRestricted";
        private static final String IS_DOWNLOAD_IN_PROGRESS = "IsDownloadInProgress";
        private static final String DOWNLOAD_STATUS = "IsDownloadSuccess";
        private static final String UPLOAD_STATUS = "IsUploadSuccess";
        private static final String FAIL_REASON = "FailReason";
        private static final String DOWNLOADS_ALREADY_IN_QUEUE = "DownloadsAlreadyInQueue";
    }
}