package fatninja.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.Arrays;

import fatninja.stormy.R;
import fatninja.stormy.adapters.DayAdapter;
import fatninja.stormy.weather.Day;

public class DailyForecastActivity extends ListActivity {

    Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        /*String[] daysOfTheWeek = {"Domingo","Lunes","Martes","Miercoles","Juevces","Viernes","Sabado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,daysOfTheWeek);
        setListAdapter(adapter);*/
        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);
        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);
    }
}
