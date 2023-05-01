package com.example.weatherapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
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

        String url = Network.openWeatherAPI + "forecast.json?key=" + Network.openWeatherAPIKey + "&q="
                + currentLocation + "&days=1&aqi=no&alerts=no";
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        Log.d(TAG, url);

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

                        // get JSON object with key
                        JSONObject locationObj = jsonResponse.getJSONObject("location");
                        JSONObject currentObj = jsonResponse.getJSONObject("current");

                        // get forecast object, forecast days array and get day
                        JSONObject dayObj = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day");

                        // get icon from object condition inside object current
                        String icon = currentObj.getJSONObject("condition").getString("icon");

                        // get string values from JSON
                        String name = locationObj.getString("name");
                        String region = locationObj.getString("region");
                        String country = locationObj.getString("country");
                        String completeLocation = name + "/" + region + "/" + country;

                        // set image placeholder to weather icon
                        Glide.with(MainActivity.this).load("https:" + icon).into(binding.weatherImage);

                        binding.currentLocation.setText(completeLocation);
                        binding.minimumTemperature.setText(getString(R.string.minTemp, currentObj.getString("mintemp_c")));
                        binding.currentTemperature.setText(getString(R.string.currTemp, currentObj.getString("temp_c")));
                        binding.maximumTemperature.setText(getString(R.string.maxTemp, currentObj.getString("maxtemp_c")));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        asyncTask.execute();
    }
}