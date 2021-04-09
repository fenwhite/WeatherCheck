package com.android.myfirstapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.ForecastDay;

import java.util.List;

public class ForecastDayAdapter extends ArrayAdapter<ForecastDay> {
    private static final String TAG = "ForecastDayAdapter";
    private int resourceId;

    public ForecastDayAdapter(@NonNull Context context, int resource, @NonNull List<ForecastDay> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(this.getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.date = convertView.findViewById(R.id.date_text);
            viewHolder.temperature = convertView.findViewById(R.id.temperature_text);
            viewHolder.text = convertView.findViewById(R.id.info_text);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ForecastDay day = getItem(position);
        if(day!=null){
            viewHolder.date.setText(day.getDate());
            viewHolder.text.setText(day.getText());
            viewHolder.temperature.setText(day.getMinTemperature()+'/'+day.getMaxTemperature());
        }

        return convertView;
    }

    public static class ViewHolder{
        TextView date,text,temperature;
    }
}
