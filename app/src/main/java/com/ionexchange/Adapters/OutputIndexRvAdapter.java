package com.ionexchange.Adapters;

import static com.ionexchange.Others.ApplicationClass.outputControlShortForm;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OutputIndexRvAdapter extends RecyclerView.Adapter<OutputIndexRvAdapter.ViewHolder> {
    RvOnClick rvOnClick;
    List<OutputConfigurationEntity> outputConfigurationEntityList;
    OutputKeepAliveDao outputKeepAliveDao;

    public OutputIndexRvAdapter(RvOnClick rvOnClick,
                                List<OutputConfigurationEntity> outputConfigurationEntityList, OutputKeepAliveDao outputKeepAliveDao) {
        this.rvOnClick = rvOnClick;
        this.outputConfigurationEntityList = outputConfigurationEntityList;
        this.outputKeepAliveDao = outputKeepAliveDao;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.output_rv_item, parent, false);
        return new OutputIndexRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.outputHeader.setText(outputConfigurationEntityList.get(position).outputHardwareNo + "");
        holder.outputModeValue.setText(outputConfigurationEntityList.get(position).outputLabel);
        if (!outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("N/A")) {
            if (outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("10")) {
                holder.outputNumber.setText(outputKeepAliveDao.getOutputRelayStatus(outputConfigurationEntityList.get(position).outputHardwareNo));
            } else {
                if (outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("8") ||
                        outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("9")) {
                    holder.outputNumber.setText(outputKeepAliveDao.getOutputRelayStatus(outputConfigurationEntityList.get(position).outputHardwareNo));
                } else {
                    if (!outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("N/A")) {
                        holder.outputNumber.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo))]);

                    }
                }
            }
        }


        holder.outputStatus.setText(outputConfigurationEntityList.get(position).outputMode);
        holder.outputLabel.setText(outputConfigurationEntityList.get(position).outputStatus);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.outputHeader.setTooltipText(outputConfigurationEntityList.get(position).outputLabel);
            holder.outputModeValue.setTooltipText(outputConfigurationEntityList.get(position).outputMode);
            holder.outputNumber.setTooltipText(outputConfigurationEntityList.get(position).outputHardwareNo + "");
            holder.outputStatus.setTooltipText(outputConfigurationEntityList.get(position).outputStatus);
            holder.outputLabel.setTooltipText(outputConfigurationEntityList.get(position).outputLabel);
        }
    }


    @Override
    public int getItemCount() {
        return outputConfigurationEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewBase;
        TextView outputHeader, outputModeValue, outputNumber, outputStatus, outputLabel;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            viewBase = itemView.findViewById(R.id.view_base);
            outputHeader = itemView.findViewById(R.id.output_label_header);
            outputModeValue = itemView.findViewById(R.id.output_mode_value);
            outputNumber = itemView.findViewById(R.id.output_number);
            outputStatus = itemView.findViewById(R.id.output_status);
            outputLabel = itemView.findViewById(R.id.output_label_value);
            viewBase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rvOnClick.onClick(Integer.parseInt(outputHeader.getText().toString()));
                }
            });
        }
    }
}
