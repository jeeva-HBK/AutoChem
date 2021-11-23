package com.ionexchange.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.R;

import java.util.List;

public class AlarmLogRvAdapter extends RecyclerView.Adapter<AlarmLogRvAdapter.itemHolder> {
    Context context;
    List<AlarmLogEntity> alarmLogEntityList;
    public AlarmLogRvAdapter(List<AlarmLogEntity> alarmLogEntityList) {
        this.alarmLogEntityList = alarmLogEntityList;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_log_item, parent, false);
        context = parent.getContext();
        return new itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        if (position % 2 == 0) {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.ash));

        }
        holder.sensorType.setText(alarmLogEntityList.get(position).sensorType);
        holder.AlertName.setText(alarmLogEntityList.get(position).alarmLog);
        holder.Date.setText(alarmLogEntityList.get(position).date+"|"+alarmLogEntityList.get(position).time);
    }

    @Override
    public int getItemCount() {
        return alarmLogEntityList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class itemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout root;
        TextView sensorType,AlertName,Date;
        public itemHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            sensorType = itemView.findViewById(R.id.sensor_type);
            AlertName = itemView.findViewById(R.id.alert_name);
            Date = itemView.findViewById(R.id.date);
        }
    }
}
