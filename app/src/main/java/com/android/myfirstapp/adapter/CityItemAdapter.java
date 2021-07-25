package com.android.myfirstapp.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myfirstapp.R;
import com.android.myfirstapp.bean.City;
import com.android.myfirstapp.view.activity.WeatherActivity;
import com.android.myfirstapp.view.fragment.AreaChoose;

import java.util.List;

public class CityItemAdapter extends RecyclerView.Adapter<CityItemAdapter.ViewHolder> {
    private static final String TAG = "CityItemAdapter";
    private List<City> mList;
    private AreaChoose dealer;

    public void setDealer(AreaChoose dealer) {
        this.dealer = dealer;
    }

    public CityItemAdapter(List<City> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "event dispatch to view");
                int pos = viewHolder.getAdapterPosition();
                City city = mList.get(pos);
                Intent intent = new Intent(parent.getContext(), WeatherActivity.class);
                // todo WeatherActivity start model
                intent.putExtra("model","search");
                intent.putExtra("city",city);
                parent.getContext().startActivity(intent);
            }
        });
        viewHolder.oper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "event dispatch to ImageView");
                int pos = viewHolder.getAdapterPosition();
                City city = mList.get(pos);
                dealer.deleteCity(city);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = mList.get(position);
        holder.cityName.setText(city.toString());
        holder.oper.setImageResource(R.drawable.has_add);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View item;
        TextView cityName;
        ImageView oper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            cityName = itemView.findViewById(R.id.city_name_text);
            oper = itemView.findViewById(R.id.add2loacl_img);
        }
    }
}
