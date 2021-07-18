package com.android.myfirstapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.ForecastHour;

import java.util.List;

public class ForecastHourAdapter extends RecyclerView.Adapter<ForecastHourAdapter.ViewHolder> {
    int resourceId;
    List<ForecastHour> mList;

    public ForecastHourAdapter(int resourceId, List<ForecastHour> list) {
        this.resourceId = resourceId;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resourceId,parent,false);
        ForecastHourAdapter.ViewHolder viewHolder = new ForecastHourAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastHour hour = mList.get(position);
        holder.time.setText(hour.getTime());
        holder.temperature.setText(hour.getTemperature());
        holder.windScale.setText(hour.getWindScale());
        // todo icon
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView time,temperature,windScale;
        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.hour_time);
            temperature = itemView.findViewById(R.id.hour_temperature);
            windScale = itemView.findViewById(R.id.hour_wind);
            icon = itemView.findViewById(R.id.hour_icon);
        }
    }
}
