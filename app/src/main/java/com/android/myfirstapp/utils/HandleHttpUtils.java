package com.android.myfirstapp.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class HandleHttpUtils {
    public static String handlePicUrlResponse(String response){
        try {
            JSONObject obj = new JSONObject(response);
            if(obj.getInt("code")==200){
                return obj.getString("imgurl");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "https://mfiles.alphacoders.com/894/thumb-894710.png";
    }
}
