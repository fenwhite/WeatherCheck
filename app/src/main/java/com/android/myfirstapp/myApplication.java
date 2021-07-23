package com.android.myfirstapp;

import android.app.Application;
import android.content.Context;

import com.qweather.sdk.view.HeConfig;

public class myApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        HeConfig.init(public, key);
        HeConfig.switchToDevService();
    }

    public static Context getContext() {
        return context;
    }
}
