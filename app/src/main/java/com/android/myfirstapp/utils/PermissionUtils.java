package com.android.myfirstapp.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;

public class PermissionUtils {
    //TAG
    private static String TAG = "permission";

    private static final int REQUEST_CODE = 0;

    //when target sdk > 28
    private static String BACKGROUND_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    public static void checkPermission(final Activity activity,String[] permissions) {
        Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT + " and " + activity.getApplication().getApplicationInfo().targetSdkVersion);
        if (Build.VERSION.SDK_INT > 28 && activity.getApplication().getApplicationInfo().targetSdkVersion > 28) {
            int n = permissions.length;
            permissions = Arrays.copyOf(permissions, n + 1);
            permissions[n] = BACKGROUND_LOCATION_PERMISSION;
        }
        if (Build.VERSION.SDK_INT > 23 && activity.getApplication().getApplicationInfo().targetSdkVersion > 23) {

        }
    }

}
