package com.android.myfirstapp.bean;

public class SunMoon {
    private String sunRaiseTime;
    private String sunSetTime;
    private String moonRaiseTime;
    private String moonSetTime;
    private String moonState;
    private String moonIllumination;

    public SunMoon() {
    }

    public SunMoon(SunMoon obj) {
        this.sunRaiseTime = obj.sunRaiseTime;
        this.sunSetTime = obj.sunSetTime;
        this.moonRaiseTime = obj.moonRaiseTime;
        this.moonSetTime = obj.moonSetTime;
        this.moonState = obj.moonState;
        this.moonIllumination = obj.moonIllumination;
    }

    //format 2021-12-30T15:44+08:00
    public String getSunRaiseTime() {
        return sunRaiseTime;
    }

    public SunMoon setSunRaiseTime(String sunRaiseTime) {
        this.sunRaiseTime = sunRaiseTime;
        return this;
    }

    public String getSunSetTime() {
        return sunSetTime;
    }

    public SunMoon setSunSetTime(String sunSetTime) {
        this.sunSetTime = sunSetTime;
        return this;
    }

    public String getMoonRaiseTime() {
        return moonRaiseTime;
    }

    public SunMoon setMoonRaiseTime(String moonRaiseTime) {
        this.moonRaiseTime = moonRaiseTime;
        return this;
    }

    public String getMoonSetTime() {
        return moonSetTime;
    }

    public SunMoon setMoonSetTime(String moonSetTime) {
        this.moonSetTime = moonSetTime;
        return this;
    }

    public String getMoonState() {
        return moonState;
    }

    public SunMoon setMoonState(String moonState) {
        this.moonState = moonState;
        return this;
    }

    public String getMoonIllumination() {
        return moonIllumination;
    }

    public SunMoon setMoonIllumination(String moonIllumination) {
        this.moonIllumination = moonIllumination;
        return this;
    }
}
