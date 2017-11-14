package com.example.zemoso.downloadmanager.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.zemoso.downloadmanager.Adapters.GalleryAdapter;
import com.example.zemoso.downloadmanager.Interface.DownloadUrl;
import com.example.zemoso.downloadmanager.POJO.AmplitudeData;
import com.example.zemoso.downloadmanager.POJO.GalleryData;
import com.example.zemoso.downloadmanager.R;
import com.example.zemoso.downloadmanager.Utils.AmplitudeLogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements DownloadUrl {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private DownloadUrl downloadUrl;
    private DownloadManager downloadManager = null;
    private String url;
    private Uri uri;
    private List<AmplitudeData> amplitudeDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadUrl = this;
        setContentView(R.layout.activity_main);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        ArrayList<GalleryData> galleryDataList = populateData();
        galleryAdapter = new GalleryAdapter(galleryDataList,downloadUrl);
        recyclerView = findViewById(R.id.gallery);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(galleryAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    public ArrayList<GalleryData> populateData()
    {
        try {

            JSONObject object = new JSONObject(loadJSONFromAsset());
            JSONArray mArray = object.getJSONArray("urls");
            ArrayList<GalleryData> galleryDataArrayList = new ArrayList<>();
            GalleryData galleryData;
            for(int i=0; i < mArray.length();i++)
            {
                JSONObject object_inside = mArray.getJSONObject(i);
                String id = object_inside.getString("id");
                String url = object_inside.getString("url");
                galleryData = new GalleryData();
                galleryData.setId(id);
                galleryData.setUrl(url);
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
                is.read(buffer);
                is.close();
                json = new String(buffer);
            }
        } catch (IOException ex) {
            Log.e(TAG,"cannot open file",ex);
            return null;
        }
        return json;
    }


    @Override
    public void setData(GalleryData galleryData) {
        Toast.makeText(this, "Downloading " + galleryData.getId(), Toast.LENGTH_SHORT).show();
       url = galleryData.getUrl();
        uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        String path = Environment.getExternalStorageDirectory() +File.separator+ Environment.DIRECTORY_PICTURES +"/Download_Manager/"
                +galleryData.getId()+".mp4";
        File file = new File(path);
        if(file.exists()) {
            Log.d("deleting file",file.getAbsolutePath());
            file.delete();
        }
        else
            Log.d("file not found",path);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"/Download_Manager/"
                        +galleryData.getId()+".mp4");
        long refId = downloadManager.enqueue(request);
        Cursor c =  downloadManager.query(new DownloadManager.Query()
        .setFilterByStatus(DownloadManager.STATUS_RUNNING));

        Log.d("running", String.valueOf(c.getCount()));

        /*refIdList.add(refId);
        refStrings.add(galleryData.getId());*/
        amplitudeDataList.add(new AmplitudeData(refId,path,System.currentTimeMillis(),
                TrafficStats.getTotalRxBytes(),c.getCount(),galleryData.getUrl()));

    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
           // Toast.makeText(MainActivity.this,refStrings.get(refIdList.indexOf(referenceId)),Toast.LENGTH_SHORT).show();
            boolean isDownloadSucces = true;
            String reason ="";
            Cursor c =  downloadManager.query(new DownloadManager.Query().setFilterById(referenceId));
            if (c.moveToFirst())
            {
                if(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))== DownloadManager.STATUS_FAILED)
                {
                    isDownloadSucces = false;
                    reason = c.getString(c.getColumnIndex(DownloadManager.COLUMN_REASON));
                }
            }
//            c = null;
            for(int i=0;i<amplitudeDataList.size();i++) {
              AmplitudeData amplitudeData = amplitudeDataList.get(i);
                if(amplitudeData.id == referenceId) {
                    Log.d("path Exists",new File(amplitudeData.filePath).exists() + "");
                    AmplitudeLogManager.logFileDownloadStatus(new File(amplitudeData.filePath),amplitudeData.url,"mp4",
                            amplitudeData.startTime
                    ,amplitudeData.startBytes,isDownloadSucces,amplitudeData.downloadsAlreadyInProgress,reason);
                    amplitudeDataList.remove(i);
                }
            }
        }
    };
}
