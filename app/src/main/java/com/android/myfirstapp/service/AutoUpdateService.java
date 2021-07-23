package com.android.myfirstapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.SunMoon;
import com.android.myfirstapp.http.ViewUpdate;
import com.android.myfirstapp.http.impl.HeHelper;
import com.android.myfirstapp.utils.ContentUtils;
import com.android.myfirstapp.utils.DateUtils;
import com.android.myfirstapp.utils.SPUtils;
import com.android.myfirstapp.utils.store.WeatherStore;

import java.util.List;


public class AutoUpdateService extends Service implements ViewUpdate {
    int interval = 8;

    HeHelper heHelper;
    WeatherStore store;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        heHelper = new HeHelper(AutoUpdateService.this,this);
        store = new WeatherStore(AutoUpdateService.this);

        updateLocationWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour = 60*60*1000;
        long tirggerAtTime = SystemClock.elapsedRealtime() + hour * interval;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME,tirggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateLocationWeather(){
        City city = store.getCity();
        SPUtils.putString(AutoUpdateService.this,"updateTime",DateUtils.formatUTC(System.currentTimeMillis(),""));

        heHelper.getWeatherNow(city);
        heHelper.getWeather3D(city.makeLocation());
        heHelper.getAirNow(city.makeLocation());
        heHelper.getSunMoon(city.makeLocation());
        heHelper.getWeather15D(city.makeLocation());
        heHelper.getWeather24Hourly(city.makeLocation());
    }

    @Override
    public void update(int what, Object obj) {
        if(what!= ContentUtils.FAIL_GET) {
            switch (what){
                case ContentUtils.BASIC_WEATHER:
                    store.storeBasicInfo(obj);
                    break;
                case ContentUtils.FORECAST_DAY_3:
                    store.storeDay((List) obj,3);
                    break;
                case ContentUtils.FORECAST_DAY_15:
                    store.storeDay((List) obj,15);
                    break;
                case ContentUtils.FORECAST_HOUR:
                    store.storeHour((List) obj);
                    break;
                case ContentUtils.SUN_MOON:
                    store.storeSunMoon(obj);
                    break;
                case ContentUtils.AQI_INFO:
                    store.storeAqi(obj);
                    break;
            }
        }
    }
}