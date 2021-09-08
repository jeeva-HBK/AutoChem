package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectSensorListAdapter extends RecyclerView.Adapter<SelectSensorListAdapter.ViewHolder> {
    String[] list;
    RvOnClick rvOnClick;
    String type;
    WaterTreatmentDb dB;
    MainConfigurationDao dao;
    int screenNo, layoutNo, windowNo;

    public SelectSensorListAdapter(String[] mList, String type, RvOnClick rvOnClick, int screenNo, int layoutNo, int windowNo) {
        this.list = mList;
        this.rvOnClick = rvOnClick;
        this.type = type;
        this.screenNo = screenNo;
        this.layoutNo = layoutNo;
        this.windowNo = windowNo;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selecsensor_rv_item, parent, false);
        dB = WaterTreatmentDb.getDatabase(parent.getContext());
        dao = dB.mainConfigurationDao();
        return new SelectSensorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectSensorListAdapter.ViewHolder holder, int position) {
        holder.sensorItem.setText(list[position]);
        List<MainConfigurationEntity> sensorlist = dao.getExitingSensorList(screenNo, layoutNo, windowNo, type);
        List<Integer> sensorexistinglist = new ArrayList<>();
        if (sensorlist.size() > 0) {
            for (int i = 0; i < sensorlist.size(); i++) {
                sensorexistinglist.add(sensorlist.get(i).getSensorSequenceNo());
            }
            for (int i = 0; i < list.length; i++) {
                if (sensorexistinglist.contains(position + 1)) {
                    holder.sensorItem.setChecked(true);
                    holder.sensorItem.setEnabled(false);
                }
            }
        }
        //sensorListCallback.SensorList(list);
        holder.sensorItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rvOnClick.onClick(compoundButton.getText().toString(), type, position);
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
