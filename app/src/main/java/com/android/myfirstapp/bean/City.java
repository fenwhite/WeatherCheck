package com.android.myfirstapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    private String district;
    private String name;
    private String cityId;
    private String province;
    private String country;
    private double latitude;    //纬度
    private double longitude;   //经度

    public City() {
    }

    public City(City obj) {
        this.district = obj.district;
        this.name = obj.name;
        this.cityId = obj.cityId;
        this.province = obj.province;
        this.country = obj.country;
        this.latitude = obj.latitude;
        this.longitude = obj.longitude;
    }

    protected City(Parcel in) {
        district = in.readString();
        name = in.readString();
        cityId = in.readString();
        province = in.readString();
        country = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(district);
        dest.writeString(name);
        dest.writeString(cityId);
        dest.writeString(province);
        dest.writeString(country);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public String getDistrict() {
        return district;
    }

    public City setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getName() {
        return name;
    }

    public City setName(String name) {
        this.name = name;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public City setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCityId() {
        return cityId;
    }

    public City setCityId(String cityId) {
        this.cityId = cityId;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public City setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public City setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public City setCountry(String country) {
        this.country = country;
        return this;
    }

    public String makeLocation(){
        StringBuffer sb = new StringBuffer();
        sb.append(longitude).append(',').append(latitude);
        return sb.toString();
    }

}
