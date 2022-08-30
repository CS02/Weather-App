package com.example.getweather;

import static android.os.Looper.getMainLooper;
import static android.os.Looper.myLooper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Adapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//This class is responsible for running the background tasks
//such as getting location and updating data every minute
/*
This class is responsible for getting the
longitude and latitude values of the phone's location
It also asks/checks for location persmission
*/
public class BackgroundTaskHandler {

    Context context;
    DataBaseHandler dataBase;
    Activity activity;
    MyAdapter adapter;
    static int counter = 0;

    Handler handler;

    public BackgroundTaskHandler(Context context, Handler handler, MyAdapter adapter) {
        this.dataBase = DataBaseHandler.getInstance(context);
        this.context = context;
        this.handler = handler;
        this.adapter = adapter;
    }

    public void updateDataBase(){

;        Runnable updatingRunnable = new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                while(true){
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        Log.i("Thread Exception", "run: ");
                    }
                    updateData();
                    Boolean bool = handler.sendEmptyMessage(0);
                    Log.i("Thread", bool.toString());
                    try{
                        Thread.sleep(59000);
                    }catch (Exception e){
                        Log.i("Thread Exception", "run: ");
                    }
                }
            }
        };
        Thread backgroundThread = new Thread(updatingRunnable);
        backgroundThread.start();
    }


    private void updateData(){
        Cursor cursor = dataBase.getdata();
        String url;
        String city;
        while (cursor.moveToNext()) {
            city = cursor.getString(0);
            Log.i("Updating", city);
            url = "https://api.openweathermap.org/data/2.5/weather?q=" +city+ "&mode=json&appid=0aabe8eb9754d936c0bff4cdcaac1293&units=metric";
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);

            // Request a string response from the provided URL.
            String finalCity = city;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //get objects from the json file
                                JSONObject mainObject = new JSONObject(response);
                                JSONObject main = mainObject.getJSONObject("main");
                                JSONObject weather = mainObject.getJSONArray("weather").getJSONObject(0);

                                dataBase.updateCityData(finalCity,
                                        weather.getString("description"),
                                        main.getString("temp") + "°C",
                                        main.getString("feels_like") + "°C",
                                        main.getString("humidity") + "%",
                                        main.getString("pressure") +" hPa",
                                        mainObject.getJSONObject("wind").getString("speed")+" m/s"
                                );
                                Log.i("Updated", finalCity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.i("Error Updating", "onResponse: ");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Volley Error", "onErrorResponse: ");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }


}
