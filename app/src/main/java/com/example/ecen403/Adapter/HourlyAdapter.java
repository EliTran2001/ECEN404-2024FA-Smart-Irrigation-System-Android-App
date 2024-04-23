package com.example.ecen403.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecen403.Domains.Hourly;
import com.example.ecen403.R;

import java.util.ArrayList;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {
    ArrayList<Hourly> items;
    Context context;

    public HourlyAdapter(ArrayList<Hourly> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_hourly,parent,false);
        context= parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyAdapter.ViewHolder holder, int position) {
    holder.hourTxt.setText(items.get(position).getHour());
    holder.tempTxt.setText(items.get(position).getTemp()+"°C");

    int drawableResourceId=holder.itemView.getResources()
            .getIdentifier(items.get(position).getPicPath(),"drawable",holder.itemView.getContext().getPackageName());

    Glide.with(context)
            .load(drawableResourceId)
            .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView hourTxt, tempTxt;
        ImageView pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hourTxt =itemView.findViewById(R.id.hourTxt);
            tempTxt=itemView.findViewById(R.id.tempTxt);
            pic=itemView.findViewById(R.id.pic);
        }
    }
}
