package com.android.myfirstapp.http;

import android.os.Handler;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.BaseHttpBean;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.bean.ForecastHour;
import com.android.myfirstapp.myApplication;
import com.android.myfirstapp.utils.ContentUtils;

import java.util.List;

public class HandlerUpdater implements ViewUpdate {
    Handler handler;

    public HandlerUpdater(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void update(int what, Object obj) {
        switch (what){
            case ContentUtils.AQI_INFO:
            case ContentUtils.SUN_MOON:
            case ContentUtils.BASIC_WEATHER:
                if(obj instanceof BaseHttpBean){
                    if(((BaseHttpBean) obj).getCode()==ContentUtils.HTTPOK) {
                        handler.sendMessage(handler.obtainMessage(what, obj));
                    }else
                        handler.sendMessage(handler.obtainMessage(ContentUtils.FAIL_GET,((BaseHttpBean) obj).getreText()));
                }
                break;
            case ContentUtils.FORECAST_DAY_3:
            case ContentUtils.FORECAST_DAY_15:
                if(obj instanceof List && ((List) obj).get(0) instanceof ForecastDay){
                    if(((List) obj).size()>0){
                        handler.sendMessage(handler.obtainMessage(what, obj));
                    }else{
                        handler.sendMessage(handler.obtainMessage(ContentUtils.FAIL_GET, myApplication.getContext().getResources().getString(R.string.bean_fail)));
                    }
                }
                break;
            case ContentUtils.FORECAST_HOUR:
                if(obj instanceof List && ((List) obj).get(0) instanceof ForecastHour){
                    if(((List) obj).size()==24){
                        handler.sendMessage(handler.obtainMessage(what, obj));
                    }else{
                        handler.sendMessage(handler.obtainMessage(ContentUtils.FAIL_GET,myApplication.getContext().getResources().getString(R.string.bean_fail)));
                    }
                }
                break;
            case ContentUtils.FAIL_GET:
            default:
                handler.sendMessage(handler.obtainMessage(what, obj));

        }
    }
}
