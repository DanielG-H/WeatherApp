package com.example.weatherapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String currentLocation = intent.getStringExtra("LATITUDE") + "," + intent.getStringExtra("LONGITUDE");
        Log.d(TAG, currentLocation);

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(Network.openWeatherAPI + "current.json?key=" + Network.openWeatherAPIKey).build();

        createAsyncTask(client, request);
    }

    private void createAsyncTask(OkHttpClient client, Request request) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        return null;
                    }
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(s);
                        JSONObject locationObject = jsonResponse.getJSONObject("location");
                        binding.currentLocationTv.setText(locationObject.getString("name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        asyncTask.execute();
    }
}