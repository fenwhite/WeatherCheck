package com.android.myfirstapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import androidx.recyclerview.widget.RecyclerView;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.view.activity.WeatherActivity;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.LinkedList;
import java.util.List;

public class AreaChoose extends Fragment {
    private static final String TAG = "AreaChoose";
    public static final int CONTROL_PROGRESS = 1;
    public static final int UPDATE_LIST = 2;
    public static final int SHOW_SEARCH_LIST = 3;
    public static final int CLOSE_SEARCH_LIST = 4;

    private ImageView setting;
    private RecyclerView cityList;
    private ProgressBar progressBar;
    private SearchView citySearch;
    private ListView citySearchList;

    private boolean isUpdateList = false;   //whether update city list view now
    private ArrayAdapter<String> adapter;
    private List<String> cityNameData;
    private List<City> cityData;

    private boolean progressControl = false;    //if true invisible

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_choose, container, false);
        setting = view.findViewById(R.id.city_manage_setting);
        cityList = view.findViewById(R.id.city_list);
        citySearch = view.findViewById(R.id.city_search);
        progressBar = view.findViewById(R.id.search_progress_bar);
        citySearchList = view.findViewById(R.id.city_search_list);

        progressBar.setVisibility(View.INVISIBLE);
        citySearchList.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
//        ComponentName name = new ComponentName(getContext(), CitySearchActivity.class);
//        citySearch.setSearchableInfo(searchManager.getSearchableInfo(name));

        handler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case UPDATE_LIST:
                        adapter.clear();
                        for (City city: cityData) {
                            adapter.add(city.getCountry()+city.getProvince()+city.getName()+city.getDistrict());
                        }
                        adapter.notifyDataSetChanged();

                        isUpdateList = false;
                        break;
                    case CONTROL_PROGRESS:
                        if(!progressControl)
                            progressBar.setVisibility(View.INVISIBLE);
                        else{
                            progressBar.setVisibility(View.VISIBLE);
                        }
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

        cityNameData = new LinkedList<>();
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,
                cityNameData);
        citySearchList.setAdapter(adapter);
        citySearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                handler.sendEmptyMessage(CLOSE_SEARCH_LIST);
                City obj = cityData.get(position);
                Intent intent = new Intent(getContext(), WeatherActivity.class);
                intent.putExtra("city",obj);
                startActivity(intent);
            }
        });

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
                        handler.sendEmptyMessage(SHOW_SEARCH_LIST);
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
                                Toast.makeText(getContext(),geoBean.getCode().getTxt(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        citySearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                handler.sendEmptyMessage(CLOSE_SEARCH_LIST);
                adapter.clear();
                return false;
            }
        });
    }
}