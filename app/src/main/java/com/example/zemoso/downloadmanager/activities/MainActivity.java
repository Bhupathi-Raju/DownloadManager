package com.example.zemoso.downloadmanager.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.zemoso.downloadmanager.adapters.GalleryAdapter;
import com.example.zemoso.downloadmanager.Interface.DownloadManager;
import com.example.zemoso.downloadmanager.pojo.AmplitudeData;
import com.example.zemoso.downloadmanager.pojo.GalleryData;
import com.example.zemoso.downloadmanager.R;
import com.example.zemoso.downloadmanager.utils.AmplitudeLogManager;
import com.example.zemoso.downloadmanager.list_helper.BurstItemSpacing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DownloadManager {

    private static final String TAG = MainActivity.class.getSimpleName();
    private android.app.DownloadManager downloadManager = null;
    private List<AmplitudeData> amplitudeDataList = new ArrayList<>();
    private boolean isDownloadInProgress;
    private GalleryAdapter galleryAdapter;

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadManager = (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,new IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        ArrayList<GalleryData> galleryDataList = populateData();
        galleryAdapter = new GalleryAdapter(this, galleryDataList, this);
        RecyclerView recyclerView = findViewById(R.id.gallery);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new BurstItemSpacing(this));
        recyclerView.setAdapter(galleryAdapter);
        ContextCompat.getColor(this, R.color.success);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    //endregion

    //region Loading Data and setting in List
    public ArrayList<GalleryData> populateData() {
        try {
            JSONObject object = new JSONObject(loadJSONFromAsset());
            JSONArray mArray = object.getJSONArray("urls");
            ArrayList<GalleryData> galleryDataArrayList = new ArrayList<>();
            GalleryData galleryData;
            int length = mArray.length();
            for(int i=0; i < length; i++) {
                JSONObject object_inside = mArray.getJSONObject(i);
                galleryData = new GalleryData(object_inside);
                galleryDataArrayList.add(galleryData);
            }
            return galleryDataArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            //BufferedReader bufferedReader = new BufferedReader()
            if(is!=null) {
                int size = is.available();
                byte[] buffer = new byte[size];
                Log.i(TAG, "Total bytes read are "+is.read(buffer));
                is.close();
                json = new String(buffer);
            }
        } catch (IOException ex) {
            Log.e(TAG,"cannot open file",ex);
            return null;
        }
        return json;
    }
    //endregion

    //region Interface Methods
    @Override
    public void setData(GalleryData galleryData) {
        isDownloadInProgress = true;
        Toast.makeText(this, "Downloading " + galleryData.getId(), Toast.LENGTH_SHORT).show();
        String url = galleryData.getUrl();
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(url));
        String path = Environment.getExternalStorageDirectory() +File.separator+ Environment.DIRECTORY_PICTURES +"/Download_Manager/"
                +galleryData.getId()+".mp4";
        File file = new File(path);
        if(file.exists() && file.delete()) {
            Log.d(TAG, file.getAbsolutePath());
        }
        else
            Log.d("file not found",path);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"/Download_Manager/"
                        +galleryData.getId()+".mp4");
        long refId = downloadManager.enqueue(request);
        Cursor c =  downloadManager.query(new android.app.DownloadManager.Query()
        .setFilterByStatus(android.app.DownloadManager.STATUS_RUNNING));

        Log.d("running", String.valueOf(c.getCount()));

        /*refIdList.add(refId);
        refStrings.add(galleryData.getId());*/
        amplitudeDataList.add(new AmplitudeData(refId,path,System.currentTimeMillis(),
                TrafficStats.getTotalRxBytes(),c.getCount(),galleryData.getUrl()));

    }

    @Override
    public boolean isDownloadInProgress() {
        return isDownloadInProgress;
    }
    //endregion

    //region BroadcastReceiver for OnComplete Intent
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("entered","entered");
            isDownloadInProgress = false;
            Long referenceId = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            boolean isDownloadSuccess = true;
            String reason ="";
            Cursor c =  downloadManager.query(new android.app.DownloadManager.Query().setFilterById(referenceId));

            if (c.moveToFirst()) {
                if(c.getInt(c.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS))== android.app.DownloadManager.STATUS_FAILED) {
                    galleryAdapter.downloadComplete(false);
                    isDownloadSuccess = false;
                    reason = c.getString(c.getColumnIndex(android.app.DownloadManager.COLUMN_REASON));
                    Toast.makeText(MainActivity.this, "Burst download Failed "+referenceId, Toast.LENGTH_SHORT).show();
                }
                if(c.getInt(c.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS))
                        == android.app.DownloadManager.STATUS_SUCCESSFUL){
                    galleryAdapter.downloadComplete(true);
                    isDownloadSuccess = true;
                    Toast.makeText(MainActivity.this, "Burst download completed "+referenceId, Toast.LENGTH_SHORT).show();
                }
                if(c.getInt(c.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS)) ==
                        android.app.DownloadManager.STATUS_PAUSED){
                    Toast.makeText(MainActivity.this, "Burst download paused "+referenceId, Toast.LENGTH_SHORT).show();
                }
            }

            for(int i=0;i<amplitudeDataList.size();i++) {
              AmplitudeData amplitudeData = amplitudeDataList.get(i);
                if(amplitudeData.id == referenceId){
                    Log.d("path Exists",new File(amplitudeData.filePath).exists() + "");
                    AmplitudeLogManager.logFileDownloadStatus(MainActivity.this, new File(amplitudeData.filePath),amplitudeData.url,"mp4",
                            amplitudeData.startTime
                    ,amplitudeData.startBytes,isDownloadSuccess,amplitudeData.downloadsAlreadyInProgress,reason);
                    amplitudeDataList.remove(i);
                }
            }
        }
    };
    //endregion

}
