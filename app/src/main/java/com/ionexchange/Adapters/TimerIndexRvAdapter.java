package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Entity.TimerConfigurationEntity;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import java.util.List;

public class TimerIndexRvAdapter extends RecyclerView.Adapter<TimerIndexRvAdapter.itemHolder> {
    RvOnClick listener;
    List<TimerConfigurationEntity> timerConfigurationEntityList;

    public TimerIndexRvAdapter(RvOnClick listener, List<TimerConfigurationEntity> timerConfigurationEntityList) {
        this.listener = listener;
        this.timerConfigurationEntityList = timerConfigurationEntityList;
    }

    @NonNull
    @Override
    public TimerIndexRvAdapter.itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_index_item, parent, false);
        return new TimerIndexRvAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerIndexRvAdapter.itemHolder holder, int position) {
        holder.timeNo.setText(timerConfigurationEntityList.get(position).timerNo+"");
        holder.timerName.setText(timerConfigurationEntityList.get(position).timerName);
        holder.outputName.setText(timerConfigurationEntityList.get(position).outputLinked);
        holder.mode.setText(timerConfigurationEntityList.get(position).mode);
        holder.startTime.setText( timerConfigurationEntityList.get(position).startTime+"");
        holder.endTime.setText( timerConfigurationEntityList.get(position).duration+"");
        holder.status.setText(timerConfigurationEntityList.get(position).status);
    }

    @Override
    public int getItemCount() {
        return timerConfigurationEntityList.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        View view;
        TextView timeNo,timerName, outputName, mode, startTime, endTime, status;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view_1);
            timeNo = itemView.findViewById(R.id.timer_no);
            timerName = itemView.findViewById(R.id.txt_timer_header_1);
            outputName = itemView.findViewById(R.id.txt_output_value_1);
            mode = itemView.findViewById(R.id.txt_mode_value_1);
            startTime = itemView.findViewById(R.id.timer_start_value_1);
            endTime = itemView.findViewById(R.id.timer_duration_value_1);
            status = itemView.findViewById(R.id.timer_status_value_1);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(timeNo.getText().toString());
                }
            });

        }
    }
}
