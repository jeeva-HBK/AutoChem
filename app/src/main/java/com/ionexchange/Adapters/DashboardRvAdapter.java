package com.ionexchange.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import java.util.List;

///Created By silambu
public class DashboardRvAdapter extends RecyclerView.Adapter<DashboardRvAdapter.itemHolder> {


    int layout;
    List<MainConfigurationEntity> mainConfigurationEntityList;
    RvOnClick rvOnClick;
    WaterTreatmentDb db;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;
    InputConfigurationDao inputConfigurationDao;


    public DashboardRvAdapter(int layout, List<MainConfigurationEntity> mainConfigurationEntityList, RvOnClick rvOnClick) {
        this.layout = layout;
        this.mainConfigurationEntityList = mainConfigurationEntityList;
        this.rvOnClick = rvOnClick;
    }

    @NonNull
    @Override
    public DashboardRvAdapter.itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        initDatabase(parent.getContext());
        View view = null;
        switch (layout) {
            case 1:
            case 2:
            case 5:
            case 6:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_layout_one_item, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_layout_three_item, parent, false);
                break;
            case 4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_layout_four_item, parent, false);
                break;

        }
        return new DashboardRvAdapter.itemHolder(view);
    }

    private void initDatabase(Context context) {
        db = WaterTreatmentDb.getDatabase(context);
        keepAliveCurrentValueDao = db.keepAliveCurrentValueDao();
        inputConfigurationDao = db.inputConfigurationDao();
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardRvAdapter.itemHolder holder, int position) {
        ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        switch (layout) {
            case 1:
                holder.constraintLayout.setLayoutParams(constraintLayoutParams);
                constraintLayoutParams.setMargins(5, 5, 5, 5);
                holder.seqNoOne.setTextSize(30);
                holder.currentKeyOne.setTextSize(30);
                holder.currentValueOne.setTextSize(70);
                holder.hardwareNoOne.setTextSize(30);
                holder.lowAlarmOne.setTextSize(30);
                holder.highAlarmOne.setTextSize(30);
                holder.lowKeyOne.setTextSize(20);
                holder.highKeyOne.setTextSize(20);
                defaultLayout(holder.seqNoOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne, holder.currentValueOne, position);
                break;

            case 2:
                holder.seqNoOne.setTextSize(30);
                holder.currentKeyOne.setTextSize(25);
                holder.currentValueOne.setTextSize(50);
                holder.hardwareNoOne.setTextSize(30);
                holder.lowAlarmOne.setTextSize(30);
                holder.highAlarmOne.setTextSize(30);
                holder.lowKeyOne.setTextSize(20);
                holder.highKeyOne.setTextSize(20);
                defaultLayout(holder.seqNoOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne, holder.currentValueOne, position);
                break;

            case 3:
            case 4:
                changedLayout(holder.seqNoOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne, holder.currentValueOne,
                        holder.seqNoTwo, holder.hardwareNoTwo, holder.lowAlarmTwo, holder.highAlarmTwo, holder.currentValueTwo,
                        holder.seqNoThree, holder.hardwareNoThree, holder.lowAlarmThree, holder.highAlarmThree, holder.currentValueThree);

                break;

            case 5:
                holder.lowKeyOne.setTextSize(20);
                holder.highKeyOne.setTextSize(20);
                defaultLayout(holder.seqNoOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne, holder.currentValueOne, position);
                break;

            case 6:
                defaultLayout(holder.seqNoOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne, holder.currentValueOne, position);
                break;

        }


    }

    void defaultLayout(TextView seq, TextView hardwareNoOne, TextView lowAlarmOne, TextView highAlarmOne, TextView currentValue, int position) {
        seq.setText("N/A");
        hardwareNoOne.setText(mainConfigurationEntityList.get(position).hardware_no + "");
        lowAlarmOne.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
        highAlarmOne.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
        if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no) != null) {
            currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no));
        }
    }

    void changedLayout(TextView seqOne, TextView hardwareNoOne, TextView lowAlarmOne, TextView highAlarmOne,
                       TextView currentValueOne, TextView seqTwo, TextView hardwareNoTwo, TextView lowAlarmTwo, TextView highAlarmTwo,
                       TextView currentValueTwo, TextView seqThree, TextView hardwareNoThree, TextView lowAlarmThree, TextView highAlarmThree,
                       TextView currentValueThree) {
        seqOne.setText("N/A");
        hardwareNoOne.setText(mainConfigurationEntityList.get(0).hardware_no + "");
        lowAlarmOne.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(0).hardware_no)));
        highAlarmOne.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(0).hardware_no)));
        if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no) != null) {
            currentValueOne.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no));
        }
        seqTwo.setText("N/A");
        hardwareNoTwo.setText(mainConfigurationEntityList.get(1).hardware_no + "");
        lowAlarmTwo.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(1).hardware_no)));
        highAlarmTwo.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(1).hardware_no)));
        if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(1).hardware_no) != null) {
            currentValueTwo.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(1).hardware_no));
        }
        seqThree.setText("N/A");
        hardwareNoThree.setText(mainConfigurationEntityList.get(2).hardware_no + "");
        lowAlarmThree.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(2).hardware_no)));
        highAlarmThree.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(2).hardware_no)));
        if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(2).hardware_no) != null) {
            currentValueThree.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(2).hardware_no));
        }
    }

    @Override
    public int getItemCount() {
        return mainConfigurationEntityList.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        View layoutOne, layoutTwo, layoutThree;
        TextView seqNoOne, hardwareNoOne, currentValueOne, lowAlarmOne, highAlarmOne, lowKeyOne, highKeyOne, currentKeyOne;
        TextView seqNoTwo, hardwareNoTwo, currentValueTwo, lowAlarmTwo, highAlarmTwo, lowKeyTwo, highKeyTwo, currentKeyTwo;
        TextView seqNoThree, hardwareNoThree, currentValueThree, lowAlarmThree, highAlarmThree, lowKeyThree, highKeyThree, currentKeyThree;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layout);
            layoutOne = itemView.findViewById(R.id.layoutOne);
            layoutTwo = itemView.findViewById(R.id.layout_two);
            layoutThree = itemView.findViewById(R.id.layout_three);
            seqNoOne = itemView.findViewById(R.id.layout_1_seq_number);
            hardwareNoOne = itemView.findViewById(R.id.layout_1_hardware_no);
            currentValueOne = itemView.findViewById(R.id.layout_one_current_value);
            highAlarmOne = itemView.findViewById(R.id.layout_1_high_alarm_value);
            lowAlarmOne = itemView.findViewById(R.id.layout_1_low_alarm_value);
            lowKeyOne = itemView.findViewById(R.id.txt_layout_1_alarm_low);
            highKeyOne = itemView.findViewById(R.id.txt_layout_1_alarm_high);
            currentKeyOne = itemView.findViewById(R.id.txt_layout_1_current);

            seqNoTwo = itemView.findViewById(R.id.layout_2_seq_number);
            hardwareNoTwo = itemView.findViewById(R.id.layout_2_hardware_no);
            currentValueTwo = itemView.findViewById(R.id.layout_2_current_value);
            highAlarmTwo = itemView.findViewById(R.id.layout_2_high_alarm_value);
            lowAlarmTwo = itemView.findViewById(R.id.layout_2_low_alarm_value);
            lowKeyTwo = itemView.findViewById(R.id.txt_layout_2_alarm_low);
            highKeyTwo = itemView.findViewById(R.id.txt_layout_2_alarm_high);
            currentKeyTwo = itemView.findViewById(R.id.txt_layout_2_current);


            seqNoThree = itemView.findViewById(R.id.layout_3_seq_number);
            hardwareNoThree = itemView.findViewById(R.id.layout_3_hardware_no);
            currentValueThree = itemView.findViewById(R.id.layout_three_current_value);
            highAlarmThree = itemView.findViewById(R.id.layout_3_high_alarm_value);
            lowAlarmThree = itemView.findViewById(R.id.layout_3_low_alarm_value);
            lowKeyThree = itemView.findViewById(R.id.txt_layout_3_alarm_low);
            highKeyThree = itemView.findViewById(R.id.txt_layout_3_alarm_high);
            currentKeyThree = itemView.findViewById(R.id.txt_layout_3_current);

            switch (layout) {
                case 1:
                case 2:
                case 5:
                case 6:
                    constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(String.valueOf(mainConfigurationEntityList.get(getAdapterPosition()).hardware_no),
                                    mainConfigurationEntityList.get(getAdapterPosition()).inputType, getAdapterPosition());
                        }
                    });
                    break;

                case 3:
                case 4:
                    layoutOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(String.valueOf(mainConfigurationEntityList.get(getAdapterPosition()).hardware_no),
                                    mainConfigurationEntityList.get(0).inputType, 0);
                        }
                    });
                    layoutTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(String.valueOf(mainConfigurationEntityList.get(getAdapterPosition()).hardware_no),
                                    mainConfigurationEntityList.get(1).inputType, 1);
                        }
                    });
                    layoutThree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(String.valueOf(mainConfigurationEntityList.get(getAdapterPosition()).hardware_no),
                                    mainConfigurationEntityList.get(2).inputType, 2);
                        }
                    });
                    break;
            }

        }
    }
}
