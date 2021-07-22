package com.android.myfirstapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.android.myfirstapp.utils.WeatherHandlerUtils;
import com.android.myfirstapp.view.activity.WeatherActivity;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.sunmoon.SunMoonBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.List;

public class AutoUpdateService extends Service implements ViewUpdate {
    int interval = 8;

    HeHelper heHelper;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        heHelper = new HeHelper(AutoUpdateService.this,this);

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
        final City city = SPUtils.getBean(AutoUpdateService.this, "City", City.class);
        SPUtils.putString(AutoUpdateService.this,"updateTime",DateUtils.formatUTC(System.currentTimeMillis(),""));

        heHelper.getWeatherNow(city);
        heHelper.getWeather3D(city.makeLocation());
        heHelper.getAirNow(city.makeLocation());
        heHelper.getSunMoon(city.makeLocation());
        heHelper.getWeather15D(city.makeLocation());

//        QWeather.getWeatherNow(AutoUpdateService.this, city.makeLocation(), new QWeather.OnResultWeatherNowListener() {
//            @Override
//            public void onError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onSuccess(WeatherNowBean weatherNowBean) {
//                BasicInfo basicInfo = WeatherHandlerUtils.getWeatherNow(weatherNowBean);
//                if(basicInfo!=null){
//                    basicInfo.setCity(city.getName()).setDistrict(city.getDistrict());
//                    SPUtils.saveBean(AutoUpdateService.this,"BasicInfo",basicInfo);
//                }
//            }
//        });
//
//        QWeather.getWeather3D(AutoUpdateService.this, city.makeLocation(), new QWeather.OnResultWeatherDailyListener() {
//            @Override
//            public void onError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onSuccess(WeatherDailyBean weatherDailyBean) {
//                List<ForecastDay> list = WeatherHandlerUtils.getWeatherDayList(weatherDailyBean);
//                if(list.size()>0){
//                    SPUtils.putListBean(AutoUpdateService.this,"List<ForecastDay>",list);
//                }
//            }
//        });
//
//        QWeather.getAirNow(AutoUpdateService.this, city.makeLocation(), null, new QWeather.OnResultAirNowListener() {
//            @Override
//            public void onError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onSuccess(AirNowBean airNowBean) {
//                Aqi aqi = WeatherHandlerUtils.getAirNow(airNowBean);
//                if(aqi!=null){
//                    SPUtils.saveBean(AutoUpdateService.this,"Aqi",aqi);
//                    aqi = null;
//
//                }
//            }
//        });
//
//        QWeather.getSunMoon(AutoUpdateService.this, city.makeLocation(), DateUtils.formatUTC(System.currentTimeMillis(), "yyyyMMdd"), new QWeather.OnResultSunMoonListener() {
//            @Override
//            public void onError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onSuccess(SunMoonBean sunMoonBean) {
//                SunMoon sunMoon = WeatherHandlerUtils.getSunMoon(sunMoonBean);
//                if(sunMoon!=null){
//                    SPUtils.saveBean(AutoUpdateService.this,"SunMoon",sunMoon);
//                }
//            }
//        });
    }

    @Override
    public void update(int what, Object obj) {
        if(what!= ContentUtils.FAIL_GET) {
            SPUtils.saveBean(AutoUpdateService.this, String.valueOf(what), obj);
            // 搞个气泡显示服务信息
        }
    }
}