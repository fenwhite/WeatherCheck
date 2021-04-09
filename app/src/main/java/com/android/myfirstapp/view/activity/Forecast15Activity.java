package com.android.myfirstapp.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.utils.SPUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Forecast15Activity extends AppCompatActivity {
    private static final String TAG = "Forecast15Activity";

    private LineChart chart;
    private ImageView back;

    List<Entry> maxEntry,minEntry;
    LineDataSet maxSet,minSet;
    LineData maxLine,minLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast15);

        chart = findViewById(R.id.forecast15_chart);
        back = findViewById(R.id.forecast15_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<ForecastDay> list = SPUtils.getListBean(this,
                "15day",
                new TypeToken<List<ForecastDay>>() {
                }.getType());
        if(list.size()==0){
            Toast.makeText(Forecast15Activity.this,getResources().getString(R.string.function_not_accomplish),Toast.LENGTH_LONG).show();
            return;
        }
        maxEntry = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            maxEntry.add(new Entry(i,Integer.valueOf(list.get(i).getMaxTemperature())));
        }
        maxSet = new LineDataSet(maxEntry,getResources().getString(R.string.max_chart_ling));
        maxSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        maxSet.setHighlightEnabled(true);
        maxSet.setLineWidth(2);
        maxSet.setColor(Color.BLUE);
        maxSet.setCircleColor(Color.CYAN);
        maxSet.setCircleRadius(6);
        maxSet.setCircleHoleRadius(3);
        maxSet.setDrawHighlightIndicators(true);
        maxSet.setHighLightColor(Color.RED);
        maxSet.setValueTextSize(12);
        maxSet.setValueTextColor(Color.BLACK);

        maxLine =  new LineData(maxSet);

        chart.setData(maxLine);

//        minEntry = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            minEntry.add(new Entry(i,Integer.valueOf(list.get(i).getMinTemperature())));
//        }
//        minSet = new LineDataSet(minEntry,getResources().getString(R.string.min_chart_ling));
//        minLine = new LineData(minSet);
//        chart.setData(minLine);

        chart.invalidate();
    }
}