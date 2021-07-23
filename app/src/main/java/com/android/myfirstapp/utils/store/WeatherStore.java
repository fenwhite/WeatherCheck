package com.android.myfirstapp.utils.store;

import android.content.Context;

import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.ForecastHour;
import com.android.myfirstapp.bean.SunMoon;
import com.android.myfirstapp.utils.ContentUtils;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class WeatherStore extends BaseStore {
    private String pre;

    public WeatherStore(Context context) {
        super(context);
        this.pre = "";
    }

    public WeatherStore(Context context,  String pre) {
        super(context);
        this.pre = pre;
    }

    public WeatherStore(Context context, boolean isValid, String pre) {
        super(context, isValid);
        this.pre = pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPre() {
        return pre;
    }

    public void storeBasicInfo(Object obj){
        store(ContentUtils.BASIC_WEATHER,obj);
    }
    public BasicInfo getBasicInfo(){
        BasicInfo obj = getBean(pre+ContentUtils.BASIC_WEATHER,BasicInfo.class);
        return obj;
    }

    public void storeCity(Object obj){
        store(ContentUtils.CITY,obj);
    }
    public City getCity(){
        City obj = getBean(pre+"City",City.class);
        return obj;
    }

    public void storeDay(List obj,int days){
        int flag = days==3? ContentUtils.FORECAST_DAY_3:ContentUtils.FORECAST_DAY_15;
        store(flag,obj);
    }
    public List<ForecastDay> getStoreDay(int days){
        int flag = days==3? ContentUtils.FORECAST_DAY_3:ContentUtils.FORECAST_DAY_15;
        List<ForecastDay> list = getListBean(pre+flag,new TypeToken<List<ForecastDay>>() {}.getType());
        return list;
    }

    public void storeHour(List obj){
        store(ContentUtils.FORECAST_HOUR,obj);
    }
    public List<ForecastHour> getStoreHour(){
        List<ForecastHour> list = getListBean(pre+ContentUtils.FORECAST_HOUR,new TypeToken<List<ForecastHour>>() {}.getType());
        return list;
    }

    public void storeAqi(Object obj){
         store(ContentUtils.AQI_INFO,obj);
    }
    public Aqi getAqi(){
        Aqi obj = getBean(pre+ContentUtils.AQI_INFO,Aqi.class);
        return obj;
    }

    public void storeSunMoon(Object obj){
        store(ContentUtils.SUN_MOON,obj);
    }
    public SunMoon getSunMoon(){
        SunMoon obj = getBean(pre+ContentUtils.SUN_MOON,SunMoon.class);
        return obj;
    }

    protected void store(int what,Object obj){
        if(!isValid())
            return;
        switch (what){
            case ContentUtils.SUN_MOON:
            case ContentUtils.AQI_INFO:
            case ContentUtils.BASIC_WEATHER:
            case ContentUtils.CITY:
                saveBean(pre+what,obj);
                break;
            case ContentUtils.FORECAST_DAY_3:
            case ContentUtils.FORECAST_DAY_15:
            case ContentUtils.FORECAST_HOUR:
                putListBean(pre+what,(List) obj);
                break;
        }
    }
}
