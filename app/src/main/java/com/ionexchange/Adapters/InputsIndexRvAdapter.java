package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Interface.InputRvOnClick;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InputsIndexRvAdapter extends RecyclerView.Adapter<InputsIndexRvAdapter.ViewHolder> {

    public InputRvOnClick rvOnClick;
    List<InputConfigurationEntity> inputConfigurationEntityList;


    public InputsIndexRvAdapter(InputRvOnClick rvOnClick, List<InputConfigurationEntity> inputConfigurationEntityList) {
        this.rvOnClick = rvOnClick;
        this.inputConfigurationEntityList = inputConfigurationEntityList;
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
    }


    @Override
    public int getItemCount() {
        return inputConfigurationEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout view;
        TextView inputType, inputNumber, lowAlarm, highAlarm, label;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.rv_view);
            inputType = itemView.findViewById(R.id.typeTv);
            label = itemView.findViewById(R.id.textView5);
            inputNumber = itemView.findViewById(R.id.inputNumberRv);
            lowAlarm = itemView.findViewById(R.id.lowAlarmtvRv);
            highAlarm = itemView.findViewById(R.id.highAlarmTvRv);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rvOnClick.onClick(inputNumber.getText().toString(),inputType.getText().toString());
                }
            });
        }
    }
}
