package com.android.myfirstapp.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.android.myfirstapp.R;
import com.android.myfirstapp.adapter.ForecastDayAdapter;
import com.android.myfirstapp.adapter.ForecastHourAdapter;
import com.android.myfirstapp.bean.Aqi;
import com.android.myfirstapp.bean.BasicInfo;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.ForecastHour;
import com.android.myfirstapp.bean.SunMoon;
import com.android.myfirstapp.bean.Weather;
import com.android.myfirstapp.utils.DateUtils;
import com.android.myfirstapp.utils.SPUtils;
import com.android.myfirstapp.view.widget.CircleProgressView;
import com.android.myfirstapp.view.widget.LabelText;
import com.android.myfirstapp.view.widget.SunTrack;
import com.google.gson.reflect.TypeToken;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.sunmoon.SunMoonBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.LinkedList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";
    private static final int BASIC_WEATHER = 114514;
    public static final int FORECAST_DAY = 1;
    public static final int FORECAST_HOUR = 391314;
    public static final int AQI_INFO = 7;
    public static final int REFRESH_FINISH = 10;
    public static final int SUN_MOON = 8;

    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawer;
    public NestedScrollView weatherLayout;
    private TextView degree, elseInfo,regionName,updateTime;
    private ListView forecastDay;
    private RecyclerView forecastHour;
    private CircleProgressView progress;
    private LabelText primary,pm25,pm10,SO2;
    private TextView moonState;
    private LabelText sunRaise,sunset;
    private SunTrack sunTrack;
    private Button to15D,toAreaChoose;

    private AMapLocationClient client;
    private AMapLocationClientOption option;
    //存储数据模型
    private City city;
    private Weather weather;

    private boolean isFromSearch = false;

    private Handler handler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case REFRESH_FINISH:
                    swipeRefresh.setRefreshing(false);
                    break;
                case BASIC_WEATHER:
                    showBasic();
                    break;
                case FORECAST_DAY:
                    showForecastInDay();
                    break;
                case FORECAST_HOUR:
                    showForecastInHour();
                    break;
                case AQI_INFO:
                    showAQI();
                    break;
                case SUN_MOON:
                    showSunMoon();
                    break;
            }
            return false;
        }
    });

    private void showBasic(){
        BasicInfo basic = weather.getBasicInfo();
        degree.setText(basic.getNowTemperature());
        elseInfo.setText(basic.getText()+" "+basic.getWindDir());
        regionName.setText(basic.getCity()+basic.getDistrict());
        updateTime.setText(DateUtils.formatChinese(basic.getUpdateTime()));
    }

    private void showForecastInDay(){
        List<ForecastDay> list = weather.getforecastDayList();
        ForecastDayAdapter adapter = new ForecastDayAdapter(WeatherActivity.this,R.layout.forecast_day_item,list);
        forecastDay.setAdapter(adapter);
    }
    private void showForecastInHour(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        forecastHour.setLayoutManager(linearLayoutManager);

        List<ForecastHour> list = weather.getForecastHourList();
        ForecastHourAdapter adapter = new ForecastHourAdapter(R.id.forecast_hour,list);
        forecastHour.setAdapter(adapter);
    }

    private void showAQI(){
        Aqi aqi = weather.getAqi();
        progress.setValue(aqi.getApi());
        progress.setText(aqi.getCategory());
        progress.startAnimProgress(4000);

        primary.setText(aqi.getPrimary());
        pm25.setText(aqi.getPm25());
        pm10.setText(aqi.getPm10());
        SO2.setText(aqi.getSO2());
    }

    private void showSunMoon(){
        SunMoon obj = weather.getSunAndMoon();
        sunRaise.setText(obj.getSunRaiseTime().substring(11,16));
        sunset.setText(obj.getSunSetTime().substring(11,16));
        moonState.setText(obj.getMoonState());
        sunTrack.setStartTime(obj.getSunRaiseTime());
        sunTrack.setFinishTime(obj.getSunSetTime());
        sunTrack.startAnimation(8000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 顶部导航栏 disappear
        if(Build.VERSION.SDK_INT >= 30){
            getWindow().getDecorView().getWindowInsetsController().hide(
                    android.view.WindowInsets.Type.statusBars()
                            | android.view.WindowInsets.Type.navigationBars()
            );
        }else if(Build.VERSION.SDK_INT >= 16){
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setContentView(R.layout.activity_weather);

        drawer = findViewById(R.id.drawer_layout);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        weatherLayout = findViewById(R.id.weather_layout);
        degree = (TextView)findViewById(R.id.now_degree);
        elseInfo = (TextView)findViewById(R.id.else_info);
        regionName = (TextView)findViewById(R.id.region_name);
        updateTime = (TextView)findViewById(R.id.update_time);
        forecastDay = findViewById(R.id.forecast_day);
        forecastHour = findViewById(R.id.forecast_hour);
        progress = findViewById(R.id.aqi_progress);
        primary = findViewById(R.id.primary);
        pm25 = findViewById(R.id.pm25);
        pm10 = findViewById(R.id.pm10);
        SO2 = findViewById(R.id.SO2);
        sunRaise = findViewById(R.id.sun_raise);
        sunset = findViewById(R.id.sun_set);
        moonState = findViewById(R.id.moon_state);
        sunTrack = findViewById(R.id.sun_track);
        to15D = findViewById(R.id.to15D);
        toAreaChoose = findViewById(R.id.choose_area_button);


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (client == null)
                    initClient();
                client.startLocation();
                Log.d(TAG, "refresh once");
                handler.sendEmptyMessage(REFRESH_FINISH);
            }
        });
        to15D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,Forecast15Activity.class);
                startActivity(intent);
            }
        });
        toAreaChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(findViewById(R.id.choose_area));
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            this.city = (City) extras.getParcelable("city");
            toAreaChoose.setVisibility(View.INVISIBLE);
            isFromSearch = true;
        }else {
            this.city = SPUtils.getBean(this, "City", City.class);
            isFromSearch = false;
        }
        if(this.city ==null) {
            initClient();
            client.startLocation();
        }else{
            boolean isSendMsg = true;
            BasicInfo basicInfo = SPUtils.getBean(this,"BasicInfo",BasicInfo.class);
            isSendMsg = isSendMsg && (basicInfo!=null);

            List<ForecastDay> forecastDays = SPUtils.getListBean(this,
                    "List<ForecastDay>",
                    new TypeToken<List<ForecastDay>>() {}.getType());
            isSendMsg = isSendMsg && (forecastDays!=null);

            Aqi aqi = SPUtils.getBean(this,"Aqi",Aqi.class);
            isSendMsg = isSendMsg && (aqi!=null);

            SunMoon sunMoon = SPUtils.getBean(this,"SunMoon",SunMoon.class);
            isSendMsg = isSendMsg && (sunMoon!=null);

            if(!isFromSearch && isSendMsg){
                weather = new Weather();
                weather.setBasicInfo(basicInfo).setForecastDayList(forecastDays)
                        .setAqi(aqi).setSunAndMoon(sunMoon);

                handler.sendEmptyMessage(BASIC_WEATHER);
                handler.sendEmptyMessage(FORECAST_DAY);
                handler.sendEmptyMessage(AQI_INFO);
                handler.sendEmptyMessage(SUN_MOON);
            }else{
                Log.d(TAG, "update|location:"+city.makeLocation());
                updateWeatherInfo();
            }
        }
    }

    private void initClient(){
        client = new AMapLocationClient(getApplicationContext());
        optionBuild();
        client.setLocationOption(option);
        client.setLocationListener(listener);
    }

    private void optionBuild(){
        option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setHttpTimeOut(5000);
        option.setOnceLocation(true);
        option.setWifiScan(false);
    }

    private AMapLocationListener listener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            handler.sendEmptyMessage(REFRESH_FINISH);
            if(location!=null){
                if(location.getErrorCode()==0){
                    //success
                    client.stopLocation();

                    city = new City();
                    city.setLatitude(location.getLatitude()).setLongitude(location.getLongitude());
                    city.setDistrict(location.getDistrict()).setCityId(location.getCityCode()).setName(location.getCity()).setProvince(location.getProvince());
                    if(!isFromSearch)
                        SPUtils.saveBean(getApplicationContext(),"City",city);

                    updateWeatherInfo();
                }else{
                    //fail
                    Toast.makeText(WeatherActivity.this,"错误描述:" + location.getLocationDetail(),Toast.LENGTH_LONG).show();
                    Log.d(TAG, "错误码:" + location.getErrorCode() + "\n"+"错误信息:" + location.getErrorInfo() + "\n"+"错误描述:" + location.getLocationDetail()+ "\n");
                }
                Log.d(TAG, "once location finish!");
            }
        }
    };

    private void updateWeatherInfo(){
        weather = new Weather();
        // get basic weather info
        QWeather.getWeatherNow(this, city.makeLocation(), new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "get weather now |location:"+city.makeLocation());
                throwable.printStackTrace();
                Toast.makeText(WeatherActivity.this,getResources().getString(R.string.net_fail),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                if(weatherNowBean!=null && weatherNowBean.getCode().getCode().equals("200")){
                    BasicInfo basicInfo = new BasicInfo();
                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    basicInfo.setUpdateTime(now.getObsTime().substring(0,10))
                             .setCity(city.getName())
                             .setDistrict(city.getDistrict())
                             .setText(now.getText())
                             .setIconCode(now.getIcon())
                             .setNowTemperature(now.getTemp())
                             .setWindDir(now.getWindDir())
                             .setWindScale(now.getWindScale());
                    weather.setBasicInfo(basicInfo);
                    if(!isFromSearch)
                        SPUtils.saveBean(WeatherActivity.this,"BasicInfo",basicInfo);
                    basicInfo = null;

                    handler.sendEmptyMessage(BASIC_WEATHER);
                }else{
                    if(weatherNowBean!=null) {
                        Log.d(TAG, "update weather information callback error:" + weatherNowBean.getCode().getCode() + " " + weatherNowBean.getCode().getTxt());
                        Toast.makeText(WeatherActivity.this,weatherNowBean.getCode().getTxt(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //get 3-days forecast
        QWeather.getWeather3D(this, city.makeLocation(), new QWeather.OnResultWeatherDailyListener() {

            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "Weather3D: "+city.makeLocation());
                throwable.printStackTrace();
                Toast.makeText(WeatherActivity.this,getResources().getString(R.string.net_fail),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                if(weatherDailyBean!=null){
                    if(weatherDailyBean.getCode().getCode().equals("200")){
                        List<ForecastDay> list = new LinkedList<>();
                        for (WeatherDailyBean.DailyBean day: weatherDailyBean.getDaily()) {
                            ForecastDay forecastDay = new ForecastDay();
                            forecastDay.setDate(day.getFxDate()).setText(day.getTextDay()).setIcon(day.getIconDay());
                            forecastDay.setMinTemperature(day.getTempMin()).setMaxTemperature(day.getTempMax());
                            list.add(forecastDay);
                        }
                        weather.setForecastDayList(list);
                        if(!isFromSearch)
                            SPUtils.putListBean(WeatherActivity.this,"List<ForecastDay>",list);
                        list = null;

                        handler.sendEmptyMessage(FORECAST_DAY);
                    }else{
                        Log.d(TAG, "update weather information callback error:" + weatherDailyBean.getCode().getCode() + " " + weatherDailyBean.getCode().getTxt());
                        Toast.makeText(WeatherActivity.this,weatherDailyBean.getCode().getTxt(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // get 24-hours forecast
        /*QWeather.getWeather24Hourly(this, "101010100", new QWeather.OnResultWeatherHourlyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "24Hourly: "+city.makeLocation());
                throwable.printStackTrace();
                Toast.makeText(WeatherActivity.this,getResources().getString(R.string.net_fail),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                if(weatherHourlyBean!=null){
                    if(weatherHourlyBean.getCode().getCode().equals("200")){
                        List<ForecastHour> list = new LinkedList<>();
                        for (WeatherHourlyBean.HourlyBean hour: weatherHourlyBean.getHourly()) {
                            ForecastHour forecastHour = new ForecastHour();
                            forecastHour.setText(hour.getText()).setIcon(hour.getIcon());
                            forecastHour.setWindDir(hour.getWindDir()).setWindScale(hour.getWindScale());
                            forecastHour.setTemperature(hour.getTemp());
                            String time = hour.getFxTime();
                            if(DateUtils.is00(time)){
                                forecastHour.setTime(DateUtils.getTimePart(time,'M')+'月'+DateUtils.getTimePart(time,'D')+'日');
                            }else{
                                forecastHour.setTime(DateUtils.gethm(time));
                            }
                        }
                        weather.setForecastHourList(list);
                        list = null;

                        handler.sendEmptyMessage(FORECAST_HOUR);
                    }else{
                        Log.d(TAG, "update weather information callback error:" + weatherHourlyBean.getCode().getCode() + " " + weatherHourlyBean.getCode().getTxt());
                        Toast.makeText(WeatherActivity.this,weatherHourlyBean.getCode().getTxt(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
*/
        //get AQI
        QWeather.getAirNow(this, city.makeLocation(), null, new QWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(WeatherActivity.this,getResources().getString(R.string.net_fail),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                if(airNowBean!=null) {
                    if (airNowBean.getCode().getCode().equals("200")) {
                        Aqi aqi = new Aqi();
                        AirNowBean.NowBean now = airNowBean.getNow();
                        aqi.setApi(now.getAqi()).setCategory(now.getCategory()).setLevel(now.getLevel());
                        if("NA".equals(now.getPrimary())){
                            aqi.setPrimary("无");
                        }else{
                            aqi.setPrimary(now.getPrimary());
                        }
                        aqi.setPm25(now.getPm2p5()).setPm10(now.getPm10()).setSO2(now.getSo2());
                        weather.setAqi(aqi);
                        if(!isFromSearch)
                            SPUtils.saveBean(WeatherActivity.this,"Aqi",aqi);
                        aqi = null;

                        handler.sendEmptyMessage(AQI_INFO);
                    } else {
                        Log.d(TAG, "update weather information callback error:" + airNowBean.getCode().getCode() + " " + airNowBean.getCode().getTxt());
                        Toast.makeText(WeatherActivity.this, airNowBean.getCode().getTxt(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //get sun and moon
        QWeather.getSunMoon(this, city.makeLocation(), DateUtils.formatUTC(System.currentTimeMillis(), "yyyyMMdd"), new QWeather.OnResultSunMoonListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(WeatherActivity.this,getResources().getString(R.string.net_fail),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(SunMoonBean sunMoonBean) {
                if(sunMoonBean!=null){
                    if(sunMoonBean.getCode().getCode().equals("200")){
                        SunMoon sunAndMoon = new SunMoon();
                        SunMoonBean.SunMoonBeanBase obj1 = sunMoonBean.getSunMoonBean();
                        List<SunMoonBean.MoonPhaseBean> obj2 = sunMoonBean.getMoonPhaseBeanList();
                        sunAndMoon.setSunRaiseTime(obj1.getSunrise()).setSunSetTime(obj1.getSunset());
                        sunAndMoon.setMoonRaiseTime(obj1.getMoonRise()).setMoonSetTime(obj1.getMoonSet());
                        if(obj2.size()>0){
                            sunAndMoon.setMoonState(obj2.get(0).getName());
                        }
                        weather.setSunAndMoon(sunAndMoon);
                        if(!isFromSearch)
                            SPUtils.saveBean(WeatherActivity.this,"SunMoon",sunAndMoon);
                        sunAndMoon = null;

                        handler.sendEmptyMessage(SUN_MOON);
                    }else{
                        Log.d(TAG, "update weather information callback error:" + sunMoonBean.getCode().getCode() + " " + sunMoonBean.getCode().getTxt());
                        Toast.makeText(WeatherActivity.this, sunMoonBean.getCode().getTxt(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //
        QWeather.getWeather15D(this, city.makeLocation(), new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(WeatherActivity.this,getResources().getString(R.string.net_fail),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                if(weatherDailyBean!=null){
                    if(weatherDailyBean.getCode().getCode().equals("200")) {
                        List<ForecastDay> list = new LinkedList<>();
                        for (WeatherDailyBean.DailyBean day: weatherDailyBean.getDaily()) {
                            ForecastDay forecastDay = new ForecastDay();
                            forecastDay.setDate(day.getFxDate()).setText(day.getTextDay()).setIcon(day.getIconDay());
                            forecastDay.setMinTemperature(day.getTempMin()).setMaxTemperature(day.getTempMax());
                            list.add(forecastDay);
                        }
                        if(!isFromSearch)
                            SPUtils.putListBean(WeatherActivity.this,"15day",list);
                        list = null;
                    }else{
                        Log.d(TAG, "update weather information callback error:" + weatherDailyBean.getCode().getCode() + " " + weatherDailyBean.getCode().getTxt());
                        Toast.makeText(WeatherActivity.this, weatherDailyBean.getCode().getTxt(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(client!=null){
            client.onDestroy();
            listener = null;
            option = null;
        }
        if(progress!=null){
            progress.destroy();
        }
        if(sunTrack!=null){
            sunTrack.destroy();
        }
        super.onDestroy();
    }
}