package com.android.myfirstapp.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtils {
    private volatile static HttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    private HttpUtils(OkHttpClient client){
        this.mOkHttpClient = client;
    }

    public static HttpUtils getInstance(){
        if(mInstance==null){
            synchronized (HttpUtils.class){
                mInstance = new HttpUtils(new OkHttpClient());
            }
        }
        return mInstance;
    }

    private OkHttpClient getmOkHttpClient(){
        return this.mOkHttpClient;
    }

    public static void sendHttp(String url,okhttp3.Callback callback){
        OkHttpClient client = getInstance().getmOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }


}