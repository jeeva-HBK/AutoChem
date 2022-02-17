package com.ionexchange.Adapters;

import static com.ionexchange.Others.PacketControl.SPILT_CHAR;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Entity.AlarmLogEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.BtnOnClick;
import com.ionexchange.R;

import java.util.List;

public class AlarmLogRvAdapter extends RecyclerView.Adapter<AlarmLogRvAdapter.itemHolder> {
    Context context;
    List<AlarmLogEntity> alarmLogEntityList;
    BtnOnClick btnOnClick;
    WaterTreatmentDb db;
    OutputConfigurationDao outputConfigurationDao;
    InputConfigurationDao inputConfigurationDao;
    TimerConfigurationDao timerConfigurationDao;

    public AlarmLogRvAdapter(List<AlarmLogEntity> alarmLogEntityList, BtnOnClick btnOnClick) {
        this.alarmLogEntityList = alarmLogEntityList;
        this.btnOnClick = btnOnClick;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        initDatabase(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_log_item, parent, false);
        context = parent.getContext();
        return new itemHolder(view);
    }

    private void initDatabase(Context context) {
        db = WaterTreatmentDb.getDatabase(context);
        inputConfigurationDao = db.inputConfigurationDao();
        outputConfigurationDao = db.outputConfigurationDao();
        timerConfigurationDao = db.timerConfigurationDao();
    }


    @Override
    public void onBindViewHolder(@NonNull itemHolder holder,int position) {
        if (position % 2 == 0) {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.ash));
            holder.button.setBackgroundColor(context.getResources().getColor(R.color.ash));
        }

        switch (alarmLogEntityList.get(position).getSensorType()){
            case "IN":
                holder.sensorType.setText(alarmLogEntityList.get(position).hardwareNo + " - "+
                        inputConfigurationDao.getInputLabel(Integer.parseInt(alarmLogEntityList.get(position).hardwareNo)));
                break;
            case "OP":
                holder.sensorType.setText(alarmLogEntityList.get(position).hardwareNo + " - "+
                        outputConfigurationDao.getOutputLabel(Integer.parseInt(alarmLogEntityList.get(position).hardwareNo)));
                break;
            case "TI":
                holder.sensorType.setText(alarmLogEntityList.get(position).hardwareNo + " - "+
                        timerConfigurationDao.getTimerName(Integer.parseInt(alarmLogEntityList.get(position).hardwareNo)));
                break;
            default:
                holder.sensorType.setText(alarmLogEntityList.get(position).hardwareNo);
                break;
        }

        holder.AlertName.setText(alarmLogEntityList.get(position).alarmLog);
        if (alarmLogEntityList.get(position).lockOutAlarm.equals("1")) {
            holder.button.setVisibility(View.VISIBLE);
        }else {
            holder.button.setVisibility(View.INVISIBLE);
        }
        holder.Date.setText(alarmLogEntityList.get(position).date + " | " + alarmLogEntityList.get(position).time);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOnClick.OnItemClick(alarmLogEntityList.get(position).sNo, Integer.parseInt(alarmLogEntityList.get(position).hardwareNo),
                        holder.button, alarmLogEntityList.get(position).getSensorType());
            }
        });
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
        MaterialButton button;
        public itemHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            sensorType = itemView.findViewById(R.id.sensor_type);
            AlertName = itemView.findViewById(R.id.alert_name);
            Date = itemView.findViewById(R.id.date);
            button = itemView.findViewById(R.id.ack_btn);
        }
    }
}
