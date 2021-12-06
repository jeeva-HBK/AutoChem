package com.ionexchange.Adapters;

import static com.ionexchange.Others.ApplicationClass.outputControlShortForm;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.OutputKeepAliveDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import java.util.List;

//created by Silambu

public class DashboardRvAdapter extends RecyclerView.Adapter<DashboardRvAdapter.itemHolder> {
    int layout;
    List<MainConfigurationEntity> mainConfigurationEntityList;
    RvOnClick rvOnClick;
    WaterTreatmentDb db;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;
    OutputKeepAliveDao outputKeepAliveDao;
    InputConfigurationDao inputConfigurationDao;
    VirtualConfigurationDao virtualConfigurationDao;
    OutputConfigurationDao outputConfigurationDao;


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
        outputConfigurationDao = db.outputConfigurationDao();
        outputKeepAliveDao = db.outputKeepAliveDao();
        virtualConfigurationDao = db.virtualConfigurationDao();
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardRvAdapter.itemHolder holder, int position) {


        ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        switch (layout) {
            case 1:
                holder.constraintLayout.setLayoutParams(constraintLayoutParams);
                constraintLayoutParams.setMargins(5, 5, 5, 5);
                holder.labeLOne.setTextSize(30);
                holder.currentKeyOne.setTextSize(30);
                holder.currentValueOne.setTextSize(70);
                holder.hardwareNoOne.setTextSize(30);
                holder.lowAlarmOne.setTextSize(30);
                holder.highAlarmOne.setTextSize(30);
                holder.lowKeyOne.setTextSize(20);
                holder.highKeyOne.setTextSize(20);
                defaultLayout(holder.labeLOne, holder.sensorLabelOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne,
                        holder.currentValueOne, holder.unitOne, holder.typeOne, holder.currentKeyOne, holder.lowKeyOne, holder.highKeyOne, position, 1);
                break;

            case 2:
                holder.labeLOne.setTextSize(30);
                holder.currentKeyOne.setTextSize(25);
                holder.currentValueOne.setTextSize(50);
                holder.hardwareNoOne.setTextSize(30);
                holder.lowAlarmOne.setTextSize(30);
                holder.highAlarmOne.setTextSize(30);
                holder.lowKeyOne.setTextSize(20);
                holder.highKeyOne.setTextSize(20);
                defaultLayout(holder.labeLOne, holder.sensorLabelOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne,
                        holder.currentValueOne, holder.unitOne, holder.typeOne, holder.currentKeyOne,
                        holder.lowKeyOne, holder.highKeyOne, position, 2);
                break;

            case 3:
            case 4:
                changedLayout(holder.labeLOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne, holder.currentValueOne,
                        holder.labeLTwo, holder.hardwareNoTwo, holder.lowAlarmTwo, holder.highAlarmTwo, holder.currentValueTwo,
                        holder.labeLThree, holder.hardwareNoThree, holder.lowAlarmThree, holder.highAlarmThree, holder.currentValueThree,
                        holder.sensorLabelOne, holder.unitOne, holder.typeOne, holder.sensorLabelTwo, holder.uniTwo,
                        holder.typeTwo, holder.sensorLabelThree, holder.uniThree, holder.typeThree, holder.lowKeyOne,
                        holder.lowKeyTwo, holder.lowKeyThree, holder.highKeyOne, holder.highKeyTwo, holder.highKeyThree,
                        holder.currentKeyOne, holder.currentKeyTwo, holder.currentKeyThree);
                break;

            case 5:
                holder.lowKeyOne.setTextSize(20);
                holder.highKeyOne.setTextSize(20);
                defaultLayout(holder.labeLOne, holder.sensorLabelOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne,
                        holder.currentValueOne, holder.unitOne, holder.typeOne, holder.currentKeyOne, holder.lowKeyOne, holder.highKeyOne, position, 5);
                break;

            case 6:
                defaultLayout(holder.labeLOne, holder.sensorLabelOne, holder.hardwareNoOne, holder.lowAlarmOne, holder.highAlarmOne,
                        holder.currentValueOne, holder.unitOne, holder.typeOne, holder.currentKeyOne, holder.lowKeyOne, holder.highKeyOne, position, 6);
                break;
        }
    }

    void defaultLayout(TextView seq, TextView label, TextView hardwareNoOne, TextView lowAlarmOne, TextView highAlarmOne,
                       TextView currentValue, TextView unitOne, TextView typeOne, TextView currentMode, TextView lowKey, TextView highKey, int position, int layout) {

        if (mainConfigurationEntityList.get(position).inputType != null) {
            if (mainConfigurationEntityList.get(position).inputType.contains("Output")) {
                currentMode.setText("Output Status");
                lowKey.setText("Mode");

                highKey.setText(mainConfigurationEntityList.get(position).hardware_no > 14 ? "Linked to" : "Mode");

                unitOne.setVisibility(View.INVISIBLE);
                typeOne.setVisibility(View.INVISIBLE);
                seq.setText(mainConfigurationEntityList.get(position).inputType);
                if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    label.setText(outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no));
                }
                hardwareNoOne.setText(mainConfigurationEntityList.get(position).hardware_no + "");
                if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    lowAlarmOne.setText(outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(position).hardware_no)));
                }
                if (outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    highAlarmOne.setText(outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no)));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                        label.setTooltipText(outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no));
                    }
                    if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        lowAlarmOne.setTooltipText(outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        highAlarmOne.setTooltipText(outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                }
                if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    if (!outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("N/A")) {
                        if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("7")) {
                            currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                        } else {
                            if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("6") ||
                                    outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("5")){
                                currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                            }else {
                                currentValue.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no))]);
                            }

                        }

                    }

                }
                if(mainConfigurationEntityList.get(position).hardware_no < 15){
                    switch (highAlarmOne.getText().toString()) {
                        case "Continuous":
                            lowKey.setText("Dose Period");
                            break;
                        case "Bleed/Blow Down":
                        case "Water Meter/Biocide":
                            lowKey.setText("Accumulated Vol");
                            break;
                        case "On/Off":
                        case "PID":
                            lowKey.setText("Set Point");
                            break;
                        default:
                            break;
                    }
                }
            }

            if (mainConfigurationEntityList.get(position).inputType.contains("virtual")) {
                currentMode.setText("Current Value");
                lowKey.setText("Low Alarm");
                highKey.setText("High Alarm");
                unitOne.setVisibility(View.INVISIBLE);
                typeOne.setVisibility(View.INVISIBLE);
                seq.setText(mainConfigurationEntityList.get(position).inputType);
                if (virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    label.setText(virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(position).hardware_no));
                }
                hardwareNoOne.setText(mainConfigurationEntityList.get(position).hardware_no + "");
                if (virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    lowAlarmOne.setText(virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                }
                if (virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    highAlarmOne.setText(virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                }
               /* if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)).equals("N/A")) {
                        unitOne.setText("");
                    } else {
                        unitOne.setText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                }
                if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)).equals("N/A")) {
                        typeOne.setText("");
                    } else {
                        typeOne.setText(inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                }
*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (mainConfigurationEntityList.get(position).inputType != null) {
                        seq.setTooltipText(mainConfigurationEntityList.get(position).inputType);
                    }
                    if (virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                        label.setTooltipText(virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(position).hardware_no));
                    }
                    hardwareNoOne.setTooltipText(mainConfigurationEntityList.get(position).hardware_no + "");
                    if (virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        lowAlarmOne.setTooltipText(virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        highAlarmOne.setTooltipText(virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                    }

                    currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no));
                   /* if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        unitOne.setTooltipText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        typeOne.setTooltipText(inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)));
                    }*/
                }
            }

            if (mainConfigurationEntityList.get(position).inputType.contains("pH")
                    || mainConfigurationEntityList.get(position).inputType.contains("ORP")
                    || mainConfigurationEntityList.get(position).inputType.contains("Temperature")
                    || mainConfigurationEntityList.get(position).inputType.contains("Flow/Water Meter")
                    || mainConfigurationEntityList.get(position).inputType.contains("Contacting Conductivity")
                    || mainConfigurationEntityList.get(position).inputType.contains("Toroidal Conductivity")
                    || mainConfigurationEntityList.get(position).inputType.contains("Analog Input")
                    || mainConfigurationEntityList.get(position).inputType.contains("Tank Level")
                    || mainConfigurationEntityList.get(position).inputType.contains("Digital Input")
                    || mainConfigurationEntityList.get(position).inputType.contains("Modbus Sensor")) {
                currentMode.setText("Current Value");
                lowKey.setText("Low Alarm");
                highKey.setText("High Alarm");
                unitOne.setVisibility(View.VISIBLE);
                typeOne.setVisibility(View.VISIBLE);
                if (mainConfigurationEntityList.get(position).inputType.contains("Digital Input")) {
                    /*lowKey.setText("Open\nMessage");
                    highKey.setText("Close\nMessage");
                    lowKey.setGravity(Gravity.CENTER);
                    highKey.setGravity(Gravity.CENTER);*/
                    lowKey.setVisibility(View.INVISIBLE);
                    highKey.setVisibility(View.INVISIBLE);
                    lowAlarmOne.setVisibility(View.INVISIBLE);
                    highAlarmOne.setVisibility(View.INVISIBLE);

                }
                seq.setText(mainConfigurationEntityList.get(position).inputType);
                if (inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    label.setText(inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(position).hardware_no));
                }
                hardwareNoOne.setText(mainConfigurationEntityList.get(position).hardware_no + "");
                if (inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    lowAlarmOne.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                }
                if (inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    highAlarmOne.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                }
                if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)).equals("N/A")) {
                        unitOne.setText("");
                    } else {
                        unitOne.setText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                }
                if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)).equals("N/A")) {
                        typeOne.setText("");
                    } else {
                        typeOne.setText(inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (mainConfigurationEntityList.get(position).inputType != null) {
                        seq.setTooltipText(mainConfigurationEntityList.get(position).inputType);
                    }
                    if (inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                        label.setTooltipText(inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(position).hardware_no));
                    }
                    hardwareNoOne.setTooltipText(mainConfigurationEntityList.get(position).hardware_no + "");
                    if (inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        lowAlarmOne.setTooltipText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        highAlarmOne.setTooltipText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        unitOne.setTooltipText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                        typeOne.setTooltipText(inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                    if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no) != null) {
                        if (mainConfigurationEntityList.get(position).inputType.contains("Digital Input")) {
                            currentValue.setTooltipText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no)
                                    .equals("OPEN") ? inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) :
                                    inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                        }
                    }
                }
                if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no));

                    if (mainConfigurationEntityList.get(position).inputType.contains("Digital Input")) {
                        currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no)
                                .equals("OPEN") ? inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) :
                                inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
                    }
                }
            }
        }
    }

    void changedLayout(TextView seqOne, TextView hardwareNoOne, TextView lowAlarmOne, TextView highAlarmOne,
                       TextView currentValueOne, TextView seqTwo, TextView hardwareNoTwo, TextView lowAlarmTwo, TextView highAlarmTwo,
                       TextView currentValueTwo, TextView seqThree, TextView hardwareNoThree, TextView lowAlarmThree, TextView highAlarmThree,
                       TextView currentValueThree,
                       TextView sensorLabelOne, TextView unitOne, TextView typeOne,
                       TextView sensorLabelTwo, TextView uniTwo, TextView typeTwo,
                       TextView sensorLabelThree, TextView uniThree, TextView typeThree,
                       TextView lowKeyOne, TextView lowKeyTwo, TextView lowKeyThree, TextView highKeyOne, TextView highKeyTwo, TextView highKeyThree,
                       TextView currentKeyOne, TextView currentKeyTwo, TextView currentKeyThree) {


        if (mainConfigurationEntityList.get(0).inputType.contains("Output")) {
            lowKeyOne.setText("Mode");
            highKeyOne.setText(mainConfigurationEntityList.get(0).hardware_no > 14 ? "Linked to" : "Mode");
            currentKeyOne.setText("Output Status");
            unitOne.setVisibility(View.INVISIBLE);
            typeOne.setVisibility(View.INVISIBLE);
            if (mainConfigurationEntityList.get(0).inputType != null) {
                seqOne.setText(mainConfigurationEntityList.get(0).inputType);
            }
            if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(0).hardware_no) != null) {
                sensorLabelOne.setText(outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(0).hardware_no));
            }
            hardwareNoOne.setText(mainConfigurationEntityList.get(0).hardware_no + "");

            if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(0).hardware_no)) != null) {
                lowAlarmOne.setText(outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(0).hardware_no)));
            }
            if (outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(0).hardware_no)) != null) {
                highAlarmOne.setText(outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(0).hardware_no)));
            }
            if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(0).hardware_no) != null) {
                if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(0).hardware_no).equals("7")) {
                    currentValueOne.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(2).hardware_no));
                } else {
                    if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(0).hardware_no).equals("6") ||
                            outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(0).hardware_no).equals("5")) {
                        currentValueOne.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(0).hardware_no));
                    } else {
                        currentValueOne.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(0).hardware_no))]);
                    }

                }

            }
            if(mainConfigurationEntityList.get(0).hardware_no < 15){
                switch (highAlarmOne.getText().toString()) {
                    case "Continuous":
                        lowKeyOne.setText("Dose Period");
                        break;
                    case "Bleed/Blow Down":
                    case "Water Meter/Biocide":
                        lowKeyOne.setText("Accumulated Vol");
                        break;
                    case "On/Off":
                    case "PID":
                        lowKeyOne.setText("Set Point");
                        break;
                    default:
                        break;
                }
            }
        }
        if (mainConfigurationEntityList.get(1).inputType.contains("Output")) {
            lowKeyTwo.setText("Mode");
            highKeyTwo.setText(mainConfigurationEntityList.get(1).hardware_no > 14 ? "Linked to" : "Mode");
            currentKeyTwo.setText("Output Status");
            uniTwo.setVisibility(View.INVISIBLE);
            typeTwo.setVisibility(View.INVISIBLE);
            if (mainConfigurationEntityList.get(1).inputType != null) {
                seqTwo.setText(mainConfigurationEntityList.get(1).inputType);
            }
            if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(1).hardware_no) != null) {
                sensorLabelTwo.setText(outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(1).hardware_no));
            }
            hardwareNoTwo.setText(mainConfigurationEntityList.get(1).hardware_no + "");
            if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                lowAlarmTwo.setText(outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(1).hardware_no)));
            }
            if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                highAlarmTwo.setText(outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(1).hardware_no)));
            }

            if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(1).hardware_no) != null) {
                if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(1).hardware_no).equals("7")) {
                    currentValueTwo.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(1).hardware_no));
                } else {
                    if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(1).hardware_no).equals("6") ||
                            outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(1).hardware_no).equals("5")) {
                        currentValueTwo.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(1).hardware_no));
                    } else {
                        currentValueTwo.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(1).hardware_no))]);
                    }

                }
            }
            if(mainConfigurationEntityList.get(1).hardware_no < 15){
                switch (highAlarmTwo.getText().toString()) {
                    case "Continuous":
                        lowKeyTwo.setText("Dose Period");
                        break;
                    case "Bleed/Blow Down":
                    case "Water Meter/Biocide":
                        lowKeyTwo.setText("Accumulated Vol");
                        break;
                    case "On/Off":
                    case "PID":
                        lowKeyTwo.setText("Set Point");
                        break;
                    default:
                        break;
                }
            }
        }
        if (mainConfigurationEntityList.get(2).inputType.contains("Output")) {
            lowKeyThree.setText("Mode");
            highKeyThree.setText(mainConfigurationEntityList.get(2).hardware_no > 14 ? "Linked to" : "Mode");
            currentKeyThree.setText("Output Status");
            uniThree.setVisibility(View.INVISIBLE);
            typeThree.setVisibility(View.INVISIBLE);
            if (mainConfigurationEntityList.get(2).inputType != null) {
                seqThree.setText(mainConfigurationEntityList.get(2).inputType);
            }
            if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(2).hardware_no) != null) {
                sensorLabelThree.setText(outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(2).hardware_no));
            }
            hardwareNoThree.setText(mainConfigurationEntityList.get(2).hardware_no + "");
            if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                lowAlarmThree.setText(outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(2).hardware_no)));
            }
            if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                highAlarmThree.setText(outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(2).hardware_no)));
            }
            if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(2).hardware_no) != null) {
                if (!outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(2).hardware_no).equals("N/A")) {
                    if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(2).hardware_no).equals("7")) {
                        currentValueThree.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(2).hardware_no));
                    } else {
                        if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(2).hardware_no).equals("6") ||
                                outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(2).hardware_no).equals("5")){
                            currentValueThree.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(2).hardware_no));
                        }else {
                            currentValueThree.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(2).hardware_no))]);
                        }

                    }

                }

            }
            if(mainConfigurationEntityList.get(2).hardware_no < 15){
                switch (highAlarmThree.getText().toString()) {
                    case "Continuous":
                        lowKeyThree.setText("Dose Period");
                        break;
                    case "Bleed/Blow Down":
                    case "Water Meter/Biocide":
                        lowKeyThree.setText("Accumulated Vol");
                        break;
                    case "On/Off":
                    case "PID":
                        lowKeyThree.setText("Set Point");
                        break;
                    default:
                        break;
                }
            }
        }


        if (mainConfigurationEntityList.get(0).inputType.contains("virtual")) {
            lowKeyOne.setText("Low Alarm");
            highKeyOne.setText("High Alarm ");
            unitOne.setVisibility(View.VISIBLE);
            typeOne.setVisibility(View.VISIBLE);
            hardwareNoOne.setText(String.valueOf(mainConfigurationEntityList.get(0).getHardware_no()));
            currentKeyOne.setText("Current Value");
            if (mainConfigurationEntityList.get(0).inputType != null) {
                seqOne.setText(mainConfigurationEntityList.get(0).inputType);
            }
            if (virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(0).hardware_no) != null) {
                sensorLabelOne.setText(virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(0).hardware_no));
            }

            if (virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(0).hardware_no)) != null) {
                lowAlarmOne.setText(virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(0).hardware_no)));
            }
            if (virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(0).hardware_no)) != null) {
                highAlarmOne.setText(virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(0).hardware_no)));
            }
            if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no) != null) {
                currentValueOne.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no));
            }
        }
        if (mainConfigurationEntityList.get(1).inputType.contains("virtual")) {
            lowKeyTwo.setText("Low Alarm");
            highKeyTwo.setText("High Alarm");
            currentKeyTwo.setText("Current Value");
            uniTwo.setVisibility(View.INVISIBLE);
            hardwareNoTwo.setText(mainConfigurationEntityList.get(1).getHardware_no() + "");
            typeTwo.setVisibility(View.INVISIBLE);
            if (mainConfigurationEntityList.get(1).inputType != null) {
                seqTwo.setText(mainConfigurationEntityList.get(1).inputType);
            }

            if (virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(1).hardware_no) != null) {
                sensorLabelTwo.setText(virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(1).hardware_no));
            }

            if (virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                lowAlarmTwo.setText(virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(1).hardware_no)));
            }

            if (virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                highAlarmTwo.setText(virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(1).hardware_no)));
            }

            if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(1).hardware_no) != null) {
                currentValueTwo.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(1).hardware_no));
            }
        }
        if (mainConfigurationEntityList.get(2).inputType.contains("virtual")) {
            lowKeyThree.setText("Low Alarm");
            highKeyThree.setText("High Alarm");
            currentKeyThree.setText("Current Value");
            uniThree.setVisibility(View.INVISIBLE);
            typeThree.setVisibility(View.INVISIBLE);
            hardwareNoThree.setText(mainConfigurationEntityList.get(2).hardware_no + "");
            if (mainConfigurationEntityList.get(2).inputType != null) {
                seqThree.setText(mainConfigurationEntityList.get(2).inputType);
            }
            if (virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(2).hardware_no) != null) {
                sensorLabelThree.setText(virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(2).hardware_no));
            }
            if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                lowAlarmThree.setText(virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(2).hardware_no)));
            }

            if (virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                highAlarmThree.setText(virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(2).hardware_no)));
            }
            if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(2).hardware_no) != null) {
                currentValueThree.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(2).hardware_no));
            }
        }


        if (mainConfigurationEntityList.get(0).inputType.contains("pH")
                || mainConfigurationEntityList.get(0).inputType.contains("ORP")
                || mainConfigurationEntityList.get(0).inputType.contains("Temperature")
                || mainConfigurationEntityList.get(0).inputType.contains("Flow/Water Meter")
                || mainConfigurationEntityList.get(0).inputType.contains("Contacting Conductivity")
                || mainConfigurationEntityList.get(0).inputType.contains("Toroidal Conductivity")
                || mainConfigurationEntityList.get(0).inputType.contains("Analog Input")
                || mainConfigurationEntityList.get(0).inputType.contains("Tank Level")
                || mainConfigurationEntityList.get(0).inputType.contains("Digital Input")
                || mainConfigurationEntityList.get(0).inputType.contains("Modbus Sensor")) {
            lowKeyOne.setText("Low Alarm");
            highKeyOne.setText("High Alarm");
            currentKeyOne.setText("Current Value");
            unitOne.setVisibility(View.VISIBLE);
            typeOne.setVisibility(View.VISIBLE);
            hardwareNoOne.setText(mainConfigurationEntityList.get(0).hardware_no + "");
            if (mainConfigurationEntityList.get(0).inputType.contains("Digital Input")) {
                    /*lowKey.setText("Open\nMessage");
                    highKey.setText("Close\nMessage");
                    lowKey.setGravity(Gravity.CENTER);
                    highKey.setGravity(Gravity.CENTER);*/
                lowKeyOne.setVisibility(View.INVISIBLE);
                highKeyOne.setVisibility(View.INVISIBLE);
                lowAlarmOne.setVisibility(View.INVISIBLE);
                highAlarmOne.setVisibility(View.INVISIBLE);
            }
            if (inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(0).hardware_no) != null) {
                sensorLabelOne.setText(inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(0).hardware_no));
            }
            if (mainConfigurationEntityList.get(0).inputType != null) {
                seqOne.setText(mainConfigurationEntityList.get(0).inputType);
            }
            if (inputConfigurationDao.getUnit(mainConfigurationEntityList.get(0).hardware_no) != null) {
                if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(0).hardware_no)).equals("N/A")) {
                    unitOne.setText("");
                } else {
                    unitOne.setText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(0).hardware_no)));
                }
            }

            if (inputConfigurationDao.getType(mainConfigurationEntityList.get(0).hardware_no) != null) {
                if (inputConfigurationDao.getType((mainConfigurationEntityList.get(0).hardware_no)).equals("N/A")) {
                    typeOne.setText("");
                } else {
                    typeOne.setText(inputConfigurationDao.getType((mainConfigurationEntityList.get(0).hardware_no)));
                }
            }
            if (inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(0).hardware_no)) != null) {
                lowAlarmOne.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(0).hardware_no)));
            }

            if (inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(0).hardware_no)) != null) {
                highAlarmOne.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(0).hardware_no)));
            }

           /* if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no) != null) {
                currentValueOne.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no));
            }*/
            setDigitalInput(currentValueOne, 0);
        }
        if (mainConfigurationEntityList.get(1).inputType.contains("pH")
                || mainConfigurationEntityList.get(1).inputType.contains("ORP")
                || mainConfigurationEntityList.get(1).inputType.contains("Temperature")
                || mainConfigurationEntityList.get(1).inputType.contains("Flow/Water Meter")
                || mainConfigurationEntityList.get(1).inputType.contains("Contacting Conductivity")
                || mainConfigurationEntityList.get(1).inputType.contains("Toroidal Conductivity")
                || mainConfigurationEntityList.get(1).inputType.contains("Analog Input")
                || mainConfigurationEntityList.get(1).inputType.contains("Tank Level")
                || mainConfigurationEntityList.get(1).inputType.contains("Digital Input")
                || mainConfigurationEntityList.get(1).inputType.contains("Modbus Sensor")) {
            lowKeyTwo.setText("Low Alarm");
            highKeyTwo.setText("High Alarm");
            currentKeyTwo.setText("Current Value");
            uniTwo.setVisibility(View.VISIBLE);
            typeTwo.setVisibility(View.VISIBLE);
            hardwareNoTwo.setText(mainConfigurationEntityList.get(1).hardware_no + "");

            if (mainConfigurationEntityList.get(1).inputType.contains("Digital Input")) {
               /* lowKeyTwo.setText("Open\nMessage");
                highKeyTwo.setText("Close\nMessage");*/

                lowKeyOne.setVisibility(View.INVISIBLE);
                highKeyOne.setVisibility(View.INVISIBLE);
                lowAlarmOne.setVisibility(View.INVISIBLE);
                highAlarmOne.setVisibility(View.INVISIBLE);
            }
            if (inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(1).hardware_no) != null) {
                sensorLabelTwo.setText(inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(1).hardware_no));
            }
            if (inputConfigurationDao.getUnit(mainConfigurationEntityList.get(1).hardware_no) != null) {
                if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(1).hardware_no)).equals("N/A")) {
                    uniTwo.setText("");
                } else {
                    uniTwo.setText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(1).hardware_no)));
                }
            }

            if (inputConfigurationDao.getType((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                if (inputConfigurationDao.getType((mainConfigurationEntityList.get(1).hardware_no)).equals("N/A")) {
                    typeTwo.setText("");
                } else {
                    typeTwo.setText(inputConfigurationDao.getType((mainConfigurationEntityList.get(1).hardware_no)));
                }
            }
            if (mainConfigurationEntityList.get(1).inputType != null) {
                seqTwo.setText(mainConfigurationEntityList.get(1).inputType);
            }


            if (inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                lowAlarmTwo.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(1).hardware_no)));
            }

            if (inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(1).hardware_no)) != null) {
                highAlarmTwo.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(1).hardware_no)));
            }
            setDigitalInput(currentValueTwo, 1);
        }
        if (mainConfigurationEntityList.get(2).inputType.contains("pH")
                || mainConfigurationEntityList.get(2).inputType.contains("ORP")
                || mainConfigurationEntityList.get(2).inputType.contains("Temperature")
                || mainConfigurationEntityList.get(2).inputType.contains("Flow/Water Meter")
                || mainConfigurationEntityList.get(2).inputType.contains("Contacting Conductivity")
                || mainConfigurationEntityList.get(2).inputType.contains("Toroidal Conductivity")
                || mainConfigurationEntityList.get(2).inputType.contains("Analog Input")
                || mainConfigurationEntityList.get(2).inputType.contains("Tank Level")
                || mainConfigurationEntityList.get(2).inputType.contains("Digital Input")
                || mainConfigurationEntityList.get(2).inputType.contains("Modbus Sensor")) {
            lowKeyThree.setText("Low Alarm");
            highKeyThree.setText("High Alarm");
            currentKeyThree.setText("Current Value");
            uniThree.setVisibility(View.VISIBLE);
            typeThree.setVisibility(View.VISIBLE);
            hardwareNoThree.setText(mainConfigurationEntityList.get(2).hardware_no + "");
            if (mainConfigurationEntityList.get(2).inputType.contains("Digital Input")) {
               /* lowKeyThree.setText("Open\nMessage");
                highKeyThree.setText("Close\nMessage");*/

                lowKeyOne.setVisibility(View.INVISIBLE);
                highKeyOne.setVisibility(View.INVISIBLE);
                lowAlarmOne.setVisibility(View.INVISIBLE);
                highAlarmOne.setVisibility(View.INVISIBLE);
            }
            if (inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(2).hardware_no) != null) {
                sensorLabelThree.setText(inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(2).hardware_no));
            }

            if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(2).hardware_no)).equals("N/A")) {
                    uniThree.setText("");
                } else {
                    uniThree.setText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(2).hardware_no)));
                }
            }

            if (inputConfigurationDao.getType((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                if (inputConfigurationDao.getType((mainConfigurationEntityList.get(2).hardware_no)).equals("N/A")) {
                    typeThree.setText("");
                } else {
                    typeThree.setText(inputConfigurationDao.getType((mainConfigurationEntityList.get(2).hardware_no)));
                }
            }
            if (mainConfigurationEntityList.get(2).inputType != null) {
                seqThree.setText(mainConfigurationEntityList.get(2).inputType);
            }
            hardwareNoThree.setText(mainConfigurationEntityList.get(2).hardware_no + "");
            if (inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                lowAlarmThree.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(2).hardware_no)));
            }

            if (inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(2).hardware_no)) != null) {
                highAlarmThree.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(2).hardware_no)));
            }

          /*  if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(2).hardware_no) != null) {
                currentValueThree.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(2).hardware_no));
            }*/
            setDigitalInput(currentValueThree, 2);
        }


    }


    void setDigitalInput(TextView txtView, int pos) {
        if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(pos).hardware_no) != null) {
            txtView.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(pos).hardware_no));

            if (mainConfigurationEntityList.get(pos).inputType.contains("Digital Input")) {
                txtView.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(pos).hardware_no)
                        .equals("OPEN") ? inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(pos).hardware_no)) :
                        inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(pos).hardware_no)));
            }
        }
    }


    @Override
    public int getItemCount() {
        if (layout == 3 || layout == 4) {
            return 1;
        }
        return mainConfigurationEntityList.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        View layoutOne, layoutTwo, layoutThree;
        TextView labeLOne, sensorLabelOne, hardwareNoOne, currentValueOne, lowAlarmOne, highAlarmOne, lowKeyOne, highKeyOne, currentKeyOne, unitOne, typeOne;
        TextView labeLTwo, sensorLabelTwo, hardwareNoTwo, currentValueTwo, lowAlarmTwo, highAlarmTwo, lowKeyTwo, highKeyTwo, currentKeyTwo, uniTwo, typeTwo;
        TextView labeLThree, sensorLabelThree, hardwareNoThree, currentValueThree, lowAlarmThree, highAlarmThree, lowKeyThree, highKeyThree, uniThree, typeThree, currentKeyThree;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layout);
            layoutOne = itemView.findViewById(R.id.layoutOne);
            layoutTwo = itemView.findViewById(R.id.layout_two);
            layoutThree = itemView.findViewById(R.id.layout_three);

            labeLOne = itemView.findViewById(R.id.layout_1_seq_number);
            sensorLabelOne = itemView.findViewById(R.id.layout_1_label);
            hardwareNoOne = itemView.findViewById(R.id.layout_1_hardware_no);
            currentValueOne = itemView.findViewById(R.id.layout_one_current_value);
            highAlarmOne = itemView.findViewById(R.id.layout_1_high_alarm_value);
            lowAlarmOne = itemView.findViewById(R.id.layout_1_low_alarm_value);
            lowKeyOne = itemView.findViewById(R.id.txt_layout_1_alarm_low);
            highKeyOne = itemView.findViewById(R.id.txt_layout_1_alarm_high);
            currentKeyOne = itemView.findViewById(R.id.txt_layout_1_current);
            unitOne = itemView.findViewById(R.id.layout_unit_1);
            typeOne = itemView.findViewById(R.id.layout_type_1);

            labeLTwo = itemView.findViewById(R.id.layout_2_seq_number);
            sensorLabelTwo = itemView.findViewById(R.id.layout_2_label);
            hardwareNoTwo = itemView.findViewById(R.id.layout_2_hardware_no);
            currentValueTwo = itemView.findViewById(R.id.layout_2_current_value);
            highAlarmTwo = itemView.findViewById(R.id.layout_2_high_alarm_value);
            lowAlarmTwo = itemView.findViewById(R.id.layout_2_low_alarm_value);
            lowKeyTwo = itemView.findViewById(R.id.txt_layout_2_alarm_low);
            highKeyTwo = itemView.findViewById(R.id.txt_layout_2_alarm_high);
            currentKeyTwo = itemView.findViewById(R.id.txt_layout_2_current);
            typeTwo = itemView.findViewById(R.id.layout_type_2);
            uniTwo = itemView.findViewById(R.id.layout_unit_2);

            labeLThree = itemView.findViewById(R.id.layout_3_seq_number);
            sensorLabelThree = itemView.findViewById(R.id.layout_3_label);
            hardwareNoThree = itemView.findViewById(R.id.layout_3_hardware_no);
            currentValueThree = itemView.findViewById(R.id.layout_three_current_value);
            highAlarmThree = itemView.findViewById(R.id.layout_3_high_alarm_value);
            lowAlarmThree = itemView.findViewById(R.id.layout_3_low_alarm_value);
            lowKeyThree = itemView.findViewById(R.id.txt_layout_3_alarm_low);
            highKeyThree = itemView.findViewById(R.id.txt_layout_3_alarm_high);
            currentKeyThree = itemView.findViewById(R.id.txt_layout_3_current);
            typeThree = itemView.findViewById(R.id.layout_type_3);
            uniThree = itemView.findViewById(R.id.layout_unit_3);

            switch (layout) {
                case 1:
                case 2:
                case 5:
                case 6:
                    constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(mainConfigurationEntityList.get(getAdapterPosition()));
                        }
                    });
                    break;

                case 3:
                case 4:
                    layoutOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(mainConfigurationEntityList.get(0));

                        }
                    });
                    layoutTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(mainConfigurationEntityList.get(1));

                        }
                    });
                    layoutThree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvOnClick.onClick(mainConfigurationEntityList.get(2));
                        }
                    });
                    break;
            }

        }
    }
}
