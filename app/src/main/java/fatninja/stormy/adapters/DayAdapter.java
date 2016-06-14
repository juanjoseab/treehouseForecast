package fatninja.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import fatninja.stormy.R;
import fatninja.stormy.weather.Day;

/**
 * Created by Juan on 10/06/2016.
 */
public class DayAdapter extends BaseAdapter{

    Context mContext;
    Day[] mDays;

    public DayAdapter(Context context, Day[] days){
        this.mContext = context;
        this.mDays = days;
    }

    @Override
    public int getCount() {

        return this.mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return this.mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; //No vamos a usar este metodos, este metodo etiqueta items para facil referenciamiento
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
            Day day  = mDays[position];
            holder.iconImageView.setImageResource(day.getIconId());
            //holder.temperatureLabel.setText(day.getTemperatureMax() + "");
            holder.temperatureLabel.setText("PUNTO ");
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }




}




















