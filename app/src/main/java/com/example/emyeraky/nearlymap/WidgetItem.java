package com.example.emyeraky.nearlymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WidgetItem extends AppCompatActivity {
    TextView res, hotel, travel, hos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_item);
        setTheme(R.style.AppTheme);
        res = (TextView) findViewById(R.id.restuarant);
        hotel = (TextView) findViewById(R.id.hotel);
        travel = (TextView) findViewById(R.id.cafe);
        hos = (TextView) findViewById(R.id.hospital);

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent(WidgetItem.this, SplashScreen.class);
                MapsActivity.status = "food";
                startActivity(intentMap);

            }
        });

        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.status = "lodging";
                Intent intent = new Intent(WidgetItem.this, SplashScreen.class);
                startActivity(intent);
                finish();

            }
        });
        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.status = "travel_agency";
                Intent intent = new Intent(WidgetItem.this, SplashScreen.class);
                startActivity(intent);
                finish();

            }
        });
        hos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.status = "hospital";
                Intent intent = new Intent(WidgetItem.this, SplashScreen.class);
                startActivity(intent);
                finish();
            }
        });

    }
}

