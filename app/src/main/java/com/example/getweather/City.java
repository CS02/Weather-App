package com.example.getweather;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/*
This class is responsible for getting a city's weather information
and inserting it into the database
This class can only write to the database
Constructor overLoading allows the API to be called using
a city's name or longitude and latitude values
*/
public class City {
        private String url;
        private DataBaseHandler dataBase;

        //inserts city into data base using city name
        public City(Context context, String cityName){
                dataBase = DataBaseHandler.getInstance(context);
                url = "https://api.openweathermap.org/data/2.5/weather?q=" +cityName+ "&mode=json&appid=0aabe8eb9754d936c0bff4cdcaac1293&units=metric";
                //get and insert data into data base
                getDataFromAPI(url, context);
        }

        //inserts city into databse using latitude and longitude
        public City(Context context, String latitude, String longitude){
                dataBase = DataBaseHandler.getInstance(context);
                url = "https://api.openweathermap.org/data/2.5/weather?lat=" +latitude+ "&lon=" +longitude+ "&appid=0aabe8eb9754d936c0bff4cdcaac1293&units=metric";
                //get and insert data into data base
                getDataFromAPI(url, context);
        }

        //Calls OpenWeather API
        //retrieves Data as a string which is then converted into JSON objects/arrays
        //specific weather data is then extracted and inserted into database
        private void getDataFromAPI(String url, Context context) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(context);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                        try {
                                                //get objects from the json file
                                                JSONObject mainObject = new JSONObject(response);
                                                JSONObject main = mainObject.getJSONObject("main");
                                                JSONObject weather = mainObject.getJSONArray("weather").getJSONObject(0);

                                                //insert values into database
                                                Boolean isInserted = dataBase.insertCityData(
                                                        mainObject.getString("name"),
                                                        weather.getString("description"),
                                                        main.getString("temp") + "°C",
                                                        main.getString("feels_like") + "°C",
                                                        main.getString("humidity") + "%",
                                                        main.getString("pressure") +" hPa",
                                                        mainObject.getJSONObject("wind").getString("speed")+" m/s"
                                                );


                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(context, "insertion failed", Toast.LENGTH_SHORT).show();
                                        }
                                }
                        }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                                Toast.makeText(context, "Volley Error Occured", Toast.LENGTH_SHORT).show();
                        }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
        }

}
