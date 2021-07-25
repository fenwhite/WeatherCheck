package com.android.myfirstapp.utils.store;

import android.content.Context;

import com.android.myfirstapp.bean.City;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CityStore extends BaseStore{
    public static final String NAME = "city_list";
    public static final String LOCATION = "position";

    public CityStore(Context context) {
        super(context);
    }
    public CityStore(Context context, boolean isValid) {
        super(context, isValid);
    }

    public List<City> getCityList(){
        List<City> list = getListBean(NAME,new TypeToken<List<City>>() {}.getType());
        return list;
    }

    public void saveCityList(List<City> list){
        putListBean(NAME,list);
    }

    public void reWriteCityList(List<City> list){
        remove(NAME);
        putListBean(NAME,list);
    }

    public void saveLocation(City city){
        saveBean(LOCATION,city);
    }
    public City getLocation(){
        City location = getBean(LOCATION,City.class);
        return location;
    }
}
