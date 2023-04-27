package com.example.weatherapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityLoadingBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoadingActivity extends AppCompatActivity {
    // to check color schemes https://m3.material.io/theme-builder#/custom
    ActivityLoadingBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int FINE_LOCATION_PERMISSION_CODE = 100;
    private static final int COARSE_LOCATION_PERMISSION_CODE = 101;
    private final String TAG = "LoadingActivity";
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this).load(R.drawable.sunshine).into(binding.appLogo);

        startProgressBar();
        startPermissionRequest();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(LoadingActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(LoadingActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_CODE:
            case COARSE_LOCATION_PERMISSION_CODE: {
                boolean isPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (isPermissionGranted) {
                    Toast.makeText(LoadingActivity.this, "Request permission GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoadingActivity.this, "Request permission DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startPermissionRequest() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error trying to get last GPS location");
                e.printStackTrace();
            }
        });
    }

    private void startProgressBar() {
        // milisinFuture = total time and interval = increments every x
        LoadingCountDownTimer loadingCountDownTimer = new LoadingCountDownTimer(5000, 1000);
        loadingCountDownTimer.start();
    }

    private void startMainActivity() {
        Intent i = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(i);
    }

    public class LoadingCountDownTimer extends CountDownTimer {
        private int progress = 0;

        public LoadingCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            progress = progress + 20;
            binding.loadingProgressBar.setProgress(progress);
        }

        @Override
        public void onFinish() { startMainActivity(); }
    }
}