package com.example.homework;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SelectCityActivity extends AppCompatActivity {
    private EditText selectCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7290b9")));

        Button btnOK = findViewById(R.id.okButton);
        TextView temperatureInfo = findViewById(R.id.temperature);
        TextView feelsLikeInfo = findViewById(R.id.feelsLike);
        TextView windInfo = findViewById(R.id.windInfo);
        TextView humidityInfo = findViewById(R.id.humidityInfo);
        TextView pressureInfo = findViewById(R.id.pressureInfo);
        TextView weatherDescription = findViewById(R.id.weatherDesription);
        ImageView weatherImage = findViewById(R.id.weatherImage);
        selectCity = findViewById(R.id.selectCityName);

        Intent setIntent = getIntent();

        temperatureInfo.setText(setIntent.getStringExtra("temp"));
        weatherDescription.setText(setIntent.getStringExtra("descr"));
        feelsLikeInfo.setText(setIntent.getStringExtra("feels_like"));
        windInfo.setText(setIntent.getStringExtra("windSpeed"));
        humidityInfo.setText(setIntent.getStringExtra("humid"));
        pressureInfo.setText(setIntent.getStringExtra("press"));

        String imgID = setIntent.getStringExtra("img");
        try {
            Picasso.with(SelectCityActivity.this).load(imgID).into(weatherImage);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent putIntent = new Intent(SelectCityActivity.this, MainActivity.class);
                putIntent.putExtra("getCityName",selectCity.getText().toString());
                startActivity(putIntent);
            }
        });




    }
}