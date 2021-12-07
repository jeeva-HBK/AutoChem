package com.ionexchange.Adapters;

import static com.ionexchange.Others.ApplicationClass.inputDAO;
import static com.ionexchange.Others.ApplicationClass.keepaliveDAO;
import static com.ionexchange.Others.ApplicationClass.outputControlShortForm;

import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//created by Silambu
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
        String analogValue = "";
        holder.outputstatusKey.setText(outputConfigurationEntityList.get(position).outputHardwareNo < 15 ? "Mode" : "Linked to");

        if (outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("N/A")){
            holder.outputNumber.setText("N/A");
        }
        if (!outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("N/A")) {
            if (outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("7")) {
                holder.outputNumber.setText(outputKeepAliveDao.getOutputRelayStatus(outputConfigurationEntityList.get(position).outputHardwareNo));
            } else {
                if (outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("5") ||
                        outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("6") ||
                        outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("8") ||
                        outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("9")) {
                    holder.outputNumber.setText(outputKeepAliveDao.getOutputRelayStatus(outputConfigurationEntityList.get(position).outputHardwareNo));
                } else {
                    if (!outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo).equals("N/A")) {
                        holder.outputNumber.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(outputConfigurationEntityList.get(position).outputHardwareNo))]);

                    }
                }
            }
        }
        analogValue = holder.outputNumber.getText().toString();
        holder.outputStatus.setText(outputConfigurationEntityList.get(position).outputMode);
        holder.outputLabel.setText(outputConfigurationEntityList.get(position).outputStatus);
        holder.outputstatusHeader.setVisibility(View.VISIBLE);
        if(outputConfigurationEntityList.get(position).outputHardwareNo < 15) {
            switch (analogValue) {
                case "D":
                    holder.viewBase.setBackgroundResource(R.drawable.ash_box);
                    break;
                case "F":
                    holder.viewBase.setBackgroundResource(R.drawable.green_box);
                    break;
                case "FÌ¶":
                    holder.viewBase.setBackgroundResource(R.drawable.red_box);
                    break;
                case "M for":
                    holder.viewBase.setBackgroundResource(R.drawable.merron_box);
                    break;
                default:
                    holder.viewBase.setBackgroundResource(R.drawable.blue_box);
                    break;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.outputHeader.setTooltipText(outputConfigurationEntityList.get(position).outputHardwareNo + "");
                holder.outputModeValue.setTooltipText(outputConfigurationEntityList.get(position).outputLabel);
                holder.outputNumber.setTooltipText(analogValue);
                holder.outputLabel.setTooltipText(outputConfigurationEntityList.get(position).outputStatus);
                holder.outputStatus.setTooltipText(outputConfigurationEntityList.get(position).outputMode);
            }
            switch (outputConfigurationEntityList.get(position).outputStatus) {
                case "Continuous":
                    holder.outputstatusHeader.setText("Dose Period");
                    holder.outputMode.setText("Dose Rate");
                    try {
                        String[] mode = outputConfigurationEntityList.get(position).outputMode.split("\\$");
                        holder.outputStatus.setText(mode[0]);
                        holder.outputNumber.setText(mode[1]);
                        holder.outputNumber.setTextSize(15f);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case "Bleed/Blow Down":
                case "Water Meter/Biocide":
                    holder.outputstatusHeader.setText("Accumulated Vol");
                    holder.outputMode.setText("Target PPM");
                    holder.outputNumber.setText(outputConfigurationEntityList.get(position).outputMode);
                    holder.outputStatus.setText(outputKeepAliveDao.getOutputRelayStatus(outputConfigurationEntityList.get(position).outputHardwareNo));
                    holder.outputNumber.setTextSize(15f);
                    break;
                case "On/Off":
                case "PID":
                    holder.outputstatusHeader.setText("Set Point");
                    holder.outputMode.setText("Value");
                    try {
                        String[] mode = outputConfigurationEntityList.get(position).outputMode.split("\\$");
                        holder.outputStatus.setText(mode[0]);
                        holder.outputMode.setText(inputDAO.getInputType(Integer.parseInt(mode[1])));
                        holder.outputNumber.setText(keepaliveDAO.getCurrentValue(Integer.parseInt(mode[1])));
                        holder.outputNumber.setTextSize(15f);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    holder.outputMode.setText("Value");
                    holder.outputstatusHeader.setVisibility(View.INVISIBLE);
                    holder.outputStatus.setVisibility(View.INVISIBLE);
                    break;
            }
        }else{
            holder.outputMode.setText("Value");
        }

    }


    @Override
    public int getItemCount() {
        return outputConfigurationEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewBase;
        TextView outputHeader, outputModeValue, outputNumber, outputStatus, outputLabel,
        outputstatusKey,outputstatusHeader,outputMode;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            viewBase = itemView.findViewById(R.id.view_base);
            outputHeader = itemView.findViewById(R.id.output_label_header);
            outputModeValue = itemView.findViewById(R.id.output_mode_value);
            outputNumber = itemView.findViewById(R.id.output_number);
            outputStatus = itemView.findViewById(R.id.output_status);
            outputLabel = itemView.findViewById(R.id.output_label_value);
            outputstatusKey = itemView.findViewById(R.id.output_label);
            outputstatusHeader = itemView.findViewById(R.id.output_status_header);
            outputMode = itemView.findViewById(R.id.output_mode_header);
            viewBase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rvOnClick.onClick(Integer.parseInt(outputHeader.getText().toString()));
                }
            });
        }
    }
}
