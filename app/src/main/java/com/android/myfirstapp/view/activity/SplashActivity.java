package com.android.myfirstapp.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.myfirstapp.R;
import com.android.myfirstapp.utils.HandleHttpUtils;
import com.android.myfirstapp.utils.HttpUtils;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    public static final  int REQUEST_CODE = 0;
    //when target sdk > 28
    private static String BACKGROUND_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    private static String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,

    };

    private ImageView bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        initPermission();
    }

    private void initPermission(){
//        if(Build.VERSION.SDK_INT > 28 && getApplicationContext().getApplicationInfo().targetSdkVersion > 28){
//            int n = needPermissions.length;
//            needPermissions = Arrays.copyOf(needPermissions, n + 1);
//            needPermissions[n] = BACKGROUND_LOCATION_PERMISSION;
//        }
        for (String permission : needPermissions) {
            Log.d(TAG, "need permission: "+permission);
        }

        if(Build.VERSION.SDK_INT > 23 && getApplicationContext().getApplicationInfo().targetSdkVersion > 23){
            List<String> noGrantedPermissions = findDeniedPermission();
            if(noGrantedPermissions.size()!=0){
                //some permissions no granted
                requestPermissions((String[]) noGrantedPermissions.toArray(new String[noGrantedPermissions.size()]),REQUEST_CODE);
            }else{
                //all permissions are granted
                startAnmi();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermission(){
        for (String permission: needPermissions) {
            if(checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> findDeniedPermission(){
        LinkedList<String> deniedPermissions = new LinkedList<>();
        for (String permission: needPermissions) {
            if(checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE){
            boolean allGranted = true;
            for (int grantRes: grantResults) {
                allGranted = allGranted && (grantRes == PackageManager.PERMISSION_GRANTED);
            }
            for (int i = 0; i < permissions.length; i++) {
                Log.d(TAG, "permission: "+permissions[i]+" and result: "+grantResults[i]);
            }
            if(!allGranted){
                // show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.notifyTitle);
                builder.setMessage(R.string.notifyMsg);

                // 拒绝, 退出应用
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                builder.setPositiveButton(R.string.setting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goAppSetting();
                            }
                        });

                builder.setCancelable(false);

                builder.show();
            }else{
                startAnmi();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startIntent(){
        Intent intent = new Intent(SplashActivity.this, WeatherActivity.class);
        intent.putExtra("model",getResources().getString(R.string.model_location));
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    private void goAppSetting(){
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startAnmi(){
        bg = findViewById(R.id.splash_background);
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.splash_alpha);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startIntent();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bg.startAnimation(animation);
    }

    private void requestBackgroundPic(){
        String url = "https://www.dmoe.cc/random.php?return=json";
        HttpUtils.sendHttp(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                loadBackgroundPic(getResources().getString(R.string.defaultPicUrl));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = response.body().string();
                String url = HandleHttpUtils.handlePicUrlResponse(responseText);
                Log.d(TAG, "picture url:"+url);
                loadBackgroundPic(url);
            }
        });
    }
    private void loadBackgroundPic(final String picUrl){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(SplashActivity.this).load(picUrl).into(bg);
                Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.splash_alpha);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startIntent();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                bg.startAnimation(animation);
            }
        });
    }
}