package com.example.zemoso.downloadmanager.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.example.zemoso.downloadmanager.R;

public class NameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 2001;

    private EditText name;
    private Button button;

    //region Override Activity Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        name = findViewById(R.id.name);
        button = findViewById(R.id.submit);
        button.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "App needs to save data", Toast.LENGTH_SHORT).show();
            }
            askPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == EXTERNAL_STORAGE_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
               askPermission();
            }
        }
    }

    //endregion

    //region Private Methods

    private void askPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    //endregion

    //region Interface Methods

    @Override
    public void onClick(View view) {
        if(view.getId() == button.getId()) {
            if(name.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please Enter User Name", Toast.LENGTH_SHORT).show();
            }else{
                Amplitude.getInstance().setUserId(name.getText().toString());
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }
    }

    //endregion
}
