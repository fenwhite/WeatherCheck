package com.android.myfirstapp.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.ForecastDay;
import com.android.myfirstapp.http.ViewUpdate;
import com.android.myfirstapp.http.impl.HeHelper;
import com.android.myfirstapp.service.AutoUpdateService;
import com.android.myfirstapp.utils.ContentUtils;
import com.android.myfirstapp.utils.SPUtils;
import com.android.myfirstapp.utils.Utils;
import com.android.myfirstapp.utils.store.WeatherStore;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Forecast15Activity extends AppCompatActivity implements ViewUpdate {
    private static final String TAG = "Forecast15Activity";

    private LineChart chart;
    private ImageView back;

    private String store_pre;
    private List<ForecastDay> list;
    HeHelper heHelper;
    WeatherStore store;

    List<Entry> maxEntry,minEntry;
    LineDataSet maxSet,minSet;
    private List<ILineDataSet> lineDataSets = new ArrayList<>();
    private LineData mlineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast15);

        heHelper = new HeHelper(Forecast15Activity.this,this);

        chart = findViewById(R.id.forecast15_chart);
        back = findViewById(R.id.forecast15_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        store_pre = extras.getString("pre");
        String location = extras.getString("location");

        store = new WeatherStore(Forecast15Activity.this,false,"");
//        list = SPUtils.getListBean(this,
//                store_pre+ ContentUtils.FORECAST_DAY_15,
//                new TypeToken<List<ForecastDay>>() {
//                }.getType());
        list = store.getStoreDay(15);
        if(list==null || list.size()==0){
            heHelper.getWeather15D(location);
        }else{
            update(ContentUtils.FORECAST_DAY_15,list);
        }
    }



    private void iniChart(){
        chart.setVisibleXRangeMaximum(10);
        chart.setScaleYEnabled(false);
        chart.setDrawBorders(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        Description description = chart.getDescription();
        description.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(10,5,0);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return list.get((int)value).getDate().substring(5);
            }
        });

        YAxis rAxis = chart.getAxisRight();
        rAxis.setEnabled(false);

        YAxis lAxis = chart.getAxisLeft();
        lAxis.setDrawGridLines(false);
    }
    private void setChartData(){

        maxEntry = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            maxEntry.add(new Entry(i,Integer.valueOf(list.get(i).getMaxTemperature())));
        }
        maxSet = new LineDataSet(maxEntry,getResources().getString(R.string.max_chart_ling));
        maxSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        maxSet.setHighlightEnabled(true);
        maxSet.setLineWidth(2);
        maxSet.setColor(Color.parseColor("#FF3030"));
        maxSet.setCircleColor(Color.parseColor("#FF3030"));
        maxSet.setCircleRadius(6);
        maxSet.setCircleHoleRadius(3);
        maxSet.setDrawHighlightIndicators(true);
        maxSet.setHighLightColor(Color.RED);
        maxSet.setValueTextSize(12);
        maxSet.setValueTextColor(Color.BLACK);
        maxSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int)value);
            }
        });
        lineDataSets.add(maxSet);


        minEntry = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            minEntry.add(new Entry(i,Integer.valueOf(list.get(i).getMinTemperature())));
        }
        minSet = new LineDataSet(minEntry,getResources().getString(R.string.min_chart_ling));
        minSet.setHighlightEnabled(true);
        minSet.setLineWidth(2);
        minSet.setColor(Color.parseColor("#4876FF"));
        minSet.setCircleColor(Color.parseColor("#4876FF"));
        minSet.setCircleRadius(6);
        minSet.setCircleHoleRadius(3);
        minSet.setDrawHighlightIndicators(true);
        minSet.setHighLightColor(Color.RED);
        minSet.setValueTextSize(12);
        minSet.setValueTextColor(Color.BLACK);
        minSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int)value);
            }
        });
        lineDataSets.add(minSet);
        mlineData = new LineData(lineDataSets);
        chart.setData(mlineData);
    }

    @Override
    public void update(int what, Object obj) {
        if(what==ContentUtils.FORECAST_DAY_15){
            if(list==null || list.size()==0)
                SPUtils.saveBean(Forecast15Activity.this, store_pre+ ContentUtils.FORECAST_DAY_15, obj);
            list = (List<ForecastDay>) obj;

            iniChart();
            setChartData();
            myMarkerView view = new myMarkerView(this,R.layout.daily_marker_view);
            view.setChartView(chart);
            chart.setMarker(view);
            chart.invalidate();
        }
    }

    private class myMarkerView extends MarkerView {
        /**
         * Constructor. Sets up the MarkerView with a custom layout resource.
         *
         * @param context
         * @param layoutResource the layout resource to use for the MarkerView
         */
        private TextView day,temperature;
        private ImageView icon;
        public myMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            day = findViewById(R.id.daily_marker_day);
            temperature = findViewById(R.id.daily_marker_temperature);
            icon = findViewById(R.id.daily_marker_icon);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            int pos = (int) e.getX();
            day.setText(list.get(pos).getDate());
            StringBuffer tmp = new StringBuffer(list.get(pos).getMaxTemperature());
            tmp.append("°~").append(list.get(pos).getMinTemperature()).append('°');
            temperature.setText(tmp.toString());
            //todo: show icon
            super.refreshContent(e,highlight);
        }

    }
}