package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private static final String API_KEY = "YOUR_API_KEY";

    private Button refreshButton;

    // Today
    private TextView cityNameText, temperatureText, descriptionText, humidityValueText, windValueText, rainValueText;
    private ImageView weatherIcon;
    private EditText cityNameInput;

    // First day
    private TextView firstDayText, firstDayDescText, firstDayMinTempText, firstDayMaxTempText;
    private ImageView firstDayIcon;

    // Second day
    private TextView secondDayText, secondDayDescText, secondDayMinTempText, secondDayMaxTempText;
    private ImageView secondDayIcon;

    // Third day
    private TextView thirdDayText, thirdDayDescText, thirdDayMinTempText, thirdDayMaxTempText;
    private ImageView thirdDayIcon;

    // Fourth day
    private TextView fourthDayText, fourthDayDescText, fourthDayMinTempText, fourthDayMaxTempText;
    private ImageView fourthDayIcon;

    // Hours
    private TextView firstHourText, firstHourValueText, secondHourText, secondHourValueText, thirdHourText, thirdHourValueText,
            fourthHourText, fourthHourValueText;
    private ImageView firstHourIcon,secondHourIcon,thirdHourIcon,fourthHourIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        refreshButton = findViewById(R.id.searchButton);
        cityNameInput = findViewById(R.id.cityNameInput);

        // Today
        cityNameText = findViewById(R.id.cityNameText);
        temperatureText = findViewById(R.id.temperatureText);
        descriptionText = findViewById(R.id.descriptionText);
        humidityValueText = findViewById(R.id.humidityValueText);
        windValueText = findViewById(R.id.windValueText);
        rainValueText = findViewById(R.id.rainValueText);
        weatherIcon = findViewById(R.id.weatherIcon);

        // First Day
        firstDayText = findViewById(R.id.firstDayText);
        firstDayIcon = findViewById(R.id.firstDayIcon);
        firstDayDescText = findViewById(R.id.firstDayDescText);
        firstDayMinTempText = findViewById(R.id.firstDayMinTempText);
        firstDayMaxTempText = findViewById(R.id.firstDayMaxTempText);

        // Second Day
        secondDayText = findViewById(R.id.secondDayText);
        secondDayIcon = findViewById(R.id.secondDayIcon);
        secondDayDescText = findViewById(R.id.secondDayDescText);
        secondDayMinTempText = findViewById(R.id.secondDayMinTempText);
        secondDayMaxTempText = findViewById(R.id.secondDayMaxTempText);

        // Third Day
        thirdDayText = findViewById(R.id.thirdDayText);
        thirdDayIcon = findViewById(R.id.thirdDayIcon);
        thirdDayDescText = findViewById(R.id.thirdDayDescText);
        thirdDayMinTempText = findViewById(R.id.thirdDayMinTempText);
        thirdDayMaxTempText = findViewById(R.id.thirdDayMaxTempText);

        // Fourth Day
        fourthDayText = findViewById(R.id.fourthDayText);
        fourthDayIcon = findViewById(R.id.fourthDayIcon);
        fourthDayDescText = findViewById(R.id.fourthDayDescText);
        fourthDayMinTempText = findViewById(R.id.fourthDayMinTempText);
        fourthDayMaxTempText = findViewById(R.id.fourthDayMaxTempText);


        // Hours
        firstHourText = findViewById(R.id.firstHourText);
        firstHourValueText = findViewById(R.id.firstHourValueText);
        firstHourIcon = findViewById(R.id.firstHourIcon);

        secondHourText = findViewById(R.id.secondHourText);
        secondHourValueText = findViewById(R.id.secondHourValueText);
        secondHourIcon = findViewById(R.id.secondHourIcon);

        thirdHourText = findViewById(R.id.thirdHourText);
        thirdHourValueText = findViewById(R.id.thirdHourValueText);
        thirdHourIcon = findViewById(R.id.thirdHourIcon);

        fourthHourText = findViewById(R.id.fourthHourText);
        fourthHourValueText = findViewById(R.id.fourthHourValueText);
        fourthHourIcon = findViewById(R.id.fourthHourIcon);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = cityNameInput.getText().toString();
                if(!cityName.isEmpty()){
                    FetchCurrentWeatherData(cityName);
                }
                else{
                    cityNameInput.setError("Please enter a city name");
                }
            }
        });

        FetchCurrentWeatherData("Bucharest");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void FetchCurrentWeatherData(String cityName) {

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid="+ API_KEY + "&units=metric";

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->
                {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    try {
                        Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        runOnUiThread(() -> updateUI(result));
                    } catch (IOException e)
                    {
                        e.printStackTrace();;
                    }
                }
        );

    }


    private void updateUI(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray forecastList = jsonObject.getJSONArray("list");

                cityNameText.setText(jsonObject.getJSONObject("city").getString("name"));

                double temp_min = 0, temp_max = 0;

                for (int i = 0; i <= forecastList.length(); i += 1) { // La fiecare 8 iteratii se schimba ziua (interval 3 ore)
                    JSONObject forecast = forecastList.getJSONObject(i);
                    String date = forecast.getString("dt_txt").split(" ")[0];
                    String unformattedHour = forecast.getString("dt_txt").split(" ")[1];
                    String hour = unformattedHour.split(":")[0] + ":" + unformattedHour.split(":")[1];
                    JSONObject main = forecast.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    double humidity = main.getDouble("humidity");
                    double rain = forecast.getDouble("pop");
                    double windSpeed = forecast.getJSONObject("wind").getDouble("speed");
                    String description = forecast.getJSONArray("weather").getJSONObject(0).getString("description");
                    String iconCode = forecast.getJSONArray("weather").getJSONObject(0).getString("icon");

                    String resourceName = "ic_" + iconCode;
                    int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());


                    // Today
                    if(i==0){
                        temperatureText.setText(String.format("%.0f°", temperature));
                        humidityValueText.setText(String.format("%.0f%%", humidity));
                        windValueText.setText(String.format("%.0f km/h", windSpeed));
                        rainValueText.setText(String.format("%.0f%%", rain * 100));
                        descriptionText.setText(description);

                        if (resId != 0) {
                            weatherIcon.setImageResource(resId);
                        } else {
                            weatherIcon.setImageResource(R.drawable.rain);
                        }

                    }

                    // First Hour
                    if(i==1){
                        firstHourValueText.setText(String.format("%.0f°", temperature));
                        firstHourText.setText(hour);

                        if (resId != 0) {
                            firstHourIcon.setImageResource(resId);
                        } else {
                            firstHourIcon.setImageResource(R.drawable.rain);
                        }

                    }


                    // Second Hour
                    if(i==2){
                        secondHourValueText.setText(String.format("%.0f°", temperature));
                        secondHourText.setText(hour);

                        if (resId != 0) {
                            secondHourIcon.setImageResource(resId);
                        } else {
                           secondHourIcon.setImageResource(R.drawable.rain);
                        }

                    }

                    // Third Hour
                    if(i==3){
                        thirdHourValueText.setText(String.format("%.0f°", temperature));
                        thirdHourText.setText(hour);

                        if (resId != 0) {
                            thirdHourIcon.setImageResource(resId);
                        } else {
                           thirdHourIcon.setImageResource(R.drawable.rain);
                        }

                    }

                    // Fourth Hour
                    if(i==4){
                        fourthHourValueText.setText(String.format("%.0f°", temperature));
                        fourthHourText.setText(hour);

                        if (resId != 0) {
                            fourthHourIcon.setImageResource(resId);
                        } else {
                            fourthHourIcon.setImageResource(R.drawable.rain);
                        }

                    }


                    if (temperature > temp_max){
                        temp_max = temperature;
                    }
                    else if(temperature< temp_min){
                        temp_min = temperature;
                    }


                    // First Day
                    if (i==8){
                        firstDayText.setText(String.format("%s", date));
                        firstDayDescText.setText(description);

                        if (resId != 0) {
                            firstDayIcon.setImageResource(resId);
                        } else {
                            firstDayIcon.setImageResource(R.drawable.rain);
                        }
                        firstDayMinTempText.setText(String.format("%.0f°", temp_min));
                        firstDayMaxTempText.setText(String.format("%.0f°", temp_max));
                        temp_min = temp_max = temperature;
                    }

                    // Second Day
                    if(i==16){
                        secondDayMinTempText.setText(String.format("%.0f°", temp_min));
                        secondDayMaxTempText.setText(String.format("%.0f°", temp_max));
                        temp_min = temp_max = temperature;

                        secondDayText.setText(String.format("%s", date));
                        secondDayDescText.setText(description);

                        if (resId != 0) {
                            secondDayIcon.setImageResource(resId);
                        } else {
                            secondDayIcon.setImageResource(R.drawable.rain);
                        }
                    }

                    // Third Day
                    if(i==24){
                        thirdDayMinTempText.setText(String.format("%.0f°", temp_min));
                        thirdDayMaxTempText.setText(String.format("%.0f°", temp_max));
                        temp_min = temp_max = temperature;

                        thirdDayText.setText(String.format("%s", date));
                        thirdDayDescText.setText(description);

                        if (resId != 0) {
                            thirdDayIcon.setImageResource(resId);
                        } else {
                            thirdDayIcon.setImageResource(R.drawable.rain);
                        }

                    }

                    //Fourth Day
                    if(i==32){
                        fourthDayMinTempText.setText(String.format("%.0f°", temp_min));
                        fourthDayMaxTempText.setText(String.format("%.0f°", temp_max));
                        temp_min = temp_max = temperature;

                        fourthDayText.setText(String.format("%s", date));
                        fourthDayDescText.setText(description);

                        if (resId != 0) {
                            fourthDayIcon.setImageResource(resId);
                        } else {
                            fourthDayIcon.setImageResource(R.drawable.rain);
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
