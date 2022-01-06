package com.ionexchange.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Interface.InputRvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

///Created By silambu
public class InputsIndexRvAdapter extends RecyclerView.Adapter<InputsIndexRvAdapter.ViewHolder> {

    public InputRvOnClick rvOnClick;
    List<InputConfigurationEntity> inputConfigurationEntityList;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;

    public InputsIndexRvAdapter(InputRvOnClick rvOnClick, List<InputConfigurationEntity>
            inputConfigurationEntityList,KeepAliveCurrentValueDao keepAliveCurrentValueDao) {
        this.rvOnClick = rvOnClick;
        this.inputConfigurationEntityList = inputConfigurationEntityList;
        this.keepAliveCurrentValueDao = keepAliveCurrentValueDao;
    }

    @NonNull
    @NotNull
    @Override
    public InputsIndexRvAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.input_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.inputNumber.setText(inputConfigurationEntityList.get(position).hardwareNo+"");
        holder.inputType.setText(inputConfigurationEntityList.get(position).inputType);
        holder.label.setText(inputConfigurationEntityList.get(position).inputLabel);
        holder.lowAlarm.setText(inputConfigurationEntityList.get(position).subValueOne);
        holder.highAlarm.setText(inputConfigurationEntityList.get(position).subValueTwo);

        holder.currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(inputConfigurationEntityList.get(position).hardwareNo));
        holder.lowAlarmKey.setText("Low Alarm");
        holder.highAlarmKey.setText("High Alarm");
        if(inputConfigurationEntityList.get(position).inputType.equalsIgnoreCase("Digital Input")) {
        holder.lowAlarmKey.setText("Open Message");
        holder.highAlarmKey.setText("Close Message");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.inputNumber.setTooltipText(inputConfigurationEntityList.get(position).hardwareNo+"");
            holder.inputType.setTooltipText(inputConfigurationEntityList.get(position).inputType);
            holder.label.setTooltipText(inputConfigurationEntityList.get(position).inputLabel);
            holder.lowAlarm.setTooltipText(inputConfigurationEntityList.get(position).subValueOne);
            holder.highAlarm.setTooltipText(inputConfigurationEntityList.get(position).subValueTwo);
        }
    }


    @Override
    public int getItemCount() {
        return inputConfigurationEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout view;
        TextView inputType, inputNumber, lowAlarm, highAlarm, label, currentValue,
         lowAlarmKey, highAlarmKey;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.rv_view);
            inputType = itemView.findViewById(R.id.typeTv);
            label = itemView.findViewById(R.id.textView5);
            inputNumber = itemView.findViewById(R.id.inputNumberRv);
            lowAlarm = itemView.findViewById(R.id.lowAlarmtvRv);
            highAlarm = itemView.findViewById(R.id.highAlarmTvRv);
            currentValue = itemView.findViewById(R.id.textView11);
            lowAlarmKey = itemView.findViewById(R.id.lowalarmKey);
            highAlarmKey = itemView.findViewById(R.id.highAlarmTv);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rvOnClick.onClick(inputNumber.getText().toString(),inputType.getText().toString());
                }
            });
        }

    }
}
