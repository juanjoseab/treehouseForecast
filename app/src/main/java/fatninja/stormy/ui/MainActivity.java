package fatninja.stormy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fatninja.stormy.R;
import fatninja.stormy.weather.Current;
import fatninja.stormy.weather.Day;
import fatninja.stormy.weather.Forecast;
import fatninja.stormy.weather.Hour;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public String mTimezoneGlobal;

    private final OkHttpClient client = new OkHttpClient();

    //private Current mCurrent;
    private Forecast mForecast;
    private TextView mTemperatureLabel;
    private TextView mTimeLabel;
    private TextView mHumidityValue;
    private TextView mPrecipValue;
    private TextView mSummaryLabel;
    private ImageView mIconImageView;
    private ImageView mRefreshImageView;
    private ProgressBar mProgressBar;
    private double lat;
    private double lng;
    private  String apiKey;

    private Button mDailyButton;
    private Button mHourlyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiKey = "f43de85d296755d18224ac3206513f4c";
        lat = 37.8267;
        lng = -122.423;
        mTemperatureLabel = (TextView)findViewById(R.id.temperatureLabel);
        mTimeLabel = (TextView)findViewById(R.id.timeLabel);
        mHumidityValue = (TextView)findViewById(R.id.humidityValue);
        mPrecipValue = (TextView)findViewById(R.id.precipValue);
        mSummaryLabel = (TextView)findViewById(R.id.summaryLabel);
        mIconImageView = (ImageView)findViewById(R.id.iconImageView);
        mRefreshImageView = (ImageView)findViewById(R.id.refreshImageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDailyButton = (Button) findViewById(R.id.dailyButton);
        mHourlyButton = (Button) findViewById(R.id.hourlyButton);

        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForcast(apiKey, lat, lng);
            }
        });

        getForcast(apiKey, lat, lng);


        mDailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DailyForecastActivity.class);
                intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());

                startActivity(intent);
            }
        });
    }

    private void getForcast(String apiKey, double lat, double lng) {
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + lat + "," + lng;
        if(isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutErro();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.d(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast =  parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutErro();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Excepcion obtenida: ", e);

                    } catch (JSONException e) {
                        Log.e(TAG, "Excepcion obtenida: ", e);
                    }
                }
            });
        }else{
            Toast.makeText(
                    this,getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }else {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText(current.getTemperature() + "" );
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipitationChance() + "%");
        mSummaryLabel.setText(current.getSumary());
        Drawable drawable = ContextCompat.getDrawable(this, current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails (String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        //forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }



    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        //String timezone = forecast.getString("timezone");
        JSONObject hourlyData = forecast.getJSONObject("hourly");
        JSONArray data = hourlyData.getJSONArray("data");
        Hour[] hourly = new Hour[data.length()];
        for(int i= 0; i < data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);
            hourly[i] = getHourlyDetails(jsonHour);
        }
        return hourly;
    }

    private Hour getHourlyDetails(JSONObject data) throws JSONException {
        Hour hour = new Hour();        //hour.setTimezone(data.getString("timezone"));
        hour.setTime(data.getLong("time"));
        hour.setIcon(data.getString("icon"));
        hour.setSummary(data.getString("summary"));
        hour.setTemperature(data.getDouble("temperature"));
        return hour;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        //String timezone = forecast.getString("timezone");
        JSONObject hourlyData = forecast.getJSONObject("daily");
        JSONArray data = hourlyData.getJSONArray("data");
        Day[] daily = new Day[data.length()];
        for(int i= 0; i < data.length(); i++){
            JSONObject jsonDay = data.getJSONObject(i);
            daily[i] = getDailyDetails(jsonDay);
        }
        return daily;
    }

    private Day getDailyDetails(JSONObject data) throws JSONException {
        Day day = new Day();
        //hour.setTimezone(data.getString("timezone"));
        day.setTime(data.getLong("time"));
        day.setIcon(data.getString("icon"));
        day.setSummary(data.getString("summary"));
        day.setTimezone(mTimezoneGlobal);
        day.setTemperatureMax(data.getDouble("temperatureMax"));
        return day;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        mTimezoneGlobal = timezone;
        Log.i(TAG, "FROM JSON: " + timezone);
        JSONObject currently = forecast.getJSONObject("currently");
        Current current = new Current();

        current.setHumidity(currently.getDouble("humidity"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipitationChance(currently.getInt("precipProbability"));
        current.setSumary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTime(currently.getLong("time"));
        current.setTimezone(timezone);
        Log.d(TAG, current.getFormattedTime());
        return current;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;

    }

    private void alertUserAboutErro() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }
}
