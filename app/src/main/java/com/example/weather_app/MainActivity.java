package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;


    class getWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try{
                URL url= new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line="";
                while((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double tempKelvin = main.getDouble("temp");
                double tempCelsius = tempKelvin - 273.15; // Convert Kelvin to Celsius
                String temperature = "Temperature : " + String.format("%.2f", tempCelsius) + " °C";

                String feelsLikeKelvin = main.getString("feels_like");
                double feelsLikeCelsius = Double.parseDouble(feelsLikeKelvin) - 273.15; // Convert Kelvin to Celsius
                String feelsLike = "Feels Like : " + String.format("%.2f", feelsLikeCelsius) + " °C";

                String tempMaxKelvin = main.getString("temp_max");
                double tempMaxCelsius = Double.parseDouble(tempMaxKelvin) - 273.15; // Convert Kelvin to Celsius
                String tempMax = "Temperature Max : " + String.format("%.2f", tempMaxCelsius) + " °C";

                String tempMinKelvin = main.getString("temp_min");
                double tempMinCelsius = Double.parseDouble(tempMinKelvin) - 273.15; // Convert Kelvin to Celsius
                String tempMin = "Temperature Min : " + String.format("%.2f", tempMinCelsius) + " °C";

                String pressure = "Pressure : " + main.getString("pressure") + " hPa";
                String humidity = "Humidity : " + main.getString("humidity") + " %";

                String weatherInfo = temperature + "\n" + feelsLike + "\n" + tempMax + "\n" + tempMin + "\n" + pressure + "\n" + humidity;
                show.setText(weatherInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        final String[] temp={""};

        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Button Clicked! ", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try{
                    if(city!=null){
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=8aa62e5b1a70bac2d484beb833697f7a";
                    }else{
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task= new getWeather();
                    temp[0] = task.execute(url).get();
                }catch(ExecutionException e){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Cannot able to find Weather");
                }

            }
        });

    }
}
