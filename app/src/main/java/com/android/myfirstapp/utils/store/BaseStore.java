package com.android.myfirstapp.utils.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.myfirstapp.R;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseStore {
    public final static String PREFERENCE_NAME = "weather";

    private Context context;
    private boolean isValid = true;

    private SharedPreferences.Editor editor;
    private SharedPreferences sp;

    public BaseStore() {
    }
    public BaseStore(Context context) {
        this.context = context;

        sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public BaseStore(Context context, boolean isValid) {
        this.context = context;
        this.isValid = isValid;

        sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public Context getContext() {
        return context;
    }
    public boolean isValid() {
        return isValid;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public void setValid(boolean valid) {
        isValid = valid;
    }

    /**
     * put string preferences
     *
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    protected boolean putString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }
    /**
     * get string preferences
     *
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     * name that is not a string
     * @see #getString(String, String)
     */
    protected String getString(String key) {
        return getString( key, "");
    }
    /**
     * get string preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a string
     */
    protected String getString( String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    /**
     * 保存List<Object>
     *
     * @param key
     * @param datalist
     */
    protected <T> void putListBean(String key, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0) {
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(key, strJson);
        editor.commit();
    }
    /**
     * 获取List<Object>
     *
     * @param key
     * @return listBean
     */
    protected <T> List<T> getListBean( String key, Type type) {
        List<T> dataList = new ArrayList<T>();
        String strJson = sp.getString(key, null);
        if (null == strJson) {
            return dataList;
        }
        Gson gson = new Gson();
        dataList = gson.fromJson(strJson, type);
        return dataList;
    }

    /**
     * 存放实体类以及任意类型
     *
     * @param key
     * @param obj
     */
    protected void saveBean( String key, Object obj) {
        Gson gson = new Gson();
        String objString = gson.toJson(obj);
        editor.putString(key, objString).commit();
    }
    /**
     * @param key
     * @param clazz   这里传入一个类就是我们所需要的实体类(obj)
     * @return 返回我们封装好的该实体类(obj)
     */
    protected <T> T getBean( String key, Class<T> clazz) {
        String objString = sp.getString(key, "");
        Gson gson = new Gson();
        return gson.fromJson(objString, clazz);
    }

    protected void remove(String key){
        editor.remove(key).commit();
    }


    protected class nonValidException extends Exception{
        public nonValidException() {
        }

        public nonValidException(String message) {
            super(message);
        }

        public nonValidException(String message, Throwable cause) {
            super(message, cause);
        }

        public nonValidException(Throwable cause) {
            super(cause);
        }
    }
}
