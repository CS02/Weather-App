package com.example.getweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/*
* This is the MainActivity Class
* It's responsible for:
* initializing the data base (when app is first called)
* creating and displaying a recyclerview
* getting location
* and starting background tasks
* */
public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{

    //Used to access data base
    DataBaseHandler dataBase;

    //Used to create recyclerView
    RecyclerView recyclerView;
    MyAdapter adapter;

    //This ArrayList will store the list of 10 cities to get data of
    private ArrayList<String> cityData = new ArrayList<String>();

    //Used to retrieve location
    LocationManager locationManager;
    LocationListener locationListener = new MyLocationListener();

    //Used to store longitude and latitude cordinates
    String lon = "";
    String lat = "";

    //Used to check gps permissions
    private boolean gps_enable = false;
    private boolean network_enable = false;

    //Used to handle messages from second thread
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize data base
        dataBase = DataBaseHandler.getInstance(MainActivity.this);
        //gets cursor which is needed to read from data base
        Cursor cursor = dataBase.getdata();
        Log.i("cursor", String.valueOf(cursor.getCount()));

        //if cursor is empty (meaning no data exists)
        if(cursor.getCount() == 0){
            //initialize the data
            initializeData();
        }

        //create and display recyclerview
        recyclerView = findViewById(R.id.recylerview);
        adapter = new MyAdapter(MainActivity.this,cursor, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Get Location Access
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);//done
        checkLocationPermission();
        getMyLocation();

        handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.i("handled", "handleMessage: ");
                adapter = new MyAdapter(MainActivity.this,dataBase.getdata(), MainActivity.this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        };

        //Start Background Tasks
        BackgroundTaskHandler bth = new BackgroundTaskHandler(MainActivity.this, handler, adapter);
        bth.updateDataBase();


    }

    //initializes the data
    private void initializeData() {
        //insert spot for current city
        cityData.add("Atlanta");
        cityData.add("Boston");
        cityData.add("Vancouver");
        cityData.add("Chicago");
        cityData.add("Houston");
        cityData.add("Seattle");
        cityData.add("Detroit");
        cityData.add("Miami");
        cityData.add("Cancun");
        cityData.add("Denver");

        for(String cityName : cityData){
            //add city to the database
            City city = new City(MainActivity.this, cityName);
        }
    }

    //returns cityData
    public ArrayList<String> getCityData() {
        return (ArrayList<String>) cityData.clone();
    }

    //used for recieving location
    //Has onLocationChanged method which returns Longitude and Latitude cordinates
    //Used to add cities to data base using location
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            //if we get loaction
            if (location != null) {
                locationManager.removeUpdates(locationListener);
                lat = "" + location.getLatitude();
                lon = "" + location.getLongitude();
                Log.i("data", "Latitude: " + lat + " Longitude: " + lon);
                //add the city to the database
                City city = new City(MainActivity.this, lat,lon);
            }
        }

        @Override
        public void onLocationChanged(@NonNull List<Location> locations) {
            LocationListener.super.onLocationChanged(locations);
        }

        @Override
        public void onFlushComplete(int requestCode) {
            LocationListener.super.onFlushComplete(requestCode);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            LocationListener.super.onStatusChanged(provider, status, extras);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            LocationListener.super.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LocationListener.super.onProviderDisabled(provider);
        }
    }

    //used for requesting location via GPS or Network
    public void getMyLocation() {
        try {
            gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.i("error", "could not get location");
        }

        if (!gps_enable && !network_enable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Attention");
            builder.setMessage("Location is not available, please enable location services");
            builder.create().show();
        }

        if (gps_enable) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        if(network_enable){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    //Checks if permissions are granted from user
    private boolean checkLocationPermission(){
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int location2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermission = new ArrayList<>();

        if(location != PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(location2 != PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(!listPermission.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermission.toArray(new String[listPermission.size()]), 1);
        }
        return true;
    }

    //if an item(city) is clicked will go to next activity
    //Passes additional weather data to display on next activity
    @Override
    public void onItemClick(String cityName) {
        //put code, when an item is clicked
        Intent intent = new Intent(this, CityDetailsActivity.class);

        Cursor cursor = dataBase.getSpecificCityData(cityName);
        cursor.moveToNext();
        intent.putExtra("name", cityName);
        intent.putExtra("description", cursor.getString(1));
        intent.putExtra("temperature", cursor.getString(2));
        intent.putExtra("feelsLike", cursor.getString(3));
        intent.putExtra("humidity", cursor.getString(4));
        intent.putExtra("pressure", cursor.getString(5));
        intent.putExtra("windSpeed", cursor.getString(6));

        //detailed view
        startActivity(intent);
    }

}