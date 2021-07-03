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

        HeConfig.init("HE2103221835221643", "acb753ce4e48440d9949cb6bd885d3a3");
        HeConfig.switchToDevService();
    }

    public static Context getContext() {
        return context;
    }
}
