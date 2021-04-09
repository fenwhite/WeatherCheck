package com.android.myfirstapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.City;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.LinkedList;
import java.util.List;

public class CitySearchActivity extends AppCompatActivity {
    private static final String TAG = "CitySearchActivity";

    public static final int UPDATE_LIST = 1;
    public static final int CLOSE_PROGRESS = 2;

    private ListView list;
    private ProgressBar progressBar;

    private boolean isUpdateList = false;   //whether update city list view now
    private List<String> cityNameData;
    private List<City> cityData;
    private ArrayAdapter<String> adapter;

    Handler handler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_LIST:
                    if(cityNameData!=null)
                        cityNameData.clear();
                    cityNameData = new LinkedList<>();
                    for (City city: cityData) {
                        cityNameData.add(city.getCountry()+city.getProvince()+city.getName()+city.getDistrict());
                    }
                    adapter.notifyDataSetChanged();

                    isUpdateList = false;
                    break;
                case CLOSE_PROGRESS:
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        list = findViewById(R.id.search_list);
        progressBar = findViewById(R.id.search_progress_bar);

        cityNameData = new LinkedList<>();
        adapter = new ArrayAdapter<>(CitySearchActivity.this,
                                             android.R.layout.simple_list_item_1,
                                             cityNameData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                City obj = cityData.get(position);
                Toast.makeText(CitySearchActivity.this,obj.getName(),Toast.LENGTH_LONG).show();
            }
        });

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        //at single-top model
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            progressBar.setVisibility(View.VISIBLE);
            searchCity(query);
        }
    }

    private void searchCity(String query){
        QWeather.getGeoCityLookup(CitySearchActivity.this, query, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                handler.sendEmptyMessage(CLOSE_PROGRESS);
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                handler.sendEmptyMessage(CLOSE_PROGRESS);
                if(geoBean!=null){
                    if("200".equals(geoBean.getCode().getCode())){
                        List<City> tmpData = new LinkedList<>();
                        List<GeoBean.LocationBean> locationList = geoBean.getLocationBean();
                        for (GeoBean.LocationBean city: locationList) {
                            City obj = new City();
                            obj.setDistrict(city.getName())
                               .setProvince(city.getAdm1())
                               .setCountry(city.getCountry())
                               .setName(city.getAdm2())
                               .setCityId(city.getId())
                               .setLongitude(Double.valueOf(city.getLon()))
                               .setLatitude(Double.valueOf(city.getLat()));

                            tmpData.add(obj);
                        }

                        // lock
                        while(isUpdateList) {
                            ;
                        }
                        isUpdateList = true;

                        if(cityData!=null)
                            cityData.clear();
                        cityData = new LinkedList<>(tmpData);
                        handler.sendEmptyMessage(UPDATE_LIST);
                    }else{
                        Log.d(TAG, "update weather information callback error:" + geoBean.getCode().getCode() + " " + geoBean.getCode().getTxt());
                        Toast.makeText(CitySearchActivity.this,geoBean.getCode().getTxt(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}