package com.example.getweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/*
* This Class is used to create the detailed city view screen
* */
public class CityDetailsActivity extends AppCompatActivity {

    TextView name, description, temperature, feelsLike, humidity, pressure, windSpeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        name = findViewById(R.id.name);
        description = findViewById(R.id.desc);
        temperature = findViewById(R.id.temp);
        feelsLike = findViewById(R.id.feelsLike);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        windSpeed = findViewById(R.id.windSpeed);

        name.setText(getIntent().getStringExtra("name"));
        description.setText(getIntent().getStringExtra("description"));
        temperature.setText(getIntent().getStringExtra("temperature"));
        feelsLike.setText(getIntent().getStringExtra("feelsLike"));
        humidity.setText(getIntent().getStringExtra("humidity"));
        pressure.setText(getIntent().getStringExtra("pressure"));
        windSpeed.setText(getIntent().getStringExtra("windSpeed"));
    }
}