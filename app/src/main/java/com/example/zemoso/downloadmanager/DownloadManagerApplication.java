package com.example.zemoso.downloadmanager;

import android.app.Application;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.example.zemoso.downloadmanager.Utils.AmplitudeLogManager;

/**
 * Created by zemoso on 14/11/17.
 */

public class DownloadManagerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Amplitude.getInstance().initialize(this,AmplitudeLogManager.AMPLITUDE_API_KEY);
    }
}
