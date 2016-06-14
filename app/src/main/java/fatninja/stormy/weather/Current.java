package fatninja.stormy.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import fatninja.stormy.R;

/**
 * Created by Juan on 20/05/2016.
 */
public class Current {
    private String mIcon;
    private Long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipitationChance;
    private String mSumary;

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    private String mTimezone;

    public String getIcon() {

        return mIcon;
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);

    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public Long getTime() {
        return mTime;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        Date dateTime = new Date(getTime()*1000) ;
        String  timeString = formatter.format(dateTime);
        return timeString;
    }

    public void setTime(Long time) {
        mTime = time;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature) ;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipitationChance() {
        double precipPercentage = mPrecipitationChance * 100;
        return (int) Math.round(precipPercentage) ;
    }

    public void setPrecipitationChance(double precipitationChance) {
        mPrecipitationChance = precipitationChance;
    }

    public String getSumary() {
        return mSumary;
    }

    public void setSumary(String sumary) {
        mSumary = sumary;
    }
}
