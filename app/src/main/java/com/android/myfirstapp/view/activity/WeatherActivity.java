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
import com.android.myfirstapp.http.impl.HeHelper;
import com.android.myfirstapp.service.AutoUpdateService;
import com.android.myfirstapp.utils.ContentUtils;
import com.android.myfirstapp.utils.DateUtils;
import com.android.myfirstapp.utils.SPUtils;
import com.android.myfirstapp.utils.WeatherHandlerUtils;
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

    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawer;
    public NestedScrollView weatherLayout;
    private TextView degree, elseInfo,regionName,updateTime;
    private TextView lastUpdateTime;
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
    HeHelper heHelper;

    private boolean needSP = false;
    private String store_pre = "";

    private Handler handler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case ContentUtils.REFRESH_FINISH:
                    swipeRefresh.setRefreshing(false);
                    break;
                case ContentUtils.BASIC_WEATHER:
                    BasicInfo basicInfo = (BasicInfo) msg.obj;
                    if(needSP)
                        SPUtils.saveBean(WeatherActivity.this,store_pre+msg.what,msg.obj);
                    showBasic(basicInfo);
                    break;
                case ContentUtils.FORECAST_DAY:
                    List<ForecastDay> forecastDays = (List<ForecastDay>) msg.obj;
                    if(needSP)
                        SPUtils.saveBean(WeatherActivity.this,store_pre+msg.what,msg.obj);
                    showForecastInDay(forecastDays);
                    break;
                case ContentUtils.FORECAST_HOUR:
                    List<ForecastHour> forecastHours = (List<ForecastHour>) msg.obj;
                    if(needSP)
                        SPUtils.saveBean(WeatherActivity.this,store_pre+msg.what,msg.obj);
                    showForecastInHour(forecastHours);
                    break;
                case ContentUtils.AQI_INFO:
                    if(needSP)
                        SPUtils.saveBean(WeatherActivity.this,store_pre+msg.what,msg.obj);
                    showAQI((Aqi) msg.obj);
                    break;
                case ContentUtils.SUN_MOON:
                    if(needSP)
                        SPUtils.saveBean(WeatherActivity.this,store_pre+msg.what,msg.obj);
                    showSunMoon((SunMoon) msg.obj);
                    break;
                case ContentUtils.FAIL_GET:
                    showToastLong((String)msg.obj);
                    break;
            }
            return false;
        }
    });

    private void showBasic(BasicInfo basic){
        degree.setText(basic.getNowTemperature());
        elseInfo.setText(basic.getText()+" "+basic.getWindDir());
        regionName.setText(basic.getCity()+basic.getDistrict());
        updateTime.setText(DateUtils.formatChinese(basic.getUpdateTime()));
    }

    private void showForecastInDay(List<ForecastDay> list){
        ForecastDayAdapter adapter = new ForecastDayAdapter(WeatherActivity.this,R.layout.forecast_day_item,list);
        forecastDay.setAdapter(adapter);
    }
    private void showForecastInHour(List<ForecastHour> list){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        forecastHour.setLayoutManager(linearLayoutManager);

        ForecastHourAdapter adapter = new ForecastHourAdapter(R.layout.forecast_hour_item,list);
        forecastHour.setAdapter(adapter);
    }

    private void showAQI(Aqi aqi){
        progress.setValue(aqi.getApi());
        progress.setText(aqi.getCategory());
        progress.startAnimProgress(4000);

        primary.setText(aqi.getPrimary());
        pm25.setText(aqi.getPm25());
        pm10.setText(aqi.getPm10());
        SO2.setText(aqi.getSO2());
    }

    private void showSunMoon(SunMoon obj){
        sunRaise.setText(obj.getSunRaiseTime().substring(11,16));
        sunset.setText(obj.getSunSetTime().substring(11,16));
        moonState.setText(obj.getMoonState());
        sunTrack.setStartTime(obj.getSunRaiseTime());
        sunTrack.setFinishTime(obj.getSunSetTime());
        sunTrack.startAnimation(8000);
    }

    private void showToastLong(String str){
        Toast.makeText(WeatherActivity.this,str,Toast.LENGTH_SHORT).show();
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
        degree = findViewById(R.id.now_degree);
        elseInfo = findViewById(R.id.else_info);
        regionName = findViewById(R.id.region_name);
        updateTime = findViewById(R.id.update_time);
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

        lastUpdateTime = findViewById(R.id.last_update_time);
        String tmp = SPUtils.getString(this,"updateTime","");
        if("".equals(tmp)){
            tmp = "别瞅了，没更新过";
        }
        lastUpdateTime.setText(tmp);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (client == null)
                    initClient();
                client.startLocation();
                handler.sendEmptyMessage(ContentUtils.REFRESH_FINISH);
            }
        });
        to15D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,Forecast15Activity.class);
                intent.putExtra("pre",store_pre);
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
        String model = extras.getString("model");
        heHelper = new HeHelper(WeatherActivity.this,handler);

        if(getResources().getString(R.string.model_location).equals(model)){
            store_pre = "";
            this.city = SPUtils.getBean(this, store_pre+"City", City.class);
            needSP = true;

        }else{
        if(getResources().getString(R.string.model_search).equals(model)){
            this.city = (City) extras.getParcelable("city");
            toAreaChoose.setVisibility(View.INVISIBLE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            needSP = false;

        }else{
            ;
        }}

        if(this.city ==null) {
            initClient();
            client.startLocation();
        }else{
            boolean isSendMsg = true;
            BasicInfo basicInfo = SPUtils.getBean(this,store_pre+ ContentUtils.BASIC_WEATHER,BasicInfo.class);
            isSendMsg = isSendMsg && (basicInfo!=null);

            List<ForecastDay> forecastDays = SPUtils.getListBean(this,
                    store_pre+ContentUtils.FORECAST_DAY,
                    new TypeToken<List<ForecastDay>>() {}.getType());
            isSendMsg = isSendMsg && (forecastDays!=null);

            Aqi aqi = SPUtils.getBean(this,store_pre+ContentUtils.AQI_INFO,Aqi.class);
            isSendMsg = isSendMsg && (aqi!=null);

            SunMoon sunMoon = SPUtils.getBean(this,store_pre+ContentUtils.SUN_MOON,SunMoon.class);
            isSendMsg = isSendMsg && (sunMoon!=null);

            List<ForecastHour> forecastHours = SPUtils.getListBean(this,
                    store_pre+ContentUtils.FORECAST_HOUR,
                    new TypeToken<List<ForecastHour>>() {}.getType());
            isSendMsg = isSendMsg && (forecastHours!=null);

            if(needSP && isSendMsg){

                showBasic(basicInfo);
                showForecastInDay(forecastDays);
                showAQI(aqi);
                showSunMoon(sunMoon);
                showForecastInHour(forecastHours);
            }else{
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
            handler.sendEmptyMessage(ContentUtils.REFRESH_FINISH);
            if(location!=null){
                if(location.getErrorCode()==0){
                    //success
                    client.stopLocation();

                    //todo:city factory
                    city = new City();
                    city.setLatitude(location.getLatitude()).setLongitude(location.getLongitude());
                    city.setDistrict(location.getDistrict()).setCityId(location.getCityCode()).setName(location.getCity()).setProvince(location.getProvince());
                    if(needSP)
                        SPUtils.saveBean(getApplicationContext(),store_pre+"City",city);

                    updateWeatherInfo();
                }else{
                    //fail
                    Toast.makeText(WeatherActivity.this,"错误描述:" + location.getLocationDetail(),Toast.LENGTH_LONG).show();
                    city = SPUtils.getBean(WeatherActivity.this, store_pre+"City", City.class);
                    if(city!=null){
                        updateWeatherInfo();
                    }
                }
            }
        }
    };

    private void updateWeatherInfo(){
        heHelper.getWeatherNow(city);
        heHelper.getWeather3D(city.makeLocation());
        heHelper.getAirNow(city.makeLocation());
        heHelper.getWeather24Hourly(city.makeLocation());
        heHelper.getSunMoon(city.makeLocation());
    }

    private void startUpdateService(){
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
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