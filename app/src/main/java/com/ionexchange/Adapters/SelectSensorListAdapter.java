package com.ionexchange.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Interface.RvCheckedChange;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//created by Silambu
public class SelectSensorListAdapter extends RecyclerView.Adapter<SelectSensorListAdapter.ViewHolder> {

    RvCheckedChange rvCheckedChange;
    Integer adapterType;
    List<VirtualConfigurationEntity> virtualConfigurationEntityList;
    List<InputConfigurationEntity> inputConfigurationEntityList;
    List<OutputConfigurationEntity> outputConfigurationEntityList;
    int size;


    public SelectSensorListAdapter(RvCheckedChange rvCheckedChange, List<InputConfigurationEntity> inputConfigurationEntityList, int adapterType, String Type) {
        this.rvCheckedChange = rvCheckedChange;
        this.inputConfigurationEntityList = inputConfigurationEntityList;
        this.adapterType = adapterType;
        size = inputConfigurationEntityList.size();
    }

    public SelectSensorListAdapter(RvCheckedChange rvCheckedChange, List<VirtualConfigurationEntity> virtualConfigurationEntityList, int adapterType) {
        this.rvCheckedChange = rvCheckedChange;
        this.virtualConfigurationEntityList = virtualConfigurationEntityList;
        this.adapterType = adapterType;
        size = virtualConfigurationEntityList.size();
    }

    public SelectSensorListAdapter(RvCheckedChange rvCheckedChange, List<OutputConfigurationEntity> outputConfigurationEntityList, int adapterType, boolean type) {
        this.rvCheckedChange = rvCheckedChange;
        this.outputConfigurationEntityList = outputConfigurationEntityList;
        this.adapterType = adapterType;
        size = outputConfigurationEntityList.size();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selecsensor_rv_item, parent, false);
        return new SelectSensorListAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (adapterType) {
            case 0:
                holder.sensorItem.setText(inputConfigurationEntityList.get(position).inputsequenceName
                        + "(" + "Input No: " + inputConfigurationEntityList.get(position).getHardwareNo() + ")");

                break;
            case 1:
                holder.sensorItem.setText(virtualConfigurationEntityList.get(position).virtualType
                        + "(" + virtualConfigurationEntityList.get(position).inputLabel + ")");
                break;

            case 2:
                holder.sensorItem.setText(outputConfigurationEntityList.get(position).outputType
                        + "(" + outputConfigurationEntityList.get(position).outputLabel + ")");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox sensorItem;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            sensorItem = itemView.findViewById(R.id.sensorItemRv);

            sensorItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switch (adapterType) {
                        case 0:
                            rvCheckedChange.onCheckChanged(inputConfigurationEntityList.get(getLayoutPosition()), buttonView, 0);
                            break;
                        case 1:
                            rvCheckedChange.onCheckChanged(virtualConfigurationEntityList.get(getLayoutPosition()), buttonView, 1);
                            break;
                        case 2:
                            rvCheckedChange.onCheckChanged(outputConfigurationEntityList.get(getLayoutPosition()), buttonView, 2);
                            break;
                    }

                }
            });
        }
    }
}
