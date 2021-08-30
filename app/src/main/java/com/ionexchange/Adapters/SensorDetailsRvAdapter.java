package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.R;

public class SensorDetailsRvAdapter extends RecyclerView.Adapter<SensorDetailsRvAdapter.itemHolder> {

    @NonNull
    @Override
    public SensorDetailsRvAdapter.itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor_details,parent,false);
        return new SensorDetailsRvAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorDetailsRvAdapter.itemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        public itemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
