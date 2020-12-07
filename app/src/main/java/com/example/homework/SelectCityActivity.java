package com.example.homework;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectCityActivity extends AppCompatActivity {
    private String imgId,wind,pressure,temperature,
    feels_like,description,humidity;
    private ActionBar actionBar;
    private Integer responseCode;
    private Button btnOK;
    private ImageView weatherImage;
    private TextView temperatureInfo,
            pressureInfo,
            humidityInfo,
            windInfo,
            feelsLikeInfo,
            weatherDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7290b9")));
        btnOK = findViewById(R.id.okButton);




        Intent intent = new Intent();
        imgId=intent.getStringExtra("img");

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}