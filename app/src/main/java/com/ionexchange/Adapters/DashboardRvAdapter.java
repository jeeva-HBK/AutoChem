package com.ionexchange.Adapters;

import static com.ionexchange.Others.ApplicationClass.inputDAO;
import static com.ionexchange.Others.ApplicationClass.keepaliveDAO;
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
            if (mainConfigurationEntityList.get(position).inputType.toUpperCase().contains("OUTPUT")) {
                String outputmode = "", outputLabel="", highAlarm = "";

                currentMode.setText("Output Status");
                lowKey.setText("Mode");
                highKey.setText(mainConfigurationEntityList.get(position).hardware_no < 15 ? "Mode" : "Linked to");

                unitOne.setVisibility(View.INVISIBLE);
                typeOne.setVisibility(View.INVISIBLE);
                seq.setText(mainConfigurationEntityList.get(position).inputType);
                if(outputConfigurationDao.getOutputMode(mainConfigurationEntityList.get(position).hardware_no)!= null) {
                    outputmode = outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(position).hardware_no));
                    lowAlarmOne.setText(outputmode);
                }
                if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    outputLabel = outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no);
                    label.setText(outputLabel);
                }
                hardwareNoOne.setText(mainConfigurationEntityList.get(position).hardware_no + "");

                if (outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no)) != null) {
                    highAlarm = outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no));
                    highAlarmOne.setText(highAlarm);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (outputLabel != null && !outputLabel.isEmpty()) {
                        label.setTooltipText(outputLabel);
                    }
                    if (outputmode != null && !outputmode.isEmpty()) {
                        lowAlarmOne.setTooltipText(outputmode.contains("$") ? outputmode.split("\\$")[0] : outputmode);
                    }

                    if (highAlarm != null && !highAlarm.isEmpty())  {
                        highAlarmOne.setTooltipText(highAlarm);
                    }
                }
                if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no) != null) {
                    if (!outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("N/A")) {
                        if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("7")) {
                            currentMode.setText("Current Value");
                            currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                        } else {
                            if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("6") ||
                                    outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("5")){
                                currentMode.setText("Status : "+outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                                currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                            }else if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("8") ||
                                    outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("9")){
                                currentMode.setText("Status : "+outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                                lowAlarmOne.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                            }else {
                                currentMode.setText("Status : "+ outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no))]);
                                currentValue.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no))]);
                            }

                        }

                    }

                }
                if(mainConfigurationEntityList.get(position).hardware_no < 15){
                    switch (highAlarm) {
                        case "Continuous":
                            lowKey.setText("Dose Period");
                            lowAlarmOne.setText(outputmode.contains("$") ? outputmode.split("\\$")[0] : outputmode);
                            currentValue.setText(outputmode.contains("$") ? outputmode.split("\\$")[1] : outputmode);
                            break;
                        case "Bleed/Blow Down":
                        case "Water Meter/Biocide":
                            lowKey.setText("Accumulated Vol");
                            currentValue.setText(outputmode);
                            break;
                        case "On/Off":
                        case "PID":
                            lowKey.setText("Set Point");
                            try {
                                lowAlarmOne.setText(outputmode.contains("$") ? outputmode.split("\\$")[0] : outputmode);
                                currentValue.setText(keepaliveDAO.getCurrentValue(Integer.parseInt(outputmode.split("\\$")[1])));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        default:
                            lowKey.setText("Mode");
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
                lowKey.setVisibility(View.VISIBLE);
                highKey.setVisibility(View.VISIBLE);
                lowAlarmOne.setVisibility(View.VISIBLE);
                highAlarmOne.setVisibility(View.VISIBLE);
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


        if (mainConfigurationEntityList.get(0).inputType.toUpperCase().contains("OUTPUT")) {
            setthreefourthLayoutOutput(lowKeyOne,lowAlarmOne,
                    sensorLabelOne,highAlarmOne,0,currentValueOne,seqOne,
                    hardwareNoOne,highKeyOne,currentKeyOne,unitOne,typeOne);
        }
        if (mainConfigurationEntityList.get(1).inputType.toUpperCase().contains("OUTPUT")) {
            setthreefourthLayoutOutput(lowKeyTwo,lowAlarmTwo,
                    sensorLabelTwo,highAlarmTwo,1,currentValueTwo,seqTwo,
                    hardwareNoTwo,highKeyTwo,currentKeyTwo,uniTwo,typeTwo);
        }
        if (mainConfigurationEntityList.get(2).inputType.toUpperCase().contains("OUTPUT")) {
            setthreefourthLayoutOutput(lowKeyThree,lowAlarmThree,
                    sensorLabelThree,highAlarmThree,2,currentValueThree,
                    seqThree,hardwareNoThree,highKeyThree,currentKeyThree,uniThree,typeThree);
        }


        if (mainConfigurationEntityList.get(0).inputType.contains("virtual")) {
            setthreefourthLayoutVirtual(0,lowKeyOne,highKeyOne,unitOne,
                    typeOne,hardwareNoOne,currentKeyOne,seqOne,
                    sensorLabelOne,lowAlarmOne,highAlarmOne,currentValueOne);
        }
        if (mainConfigurationEntityList.get(1).inputType.contains("virtual")) {
            setthreefourthLayoutVirtual(1,lowKeyTwo,highKeyTwo,uniTwo,
                    typeTwo,hardwareNoTwo,currentKeyTwo,seqTwo,
                    sensorLabelTwo,lowAlarmTwo,highAlarmTwo,currentValueTwo);
        }
        if (mainConfigurationEntityList.get(2).inputType.contains("virtual")) {
            setthreefourthLayoutVirtual(2,lowKeyThree,highKeyThree,uniThree,
                    typeThree,hardwareNoThree,currentKeyThree,seqThree,
                    sensorLabelThree,lowAlarmThree,highAlarmThree,currentValueThree);
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
            setthreefourthLayoutSensor(0,lowKeyOne,highKeyOne,currentKeyOne,unitOne,typeOne,hardwareNoOne,
                    lowAlarmOne,highAlarmOne,sensorLabelOne,seqOne,currentValueOne);

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
            setthreefourthLayoutSensor(1,lowKeyTwo,highKeyTwo,currentKeyTwo,uniTwo,typeTwo,hardwareNoTwo,
                    lowAlarmTwo,highAlarmTwo,sensorLabelTwo,seqTwo,currentValueTwo);
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
            setthreefourthLayoutSensor(2,lowKeyThree,highKeyThree,currentKeyThree,uniThree,typeThree,hardwareNoThree,
                    lowAlarmThree,highAlarmThree,sensorLabelThree,seqThree,currentValueThree);
        }

    }

    private void setthreefourthLayoutSensor(int position, TextView lowKey, TextView highKey,
                                            TextView currentKey, TextView unit, TextView type,
                                            TextView hardwareNo, TextView lowAlarm, TextView highAlarm,
                                            TextView sensorLabel, TextView seq, TextView currentValue) {
        lowKey.setText("Low Alarm");
        highKey.setText("High Alarm");
        currentKey.setText("Current Value");
        unit.setVisibility(View.VISIBLE);
        type.setVisibility(View.VISIBLE);
        hardwareNo.setText(mainConfigurationEntityList.get(position).hardware_no + "");
        if (mainConfigurationEntityList.get(position).inputType.contains("Digital Input")) {
                    /*lowKey.setText("Open\nMessage");
                    highKey.setText("Close\nMessage");
                    lowKey.setGravity(Gravity.CENTER);
                    highKey.setGravity(Gravity.CENTER);*/
            lowKey.setVisibility(View.INVISIBLE);
            highKey.setVisibility(View.INVISIBLE);
            lowAlarm.setVisibility(View.INVISIBLE);
            highAlarm.setVisibility(View.INVISIBLE);
        }
        if (inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
            sensorLabel.setText(inputConfigurationDao.getInputLabel(mainConfigurationEntityList.get(position).hardware_no));
        }
        if (mainConfigurationEntityList.get(position).inputType != null) {
            seq.setText(mainConfigurationEntityList.get(position).inputType);
        }
        if (inputConfigurationDao.getUnit(mainConfigurationEntityList.get(position).hardware_no) != null) {
            if (inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)).equals("N/A")) {
                unit.setText("");
            } else {
                unit.setText(inputConfigurationDao.getUnit((mainConfigurationEntityList.get(position).hardware_no)));
            }
        }

        if (inputConfigurationDao.getType(mainConfigurationEntityList.get(position).hardware_no) != null) {
            if (inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)).equals("N/A")) {
                type.setText("");
            } else {
                type.setText(inputConfigurationDao.getType((mainConfigurationEntityList.get(position).hardware_no)));
            }
        }
        if (inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
            lowAlarm.setText(inputConfigurationDao.getLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
        }

        if (inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
            highAlarm.setText(inputConfigurationDao.getHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
        }

           /* if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no) != null) {
                currentValueOne.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(0).hardware_no));
            }*/
        setDigitalInput(currentValue, position);
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

    private void setthreefourthLayoutVirtual(int position,TextView lowKey,TextView highKey,TextView unit,
                                             TextView type,TextView hardwareNo, TextView currentKey,TextView seq,
                                             TextView sensorLabel,TextView lowAlarm,TextView highAlarm,TextView currentValue){
        lowKey.setText("Low Alarm");
        highKey.setText("High Alarm ");
        unit.setVisibility(View.VISIBLE);
        type.setVisibility(View.VISIBLE);
        hardwareNo.setText(String.valueOf(mainConfigurationEntityList.get(position).getHardware_no()));
        currentKey.setText("Current Value");
        if (mainConfigurationEntityList.get(position).inputType != null) {
            seq.setText(mainConfigurationEntityList.get(position).inputType);
        }
        if (virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
            sensorLabel.setText(virtualConfigurationDao.getVirtualLabel(mainConfigurationEntityList.get(position).hardware_no));
        }

        if (virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
            lowAlarm.setText(virtualConfigurationDao.getVirtualLowAlarm((mainConfigurationEntityList.get(position).hardware_no)));
        }
        if (virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(position).hardware_no)) != null) {
            highAlarm.setText(virtualConfigurationDao.getVirtualHighAlarm((mainConfigurationEntityList.get(position).hardware_no)));
        }
        if (keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no) != null) {
            currentValue.setText(keepAliveCurrentValueDao.getCurrentValue(mainConfigurationEntityList.get(position).hardware_no));
        }
    }
    private void setthreefourthLayoutOutput(TextView lowKey,TextView lowAlarm,TextView sensorLabel, TextView highAlarm,
                                            int position, TextView currentValue,TextView seqType,
                                            TextView hardwareNo,TextView highKey,TextView currentKey,
                                            TextView unit,TextView type){
        String highalarmValue = "", outputLabel="", outputMode = "";
        lowKey.setText("Mode");
        highKey.setText(mainConfigurationEntityList.get(position).hardware_no < 15 ? "Mode" : "Linked to");
        currentKey.setText("Output Status");
        unit.setVisibility(View.INVISIBLE);
        type.setVisibility(View.INVISIBLE);
        if (mainConfigurationEntityList.get(position).inputType != null) {
            seqType.setText(mainConfigurationEntityList.get(position).inputType);
        }
        if (outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no) != null) {
            outputLabel = outputConfigurationDao.getOutputLabel(mainConfigurationEntityList.get(position).hardware_no);
            sensorLabel.setText(outputLabel);
        }
        hardwareNo.setText(mainConfigurationEntityList.get(position).hardware_no + "");
        if (outputConfigurationDao.getOutputMode((mainConfigurationEntityList.get(position).hardware_no)) != null) {
            outputMode = outputConfigurationDao.getOutputMode(mainConfigurationEntityList.get(position).hardware_no);
            lowAlarm.setText(outputMode);
        }
        if (outputConfigurationDao.getOutputStatus((mainConfigurationEntityList.get(position).hardware_no)) != null) {
            highalarmValue = outputConfigurationDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no);
            highAlarm.setText(highalarmValue);
        }
        if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no) != null) {
            if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("7")) {
                currentKey.setText("Current Value");
                currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
            } else {
                if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("6") ||
                        outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("5")) {
                    currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                    currentKey.setText("Output Status : " + outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                } else if (outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("8") ||
                        outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no).equals("9")) {
                    currentValue.setText(outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                    currentKey.setText("Output Status : " + outputKeepAliveDao.getOutputRelayStatus(mainConfigurationEntityList.get(position).hardware_no));
                } else {
                    currentValue.setText(outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no))]);
                    currentKey.setText("Output Status : " + outputControlShortForm[Integer.parseInt(outputKeepAliveDao.getOutputStatus(mainConfigurationEntityList.get(position).hardware_no))]);
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (outputLabel != null && !outputLabel.isEmpty()) {
                sensorLabel.setTooltipText(outputLabel);
            }
            if (outputMode != null && !outputMode.isEmpty()) {
                lowAlarm.setTooltipText(outputMode.contains("$") ? outputMode.split("\\$")[0] : outputMode);
            }

            if (highalarmValue != null && !highalarmValue.isEmpty())  {
                highAlarm.setTooltipText(highalarmValue);
            }
        }
        if(mainConfigurationEntityList.get(position).hardware_no < 15){
            switch (highalarmValue) {
                case "Continuous":
                    lowKey.setText("Dose Period");
                    lowAlarm.setText(outputMode.contains("$") ? outputMode.split("\\$")[0] : outputMode);
                    currentValue.setText(outputMode.contains("$") ? outputMode.split("\\$")[1] : outputMode);
                    break;
                case "Bleed/Blow Down":
                case "Water Meter/Biocide":
                    lowKey.setText("Accumulated Vol");
                    currentValue.setText(outputMode);
                    break;
                case "On/Off":
                case "PID":
                    lowKey.setText("Set Point");
                    if(outputMode.contains("$")){
                        lowAlarm.setText(outputMode.split("\\$")[0]);
                        currentValue.setText(keepaliveDAO.getCurrentValue(Integer.parseInt(outputMode.split("\\$")[1])));
                    }
                    break;
                default:
                    lowKey.setText("Mode");
                    break;
            }
        }
    }
}
