package com.android.myfirstapp.http.impl;

import android.content.Context;
import android.os.Handler;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BaseHttpBean;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.ForecastHour;
import com.android.myfirstapp.bean.SunMoon;
import com.android.myfirstapp.http.HttpHelper;
import com.android.myfirstapp.myApplication;
import com.android.myfirstapp.utils.ContentUtils;
import com.android.myfirstapp.utils.DateUtils;
import com.android.myfirstapp.utils.SPUtils;
import com.android.myfirstapp.utils.WeatherHandlerUtils;
import com.android.myfirstapp.view.activity.WeatherActivity;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.sunmoon.SunMoonBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.List;


public class HeHelper implements HttpHelper {
    private Context context;
    private Handler handler;

    public HeHelper(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    //proxy
    public void sendMsg(int what,Object obj){
        switch (what){
            case ContentUtils.AQI_INFO:
            case ContentUtils.SUN_MOON:
            case ContentUtils.BASIC_WEATHER:
                if(obj instanceof BaseHttpBean){
                    if(((BaseHttpBean) obj).getCode()==ContentUtils.HTTPOK) {
                        handler.sendMessage(handler.obtainMessage(what, obj));
                    }else
                        handler.sendMessage(handler.obtainMessage(ContentUtils.FAIL_GET,((BaseHttpBean) obj).getreText()));
                }
                break;
            case ContentUtils.FORECAST_DAY:
                if(obj instanceof List && ((List) obj).get(0) instanceof ForecastDay){
                    if(((List) obj).size()==3){
                        handler.sendMessage(handler.obtainMessage(what, obj));
                    }else{
                        handler.sendMessage(handler.obtainMessage(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.bean_fail)));
                    }
                }
                break;
            case ContentUtils.FORECAST_HOUR:
                if(obj instanceof List && ((List) obj).get(0) instanceof ForecastHour){
                    if(((List) obj).size()==24){
                        handler.sendMessage(handler.obtainMessage(what, obj));
                    }else{
                        handler.sendMessage(handler.obtainMessage(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.bean_fail)));
                    }
                }
                break;
            case ContentUtils.FAIL_GET:
            default:
                handler.sendMessage(handler.obtainMessage(what, obj));

        }
    }

    @Override
    public void getWeatherNow(City city) {
        final BasicInfo basicInfo = new BasicInfo();
        basicInfo.setCity(city.getName()).setDistrict(city.getDistrict());
        QWeather.getWeatherNow(context, city.makeLocation(), new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();

                basicInfo.setCode(ContentUtils.HTTPFAIL);
                basicInfo.setreText(myApplication.getContext().getResources().getString(R.string.net_fail));
                sendMsg(ContentUtils.FAIL_GET,basicInfo);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                if(weatherNowBean!=null && Code.OK.getCode().equalsIgnoreCase(weatherNowBean.getCode().getCode())){
                    basicInfo.setCode(ContentUtils.HTTPOK);

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    basicInfo.setUpdateTime(now.getObsTime().substring(0,10))
                            .setText(now.getText())
                            .setIconCode(now.getIcon())
                            .setNowTemperature(now.getTemp())
                            .setWindDir(now.getWindDir())
                            .setWindScale(now.getWindScale());

                }else {
                    basicInfo.setCode(ContentUtils.HTTPFAIL);
                    basicInfo.setreText(myApplication.getContext().getResources().getString(R.string.bean_fail));
                }
                sendMsg(ContentUtils.BASIC_WEATHER, basicInfo);
            }
        });
    }

    @Override
    public void getWeather3D(String location) {
        QWeather.getWeather3D(context, location, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.net_fail));
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                List<ForecastDay> list = WeatherHandlerUtils.getWeatherDayList(weatherDailyBean);
                sendMsg(ContentUtils.FORECAST_DAY,list);
            }
        });
    }

    @Override
    public void getWeather15D(String location) {
        QWeather.getWeather15D(context, location, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.net_fail));
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                List<ForecastDay> list = WeatherHandlerUtils.getWeatherDayList(weatherDailyBean);
                if(list.size()==15)
                   ;//todo receiver
                else
                sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.bean_fail));
            }
        });
    }

    @Override
    public void getWeather24Hourly(String location) {
        QWeather.getWeather24Hourly(context, location, new QWeather.OnResultWeatherHourlyListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.net_fail));
            }

            @Override
            public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                List<ForecastHour> list = WeatherHandlerUtils.getWeatherHourly(weatherHourlyBean);
                if(list.size()==24)
                    sendMsg(ContentUtils.FORECAST_HOUR,list);
                else
                    sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.bean_fail));
            }
        });
    }

    @Override
    public void getAirNow(String location) {
        QWeather.getAirNow(context, location, null, new QWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.net_fail));
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                Aqi aqi = WeatherHandlerUtils.getAirNow(airNowBean);
                sendMsg(ContentUtils.AQI_INFO,aqi);
            }
        });
    }

    @Override
    public void getSunMoon(String location) {
        QWeather.getSunMoon(context, location, DateUtils.formatUTC(System.currentTimeMillis(), "yyyyMMdd"), new QWeather.OnResultSunMoonListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                sendMsg(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.net_fail));
            }

            @Override
            public void onSuccess(SunMoonBean sunMoonBean) {
                SunMoon sunMoon = WeatherHandlerUtils.getSunMoon(sunMoonBean);
                sendMsg(ContentUtils.SUN_MOON,sunMoon);
            }
        });
    }


}
