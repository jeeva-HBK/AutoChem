package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

public class SelectSensorListAdapter extends RecyclerView.Adapter<SelectSensorListAdapter.ViewHolder> {
    String[] list;
    RvOnClick rvOnClick;

    public SelectSensorListAdapter(String[] mList, RvOnClick rvOnClick) {
        this.list = mList;
        this.rvOnClick = rvOnClick;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selecsensor_rv_item, parent, false);
        return new SelectSensorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectSensorListAdapter.ViewHolder holder, int position) {
        holder.sensorItem.setText(list[position]);
        holder.sensorItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rvOnClick.onClick(compoundButton.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox sensorItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            sensorItem = itemView.findViewById(R.id.sensorItemRv);
        }
    }
}
