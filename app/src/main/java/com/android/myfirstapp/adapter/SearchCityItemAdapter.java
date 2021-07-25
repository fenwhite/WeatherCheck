package com.android.myfirstapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.myfirstapp.bean.City;

import java.util.List;

import com.android.myfirstapp.R;
import com.android.myfirstapp.view.fragment.AreaChoose;

public class SearchCityItemAdapter extends ArrayAdapter<City> {
    private AreaChoose dealer;

    public SearchCityItemAdapter(@NonNull Context context, int resource, @NonNull List<City> objects, AreaChoose dealer) {
        super(context, resource, objects);
        this.dealer = dealer;
    }

    public SearchCityItemAdapter(@NonNull Context context, int resource, @NonNull List<City> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.city_item,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final City city = getItem(position);
        holder.cityName.setText(city.toString());
        if(dealer.isCityStored(city)){
            holder.oper.setImageResource(R.drawable.has_add);
        }else{
            holder.oper.setImageResource(R.drawable.to_add);
        }
        holder.oper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dealer.isCityStored(city)){
                    Toast.makeText(getContext(),"已添加",Toast.LENGTH_SHORT).show();
                }else{
                    holder.oper.setImageResource(R.drawable.has_add);
                    dealer.addCity(getItem(position));
                }
            }
        });

        return convertView;
    }

    static class ViewHolder{
        View item;
        TextView cityName;
        ImageView oper;

        public ViewHolder(@NonNull View item) {
            this.item = item;
            cityName = item.findViewById(R.id.city_name_text);
            oper = item.findViewById(R.id.add2loacl_img);
        }
    }
}
