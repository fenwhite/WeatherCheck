package com.android.myfirstapp.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myfirstapp.R;
import com.android.myfirstapp.adapter.CityItemAdapter;
import com.android.myfirstapp.adapter.SearchCityItemAdapter;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.utils.Utils;
import com.android.myfirstapp.utils.store.CityStore;
import com.android.myfirstapp.view.activity.WeatherActivity;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AreaChoose extends Fragment {
    private static final String TAG = "AreaChoose";
    public static final int CONTROL_PROGRESS = 1;
    public static final int SHOW_LIST = 2;
    public static final int SHOW_SEARCH_LIST = 3;
    public static final int CLOSE_SEARCH_LIST = 4;

    private RecyclerView cityList;
    private ProgressBar progressBar;
    private SearchView citySearch;
    private ListView citySearchList;

    private Activity context;
    private SearchCityItemAdapter searchAdapter;
    CityItemAdapter cityItemAdapter;

    
    private CityStore cityManager;
    private List<City> searchData,cityData;

    private boolean progressControl = false;    //if true invisible

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_choose, container, false);
        cityList = view.findViewById(R.id.city_list);
        citySearch = view.findViewById(R.id.city_search);
        progressBar = view.findViewById(R.id.search_progress_bar);
        citySearchList = view.findViewById(R.id.city_search_list);

        progressBar.setVisibility(View.INVISIBLE);
//        citySearchList.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        handler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case SHOW_LIST:
                        //todo why?
//                        synchronized (this) {
//                            for (City city : searchData) {
//                                searchAdapter.add(city);
//                            }
//                            searchAdapter.notifyDataSetChanged();
//                        }

                        searchAdapter.clear();
                        searchAdapter.addAll(searchData);
                        searchAdapter.notifyDataSetChanged();
                        break;
                    case CONTROL_PROGRESS:
                        progressBar.setVisibility(progressControl? View.VISIBLE:View.INVISIBLE);
                        break;
                    case SHOW_SEARCH_LIST:
                        citySearchList.setVisibility(View.VISIBLE);
                        break;
                    case CLOSE_SEARCH_LIST:
                        citySearchList.setVisibility(View.INVISIBLE);
                        break;
                }
                return false;
            }
        });

        searchData = new LinkedList<>();
        searchAdapter = new SearchCityItemAdapter(context,R.layout.city_item,searchData,this);
        citySearchList.setAdapter(searchAdapter);
        citySearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                City obj = searchData.get(position);
                Intent intent = new Intent(getContext(), WeatherActivity.class);
                Utils.makePre(intent,getResources().getString(R.string.model_search),obj);
                startActivity(intent);
            }
        });

        cityManager = new CityStore(context);
        cityData = new ArrayList<>();
        List storeCity = cityManager.getCityList();
        if(storeCity!=null && storeCity.size()>0)
            cityData.addAll(storeCity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        cityList.setLayoutManager(linearLayoutManager);
        cityItemAdapter = new CityItemAdapter(cityData);
        cityItemAdapter.setDealer(this);
        cityList.setAdapter(cityItemAdapter);

        citySearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressControl = true;
                handler.sendEmptyMessage(CONTROL_PROGRESS);

                QWeather.getGeoCityLookup(getContext(), query, new QWeather.OnResultGeoListener() {
                    @Override
                    public void onError(Throwable throwable) {
                        progressControl = false;
                        handler.sendEmptyMessage(CONTROL_PROGRESS);
                    }

                    @Override
                    public void onSuccess(GeoBean geoBean) {
                        progressControl = false;
                        handler.sendEmptyMessage(CONTROL_PROGRESS);
                        if(geoBean!=null){
                            if("200".equals(geoBean.getCode().getCode())){
                                List<City> tmpData = new LinkedList<>();
                                List<GeoBean.LocationBean> locationList = geoBean.getLocationBean();
                                for (GeoBean.LocationBean city: locationList) {
                                    City obj = new City();
                                    obj.setDistrict(city.getName()+"区")
                                            .setProvince(city.getAdm1())
                                            .setCountry(city.getCountry())
                                            .setName(city.getAdm2()+"市")
                                            .setCityId(city.getId())
                                            .setLongitude(Double.parseDouble(city.getLon()))
                                            .setLatitude(Double.parseDouble(city.getLat()));

                                    tmpData.add(obj);
                                }

                                //todo why get ConcurrentModificationException???
//                                synchronized (this) {
//                                    searchData.clear();
//                                    searchData.addAll(tmpData);
//                                }
                                searchData = tmpData;

                                handler.sendEmptyMessage(SHOW_LIST);
                            }else{
                                Log.d(TAG, "update weather information callback error:" + geoBean.getCode().getCode() + " " + geoBean.getCode().getTxt());
                                Toast.makeText(getContext(),geoBean.getCode().getTxt(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if(TextUtils.isEmpty(newText)){
//                    searchAdapter.clear();
//                }
                return false;
            }
        });
        citySearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchAdapter.clear();
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public boolean isCityStored(City city){
        boolean in = false;
        for(City obj : cityData)
            if(obj.equals(city)){
                in = true;
                break;
            }
        return in;
    }
    public void addCity(City city){
        cityData.add(city);
        cityItemAdapter.notifyDataSetChanged();
        cityManager.saveCityList(cityData);
    }
    public void deleteCity(City city){
        cityData.remove(city);
        cityItemAdapter.notifyDataSetChanged();
        cityManager.saveCityList(cityData);
    }
}