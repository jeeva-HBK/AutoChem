package com.ionexchange.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Entity.EventLogEntity;
import com.ionexchange.R;

import java.util.List;

public class EventLogRvAdapter extends RecyclerView.Adapter<EventLogRvAdapter.itemHolder> {
    Context context;
    List<EventLogEntity> eventLogEntityList;


    public EventLogRvAdapter(List<EventLogEntity> eventLogEntityList) {
        this.eventLogEntityList = eventLogEntityList;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_log_item, parent, false);
        context = parent.getContext();
        return new EventLogRvAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        if (position % 2 == 0) {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.ash));

        }

        holder.sensorType.setText(eventLogEntityList.get(position).sensorType);
        holder.AlertName.setText(eventLogEntityList.get(position).eventLog);
        holder.Date.setText(eventLogEntityList.get(position).date+" | "+eventLogEntityList.get(position).time);
    }

    @Override
    public int getItemCount() {
        return eventLogEntityList.size();
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
