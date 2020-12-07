package com.example.homework;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private String imgID;

    private double tempChange,
            feelsTempChange,
            lon,
            lat;
    private TextView cityName,
            temperature,
            pressureInfo,
            humidityInfo,
            windInfo,
            feelsLikeInfo,
            weatherDescription;
    private Integer responseCode;
    private ImageView weatherImage;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7290b9")));

        cityName = findViewById(R.id.cityName);
        temperature = findViewById(R.id.temperature);
        feelsLikeInfo = findViewById(R.id.feelsLike);
        windInfo = findViewById(R.id.windInfo);
        humidityInfo = findViewById(R.id.humidityInfo);
        pressureInfo = findViewById(R.id.pressureInfo);
        weatherDescription = findViewById(R.id.weatherDesription);
        weatherImage = findViewById(R.id.weatherImage);

        Button btnCityChange = findViewById(R.id.buttonCityChange);
        final Button btnCelsius = findViewById(R.id.buttonCelsius);
        final Button btnFahrenheit = findViewById(R.id.buttonFarenheit);
        Button btnGeolocation = findViewById(R.id.buttonGeoloc);


        Intent setName = getIntent();
        String cName = setName.getStringExtra("getCityName");
        if(cName ==null) {
            cName = "Омск";
        }

        if (!cityName.getText().toString().isEmpty())
            cName = cityName.getText().toString();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnCelsius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (responseCode != 404) {
                        btnFahrenheit.setBackgroundResource(R.drawable.radius_right);
                        btnFahrenheit.setTextColor(Color.parseColor("#aabcd5"));
                        btnCelsius.setBackgroundResource(R.drawable.radius_left);
                        btnCelsius.setTextColor(Color.parseColor("#ffffff"));
                        Picasso.with(v.getContext()).load(imgID).into(weatherImage);
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        temperature.setText(decimalFormat.format(tempChange).concat("°C"));
                        feelsLikeInfo.setText(decimalFormat.format(feelsTempChange).concat("°C"));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        btnFahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (responseCode != 404) {
                        btnFahrenheit.setBackgroundResource(R.drawable.second_radius_right);
                        btnFahrenheit.setTextColor(Color.parseColor("#ffffff"));
                        btnCelsius.setBackgroundResource(R.drawable.second_radius_left);
                        btnCelsius.setTextColor(Color.parseColor("#aabcd5"));
                        Picasso.with(v.getContext()).load(imgID).into(weatherImage);
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        temperature.setText(decimalFormat.format(getFarenheitTemperature(tempChange)).concat("°F"));
                        feelsLikeInfo.setText(decimalFormat.format(getFarenheitTemperature(feelsTempChange)).concat("°F"));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        btnGeolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(lon>0.0&& lat>0.0) {
                        btnFahrenheit.setBackgroundResource(R.drawable.radius_right);
                        btnFahrenheit.setTextColor(Color.parseColor("#aabcd5"));
                        btnCelsius.setBackgroundResource(R.drawable.radius_left);
                        btnCelsius.setTextColor(Color.parseColor("#ffffff"));
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        System.out.println("lat= " + decimalFormat.format(lat) + "\nlon=" + decimalFormat.format(lon + 0.3));
                        getWeather(null,
                                Double.parseDouble(decimalFormat.format(lat).replace(",", ".")),
                                Double.parseDouble(decimalFormat.format(lon + 0.3).replace(",", ".")));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        getWeather(cName, 0.0, 0.0);
        try {
            Picasso.with(this).load(imgID).into(weatherImage);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        btnCityChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //отправляем информацию на вторую активность
                Intent putIntent = new Intent(MainActivity.this, SelectCityActivity.class);
                putIntent.putExtra("temp",temperature.getText().toString());
                putIntent.putExtra("descr",weatherDescription.getText().toString());
                putIntent.putExtra("feels_like",feelsLikeInfo.getText().toString());
                putIntent.putExtra("windSpeed",windInfo.getText().toString());
                putIntent.putExtra("humid",humidityInfo.getText().toString());
                putIntent.putExtra("press",pressureInfo.getText().toString());
                putIntent.putExtra("img",imgID);

                startActivity(putIntent);
            }
        });

    }

    //трансформация °C в °F
    public double getFarenheitTemperature(double i){
        i=(32.0 + 1.8*i);
        return i;
    }


    //запрос на получение json, его обработка и установка полученных значений в TextView's
    public void getWeather(String nameOfCity, double lat, double lon){
        String URL;

        // metric - для градусов цельсия, imperial - фаренгейты
        // ссылка для картинок: "https://openweathermap.org/img/wn/"+iconId+"@2x.png"
        if(nameOfCity==null){
            URL =
              "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+
                      lon+"&lang=ru&units=metric&appid=b571349819d6c5df2864688943db0912";
        } else
            URL =
              "http://api.openweathermap.org/data/2.5/weather?q="+nameOfCity+"&lang=ru&units=metric&appid=b571349819d6c5df2864688943db0912";

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, URL, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            responseCode = response.getInt("cod");
                            if(responseCode!=404) {
                                JSONArray jArray = response.getJSONArray("weather");
                                JSONObject weatherObject = jArray.getJSONObject(0);
                                JSONObject mainObject = response.getJSONObject("main");
                                JSONObject windObject = response.getJSONObject("wind");

                                String city = response.getString("name");
                                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                tempChange = Double.parseDouble(decimalFormat.format(mainObject.getDouble("temp"))
                                        .replace(",","."));
                                feelsTempChange = Double.parseDouble(decimalFormat.format(mainObject.getDouble("feels_like"))
                                        .replace(",","."));
                                String temp = decimalFormat.format(tempChange).concat("°C");
                                String feelsLike = decimalFormat.format(feelsTempChange).concat("°C");

                                String press = String.valueOf((int) (mainObject.getDouble("pressure") *
                                        (0.7500616827041699))).concat(" мм.рт.ст.");
                                String humid = String.valueOf(mainObject.getInt("humidity")).concat("%");
                                String descr = weatherObject.getString("description");
                                String wind = windObject.getString("speed").concat(" м/с");

                                imgID = "https://openweathermap.org/img/wn/" + weatherObject.getString("icon") + "@2x.png";

                                try {
                                    Picasso.with(MainActivity.this).load(imgID).into(weatherImage);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                cityName.setText(city);
                                weatherDescription.setText(descr);
                                windInfo.setText(wind);
                                temperature.setText(temp);
                                feelsLikeInfo.setText(feelsLike);
                                humidityInfo.setText(humid);
                                pressureInfo.setText(press);
                            } else cityName.setText("Город не найден");
                        } catch (JSONException e) {
                            cityName.setText("Введите снова");
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cityName.setText("Не найдено");
                error.printStackTrace();
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jr);
    }

    //Работа с геолокацией, запрос на получение доступа к геолокации и получениее lat, lon
    final int PERMISSION_REQUEST = 1;
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NotNull Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(@NotNull String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(@NotNull String provider) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            } else {
                checkEnabled();
                showLocation(locationManager.getLastKnownLocation(provider));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                System.out.println("Status: " + status);
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                System.out.println("Status: " + status);
            }
        }
    };
    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            formatLocation(location);
            System.out.println(lat+" -lat, lon-  "+  lon);
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            formatLocation(location);
            System.out.println(lat+" -lat, lon-  "+  lon);
        }
    }

    private void formatLocation(Location location) {
        if (location == null)
            return;
        lat=location.getLatitude();
        lon=location.getLongitude();
    }

    private void checkEnabled() {
        System.out.println("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        System.out.println("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}