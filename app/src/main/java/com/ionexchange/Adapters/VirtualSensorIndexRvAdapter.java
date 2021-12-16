package com.ionexchange.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//created by Silambu
public class VirtualSensorIndexRvAdapter extends RecyclerView.Adapter<VirtualSensorIndexRvAdapter.ViewHolder> {
    RvOnClick rvOnClick;
    List<VirtualConfigurationEntity> virtualConfigurationEntityList;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;

    public VirtualSensorIndexRvAdapter(RvOnClick rvOnClick, List<VirtualConfigurationEntity> virtualConfigurationEntityList,
                                       KeepAliveCurrentValueDao keepAliveCurrentValueDao) {
        this.rvOnClick = rvOnClick;
        this.virtualConfigurationEntityList = virtualConfigurationEntityList;
        this.keepAliveCurrentValueDao = keepAliveCurrentValueDao;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vi_rv_item, parent, false);
        return new VirtualSensorIndexRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.hardwareNo.setText(keepAliveCurrentValueDao.getCurrentValue(virtualConfigurationEntityList.get(position).hardwareNo));
        holder.label.setText(virtualConfigurationEntityList.get(position).inputLabel);
        holder.low.setText(virtualConfigurationEntityList.get(position).subValueOne);
        holder.high.setText(virtualConfigurationEntityList.get(position).subValueTwo);
        holder.tv.setText("Virtual Sensor "+(position + 1));
        holder.currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(virtualConfigurationEntityList.get(position).hardwareNo));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.hardwareNo.setTooltipText(virtualConfigurationEntityList.get(position).hardwareNo+"");
            holder.currentValue.setTooltipText(keepAliveCurrentValueDao.getCurrentValue(virtualConfigurationEntityList.get(position).hardwareNo));
            holder.label.setTooltipText(virtualConfigurationEntityList.get(position).inputLabel);
            holder.low.setTooltipText(virtualConfigurationEntityList.get(position).subValueOne);
            holder.high.setTooltipText(virtualConfigurationEntityList.get(position).subValueTwo);
        }
    }

    @Override
    public int getItemCount() {
        return virtualConfigurationEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv, label, low, high, hardwareNo,currentValue;
        View view;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.vite_header_txt);
            view = itemView.findViewById(R.id.view_base);
            label = itemView.findViewById(R.id.input_label_value);
            low = itemView.findViewById(R.id.low_alarm_value);
            high = itemView.findViewById(R.id.high_alarm_value);
            hardwareNo = itemView.findViewById(R.id.input_hardware_no);
            currentValue = itemView.findViewById(R.id.current_value);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rvOnClick.onClick(getAdapterPosition() + 50);
                }
            });
        }
    }
}
