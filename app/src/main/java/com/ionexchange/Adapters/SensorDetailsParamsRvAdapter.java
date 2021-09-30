package com.ionexchange.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.R;

import java.util.List;

public class SensorDetailsParamsRvAdapter extends RecyclerView.Adapter<SensorDetailsParamsRvAdapter.itemHolder> {
    List<String[]> mData;

    public SensorDetailsParamsRvAdapter(List<String[]> dataMap) {
        this.mData = dataMap;
    }

    @NonNull
    @Override
    public SensorDetailsParamsRvAdapter.itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor_details, parent, false);
        return new SensorDetailsParamsRvAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        String[] stArr = mData.get(position);
        holder.header.setText(stArr[0]);
        holder.value.setText(stArr[1]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.header.setTooltipText(stArr[0]);
            holder.value.setTooltipText(stArr[1]);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        TextView header, value;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header_txt);
            value = itemView.findViewById(R.id.rValue);
        }
    }
}
