package com.ionexchange.Fragments.Configuration.TimerConfig;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Dao.TimerConfigurationDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTimerstatusConfigBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ionexchange.Others.ApplicationClass.accessoryTimerMode;
import static com.ionexchange.Others.ApplicationClass.accessoryType;
import static com.ionexchange.Others.ApplicationClass.bleedRelay;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getPosition;
import static com.ionexchange.Others.ApplicationClass.timerFlowSensor;
import static com.ionexchange.Others.ApplicationClass.timerOutputMode;
import static com.ionexchange.Others.ApplicationClass.toStringValue;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_TIMER_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_WEEKLY_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentTimerStatus_Config extends Fragment implements DataReceiveCallback, View.OnClickListener {
    FragmentTimerstatusConfigBinding mBinding;
    String timerNo;
    BaseActivity mActivity;
    ApplicationClass mAppClass;

    String week1, week2, week3, week4;

    String enabledWeek1;

    int loopWeeklyPacket;
    String week;
    String[] timerOne, timerTwo, timerThree, timerFour, accessoryTimer, outputNames;

    EditText startHour;
    EditText startMin;
    EditText startSec;
    AutoCompleteTextView mode;
    AutoCompleteTextView type;
    AutoCompleteTextView outputName;
    CheckBox accessory;


    EditText startHourDay;
    EditText startMinDay;
    EditText startSecDay;
    EditText endHourDay;
    EditText endMinDay;
    EditText endSecDay;
    WaterTreatmentDb dB;
    OutputConfigurationDao dao;

    public FragmentTimerStatus_Config(String week1, String week2, String week3, String week4, String timerNo) {
        this.timerNo = timerNo;
        this.week1 = week1;
        this.week2 = week2;
        this.week3 = week3;
        this.week4 = week4;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timerstatus_config, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = dB.outputConfigurationDao();
        week = "Week-1";
        initAdapter();
        enableWeek();
        mBinding.saveFabCondIS.setOnClickListener(this);
        mBinding.saveLayoutCondIS.setOnClickListener(this);
        mBinding.weekCheckbox1.setOnClickListener(this);
        mBinding.weekCheckbox2.setOnClickListener(this);
        mBinding.weekCheckbox3.setOnClickListener(this);
        mBinding.weekCheckbox4.setOnClickListener(this);

        mBinding.checkBoxMonday.setOnClickListener(this);
        mBinding.checkBoxTuesday.setOnClickListener(this);
        mBinding.checkBoxWednesday.setOnClickListener(this);
        mBinding.checkBoxThursday.setOnClickListener(this);
        mBinding.checkBoxFriday.setOnClickListener(this);
        mBinding.checkBoxSaturday.setOnClickListener(this);
        mBinding.checkBoxSunday.setOnClickListener(this);

        mBinding.AccessoryCheckbox1.setOnClickListener(this);
        mBinding.AccessoryCheckbox2.setOnClickListener(this);
        mBinding.AccessoryCheckbox3.setOnClickListener(this);
        mBinding.AccessoryCheckbox4.setOnClickListener(this);

            mBinding.switchBtnWeek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week1_enable))) {
                            mBinding.weekCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_checked));
                            if (timerOne != null)
                                timerOne[5] = "1";
                        }
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week2_enable))) {
                            mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));
                            if (timerTwo != null)
                                timerTwo[5] = "1";
                        }
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week3_enable))) {
                            mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));
                            if (timerThree != null)
                                timerThree[5] = "1";
                        }
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week4_enable))) {
                            mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                            if (timerFour != null)
                                timerFour[5] = "1";
                        }

                    } else {
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week1_enable))) {
                            mBinding.weekCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
                            if (timerOne != null)
                                timerOne[5] = "0";
                        }
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week2_enable))) {
                            mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                            if (timerTwo != null)
                                timerTwo[5] = "0";
                        }
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week3_enable))) {
                            mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                            if (timerThree != null)
                                timerThree[5] = "0";
                        }
                        if (mBinding.switchBtnWeek.getText().toString().equals(getString(R.string.week4_enable))) {
                            mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
                            if (timerFour != null)
                                timerFour[5] = "0";
                        }
                    }
                }
            });
    }

    void enableWeek() {
        if (mBinding.switchBtnWeek.isChecked()) {
            enabledWeek1 = "1";
        } else {
            enabledWeek1 = "0";
        }
    }

    private void initAdapter() {
        List<OutputConfigurationEntity> outputNameList = dao.getOutputHardWareNoConfigurationEntityList(1, 14);
        outputNames = new String[14];
        if (!outputNameList.isEmpty()) {
            for (int i = 0; i < outputNameList.size(); i++) {
                outputNames[i] = "Output- " + outputNameList.get(i).getOutputHardwareNo() + " (" + outputNameList.get(i).getOutputLabel() + ")";
            }
        }
        if (outputNames.length == 0) {
            outputNames = bleedRelay;
        }
        mBinding.txtOutputNameValueAct.setAdapter(getAdapter(outputNames));
        mBinding.txtModeValueAct.setAdapter(getAdapter(timerOutputMode));
        mBinding.txtFlowSensorValueAct.setAdapter(getAdapter(timerFlowSensor));
        mBinding.txtModeValueAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                flowSensorVisibility(position);
            }
        });
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_TIMER_CONFIG + SPILT_CHAR + timerNo);
    }

    void dayDialog(int day, String week, String titleName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_timer_day, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        CheckBox enableSchedule = dialogView.findViewById(R.id.checkbox_enable_time);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnApplyToAll = dialogView.findViewById(R.id.btn_apply_to_all);
        TextView title = dialogView.findViewById(R.id.txt_date);
        title.setText(titleName);
        startHourDay = dialogView.findViewById(R.id.number_picker_hour);
        startMinDay = dialogView.findViewById(R.id.number_min_hour);
        startSecDay = dialogView.findViewById(R.id.number_sec_hour);
        endHourDay = dialogView.findViewById(R.id.number_dur_picker_hour);
        endMinDay = dialogView.findViewById(R.id.number_dur_min_hour);
        endSecDay = dialogView.findViewById(R.id.number_dur_sec_hour);

        enableDayScreen(enableSchedule.isChecked(), startHourDay, startMinDay, startSecDay,
                endHourDay, endMinDay, endSecDay);

        enableSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableDayScreen(isChecked, startHourDay, startMinDay, startSecDay,
                        endHourDay, endMinDay, endSecDay);
            }
        });

        alertDialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        alertDialog.getWindow().setLayout(width, height);

        //DataReceive status
        //Week-1
        if (week.equals("Week-1")) {
            if (timerOne != null) {
                switch (day) {
                    case 1:
                        setWeekDatas(timerOne[6], timerOne[7], timerOne[8], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 2:
                        setWeekDatas(timerOne[9], timerOne[10], timerOne[11], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 3:
                        setWeekDatas(timerOne[12], timerOne[13], timerOne[14], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 4:
                        setWeekDatas(timerOne[15], timerOne[16], timerOne[17], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 5:
                        setWeekDatas(timerOne[18], timerOne[19], timerOne[20], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 6:
                        setWeekDatas(timerOne[21], timerOne[22], timerOne[23], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 7:
                        setWeekDatas(timerOne[24], timerOne[25], timerOne[26], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                }
            }
        }
        //Week-2
        if (week.equals("Week-2")) {
            if (timerTwo != null) {
                switch (day) {
                    case 1:
                        setWeekDatas(timerTwo[6], timerTwo[7], timerTwo[8], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 2:
                        setWeekDatas(timerTwo[9], timerTwo[10], timerTwo[11], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 3:
                        setWeekDatas(timerTwo[12], timerTwo[13], timerTwo[14], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 4:
                        setWeekDatas(timerTwo[15], timerTwo[16], timerTwo[17], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 5:
                        setWeekDatas(timerTwo[18], timerTwo[19], timerTwo[20], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 6:
                        setWeekDatas(timerTwo[21], timerTwo[22], timerTwo[23], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 7:
                        setWeekDatas(timerTwo[24], timerTwo[25], timerTwo[26], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                }
            }
        }
        //Week-3
        if (week.equals("Week-3")) {
            if (timerThree != null) {
                switch (day) {
                    case 1:
                        setWeekDatas(timerThree[6], timerThree[7], timerThree[8], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 2:
                        setWeekDatas(timerThree[9], timerThree[10], timerThree[11], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 3:
                        setWeekDatas(timerThree[12], timerThree[13], timerThree[14], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 4:
                        setWeekDatas(timerThree[15], timerThree[16], timerThree[17], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 5:
                        setWeekDatas(timerThree[18], timerThree[19], timerThree[20], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 6:
                        setWeekDatas(timerThree[21], timerThree[22], timerThree[23], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 7:
                        setWeekDatas(timerThree[24], timerThree[25], timerThree[26], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                }
            }
        }
        //Week-4
        if (week.equals("Week-4")) {
            if (timerFour != null) {
                switch (day) {
                    case 1:
                        setWeekDatas(timerFour[6], timerFour[7], timerFour[8], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 2:
                        setWeekDatas(timerFour[9], timerFour[10], timerFour[11], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 3:
                        setWeekDatas(timerFour[12], timerFour[13], timerFour[14], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 4:
                        setWeekDatas(timerFour[15], timerFour[16], timerFour[17], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 5:
                        setWeekDatas(timerFour[18], timerFour[19], timerFour[20], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 6:
                        setWeekDatas(timerFour[21], timerFour[22], timerFour[23], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                    case 7:
                        setWeekDatas(timerFour[24], timerFour[25], timerFour[26], enableSchedule, startHourDay,
                                startMinDay, startSecDay, endHourDay, endMinDay, endSecDay);
                        break;
                }
            }
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SendData
                if (weeklyScheduledValidation()) {
                    if (week.equals("Week-1") && timerOne != null) {
                        weekupdateData(enableSchedule, day, timerOne);
                    }
                    if (week.equals("Week-2") && timerTwo != null) {
                        weekupdateData(enableSchedule, day, timerTwo);
                    }
                    if (week.equals("Week-3") && timerThree != null) {
                        weekupdateData(enableSchedule, day, timerThree);
                    }
                    if (week.equals("Week-4") &&  timerFour != null) {
                        weekupdateData(enableSchedule, day, timerFour);
                    }
                    alertDialog.dismiss();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnApplyToAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ApplyToAll
                Log.e("week", week);
                if (weeklyScheduledValidation()) {
                    if (week.equals("Week-1") && timerOne != null) {
                        weekupdateData(enableSchedule, day, timerOne);
                        applyToAllTime(timerOne, timerOne, day);
                        handleResponse(timerOne, 1);
                        alertDialog.dismiss();
                    }
                    if (week.equals("Week-2") && timerTwo != null ) {
                        weekupdateData(enableSchedule, day, timerTwo);
                        applyToAllTime(timerTwo, timerTwo, day);
                        handleResponse(timerTwo, 1);
                        alertDialog.dismiss();
                    }
                    if (week.equals("Week-3") && timerThree != null) {
                        weekupdateData(enableSchedule, day, timerThree);
                        applyToAllTime(timerThree, timerThree, day);
                        handleResponse(timerThree, 1);
                        alertDialog.dismiss();
                    }
                    if (week.equals("Week-4") && timerFour != null) {
                        weekupdateData(enableSchedule, day, timerFour);
                        applyToAllTime(timerFour, timerFour, day);
                        handleResponse(timerFour, 1);
                        alertDialog.dismiss();
                    }
                }
            }
        });

    }

    private void weekupdateData(CheckBox enableSchedule, int day, String[] timerNo) {
        if (day == 1) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxMonday,
                    timerNo, 6, 7, 8,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
        if (day == 2) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxTuesday,
                    timerNo, 9, 10, 11,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
        if (day == 3) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxWednesday,
                    timerNo, 12, 13, 14,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
        if (day == 4) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxThursday,
                    timerNo, 15, 16, 17,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
        if (day == 5) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxFriday,
                    timerNo, 18, 19, 20,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
        if (day == 6) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxSaturday,
                    timerNo, 21, 22, 23,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
        if (day == 7) {
            updateWeekDay(enableSchedule.isChecked(), mBinding.checkBoxSunday,
                    timerNo, 24, 25, 26,
                    startHourDay, startMinDay, startSecDay,
                    endHourDay, endMinDay, endSecDay);
        }
    }

    private void updateWeekDay(boolean enableDay, View checkBoxDate, String[] timerNo,
                               int day, int startTime, int durationTime,
                               EditText startHourDay, EditText startMinDay, EditText startSecDay,
                               EditText endHourDay, EditText endMinDay, EditText endSecDay) {
        if (enableDay) {
            timerNo[day] = "1";
            checkBoxDate.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
        } else {
            timerNo[day] = "0";
            checkBoxDate.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
        }
        timerNo[startTime] = formDigits(2, startHourDay.getText().toString()) + formDigits(2, startMinDay.getText().toString()) + formDigits(2, startSecDay.getText().toString());
        timerNo[durationTime] = formDigits(2, endHourDay.getText().toString()) + formDigits(2, endMinDay.getText().toString()) + formDigits(2, endSecDay.getText().toString());
        Log.e("update", timerNo[day] + " " + timerNo[startTime] + " " + timerNo[durationTime]);
    }

    private void setWeekDatas(String enableDay, String startTime, String durationTime,
                              CheckBox enableSchedule,
                              EditText startHourDay, EditText startMinDay, EditText startSecDay,
                              EditText endHourDay, EditText endMinDay, EditText endSecDay) {
        if (enableDay.equals("0")) {
            enableSchedule.setChecked(false);
        } else if (enableDay.equals("1")) {
            enableSchedule.setChecked(true);
        }
        if (startTime != null && startTime.length() == 6) {
            startHourDay.setText(startTime.substring(0, 2));
            startMinDay.setText(startTime.substring(2, 4));
            startSecDay.setText(startTime.substring(4, 6));
        }
        if (durationTime != null && durationTime.length() == 6) {
            endHourDay.setText(durationTime.substring(0, 2));
            endMinDay.setText(durationTime.substring(2, 4));
            endSecDay.setText(durationTime.substring(4, 6));
        }
    }

    private void enableDayScreen(boolean checked, EditText startHourDay, EditText startMinDay, EditText startSecDay, EditText endHourDay, EditText endMinDay, EditText endSecDay) {
        if (checked) {
            startHourDay.setEnabled(true);
            startMinDay.setEnabled(true);
            startSecDay.setEnabled(true);
            endHourDay.setEnabled(true);
            endMinDay.setEnabled(true);
            endSecDay.setEnabled(true);
        } else {
            startHourDay.setEnabled(false);
            startMinDay.setEnabled(false);
            startSecDay.setEnabled(false);
            endHourDay.setEnabled(false);
            endMinDay.setEnabled(false);
            endSecDay.setEnabled(false);
        }

    }

    void accessoryTimer(int timer) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_accessory_picker, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        //init
        TextView txt_header = dialogView.findViewById(R.id.txt_date);
        TextInputLayout outputNameTil = dialogView.findViewById(R.id.output_til);
        TextInputLayout modeTil = dialogView.findViewById(R.id.mode_til);
        TextInputLayout typeTil = dialogView.findViewById(R.id.outputType);

        startHour = dialogView.findViewById(R.id.number_picker_hour);
        startMin = dialogView.findViewById(R.id.number_min_hour);
        startSec = dialogView.findViewById(R.id.number_sec_hour);
        Button okBtn = dialogView.findViewById(R.id.btn_ok);
        Button cancelBtn = dialogView.findViewById(R.id.btn_cancel);
        accessory = dialogView.findViewById(R.id.checkbox_enable_time);
        mode = dialogView.findViewById(R.id.mode_act);
        type = dialogView.findViewById(R.id.outputType_act);
        outputName = dialogView.findViewById(R.id.output_act);
        mode.setAdapter(getAdapter(accessoryTimerMode));
        type.setAdapter(getAdapter(accessoryType));
        outputName.setAdapter(getAdapter(outputNames));

        txt_header.setText(getString(R.string.accessories_timer_header) + " " + timer);

        setAccessoryTimerEnabled(accessory.isChecked(), startHour, startMin, startSec, outputNameTil, modeTil, typeTil);

        accessory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAccessoryTimerEnabled(isChecked, startHour, startMin, startSec, outputNameTil, modeTil, typeTil);
            }
        });

        //OnDataReceive Timer status
        if (timer == 1 && accessoryTimer != null && accessoryTimer.length > 8) {
            setTimerDatas(accessoryTimer[9], accessoryTimer[10], accessoryTimer[11], accessoryTimer[12], accessoryTimer[13],
                    accessory, mode, startHour, startMin, startSec, outputName, type);
        }
        if (timer == 2 && accessoryTimer != null && accessoryTimer.length > 14) {
            setTimerDatas(accessoryTimer[15], accessoryTimer[16], accessoryTimer[17], accessoryTimer[18], accessoryTimer[19],
                    accessory, mode, startHour, startMin, startSec, outputName, type);
        }
        if (timer == 3 && accessoryTimer != null && accessoryTimer.length > 20) {
            setTimerDatas(accessoryTimer[21], accessoryTimer[22], accessoryTimer[23], accessoryTimer[24], accessoryTimer[25],
                    accessory, mode, startHour, startMin, startSec, outputName, type);
        }
        if (timer == 4 && accessoryTimer != null && accessoryTimer.length > 26) {
            setTimerDatas(accessoryTimer[27], accessoryTimer[28], accessoryTimer[29], accessoryTimer[30], accessoryTimer[31],
                    accessory, mode, startHour, startMin, startSec, outputName, type);
        }

        //dialog size
        alertDialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        alertDialog.getWindow().setLayout(width, height);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SendData
                if (timer == 1) {
                    if (accessoryTimeValidation()  && accessoryTimer != null && accessoryTimer.length > 8) {
                        if (accessory.isChecked()) {
                            accessoryTimer[9] = "1";
                            mBinding.AccessoryCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_checked));
                        } else {
                            accessoryTimer[9] = "0";
                            mBinding.AccessoryCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
                        }
                        accessoryTimer[10] = getPosition(1, toStringValue(mode), accessoryTimerMode);
                        accessoryTimer[11] = formDigits(2, startHour.getText().toString()) + formDigits(2, startMin.getText().toString()) + formDigits(2, startSec.getText().toString());
                        accessoryTimer[12] = getPosition(2, toStringValue(outputName), outputNames);
                        accessoryTimer[13] = getPosition(1, toStringValue(type), accessoryType);
                        alertDialog.dismiss();
                    }
                }
                if (timer == 2) {
                    if (accessoryTimeValidation()  && accessoryTimer != null && accessoryTimer.length > 14) {
                        if (accessory.isChecked()) {
                            accessoryTimer[15] = "1";
                            mBinding.AccessoryCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));
                        } else {
                            accessoryTimer[15] = "0";
                            mBinding.AccessoryCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                        }
                        accessoryTimer[16] = getPosition(1, toStringValue(mode), accessoryTimerMode);
                        accessoryTimer[17] = formDigits(2, startHour.getText().toString()) + formDigits(2, startMin.getText().toString()) + formDigits(2, startSec.getText().toString());
                        accessoryTimer[18] = getPosition(2, toStringValue(outputName), outputNames);
                        accessoryTimer[19] = getPosition(1, toStringValue(type), accessoryType);
                        alertDialog.dismiss();
                    }

                }
                if (timer == 3) {
                    if (accessoryTimeValidation()  && accessoryTimer != null && accessoryTimer.length > 20) {
                        if (accessory.isChecked()) {
                            accessoryTimer[21] = "1";
                            mBinding.AccessoryCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));

                        } else {
                            accessoryTimer[21] = "0";
                            mBinding.AccessoryCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                        }
                        accessoryTimer[22] = getPosition(1, toStringValue(mode), accessoryTimerMode);
                        accessoryTimer[23] = formDigits(2, startHour.getText().toString()) + formDigits(2, startMin.getText().toString()) + formDigits(2, startSec.getText().toString());
                        accessoryTimer[24] = getPosition(2, toStringValue(outputName), outputNames);
                        accessoryTimer[25] = getPosition(1, toStringValue(type), accessoryType);
                        alertDialog.dismiss();
                    }

                }
                if (timer == 4) {
                    if (accessoryTimeValidation()  && accessoryTimer != null && accessoryTimer.length > 26) {
                        if (accessory.isChecked()) {
                            accessoryTimer[27] = "1";
                            mBinding.AccessoryCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                        } else {
                            accessoryTimer[27] = "0";
                            mBinding.AccessoryCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
                        }
                        accessoryTimer[28] = getPosition(1, toStringValue(mode), accessoryTimerMode);
                        accessoryTimer[29] = formDigits(2, startHour.getText().toString()) + formDigits(2, startMin.getText().toString()) + formDigits(2, startSec.getText().toString());
                        accessoryTimer[30] = getPosition(2, toStringValue(outputName), outputNames);
                        accessoryTimer[31] = getPosition(1, toStringValue(type), accessoryType);
                        alertDialog.dismiss();
                    }
                }

            }
        });


        mode.setAdapter(getAdapter(accessoryTimerMode));
        type.setAdapter(getAdapter(accessoryType));
        outputName.setAdapter(getAdapter(outputNames));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void setAccessoryTimerEnabled(boolean checked, EditText startHour, EditText startMin, EditText startSec,
                                          TextInputLayout outputNameTil, TextInputLayout modeTil, TextInputLayout typeTil) {
        if (checked) {
            startHour.setEnabled(true);
            startMin.setEnabled(true);
            startSec.setEnabled(true);
            outputNameTil.setEnabled(true);
            modeTil.setEnabled(true);
            typeTil.setEnabled(true);
        } else {
            startHour.setEnabled(false);
            startMin.setEnabled(false);
            startSec.setEnabled(false);
            outputNameTil.setEnabled(false);
            modeTil.setEnabled(false);
            typeTil.setEnabled(false);
        }
    }

    private void setTimerDatas(String accessoryTimerEnable, String accessoryTimerMode, String accessoryTimehhmmsec,
                               String accessoryTimerOutput, String accessoryTimerType, CheckBox accessory,
                               AutoCompleteTextView mode, EditText startHour, EditText startMin, EditText startSec,
                               AutoCompleteTextView outputName, AutoCompleteTextView type) {
        if (accessoryTimerEnable.equals("0")) {
            accessory.setChecked(false);
        } else if (accessoryTimerEnable.equals("1")) {
            accessory.setChecked(true);
        }
        if (accessoryTimerMode != null && accessoryTimehhmmsec != null && accessoryTimerOutput != null && accessoryTimerType != null) {
            mode.setText(mode.getAdapter().getItem(Integer.parseInt(accessoryTimerMode)).toString());
            if (accessoryTimehhmmsec.length() == 6) {
                startHour.setText(accessoryTimehhmmsec.substring(0, 2));
                startMin.setText(accessoryTimehhmmsec.substring(2, 4));
                startSec.setText(accessoryTimehhmmsec.substring(4, 6));
            }
            Log.e("output",accessoryTimerOutput);
            outputName.setText(outputName.getAdapter().getItem(Integer.parseInt(accessoryTimerOutput)).toString());
            type.setText(type.getAdapter().getItem(Integer.parseInt(accessoryTimerType)).toString());
        }
    }


    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect") || data.equals("pckError") || data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.connection_failed));
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), getString(R.string.timeout));
        }
        if (data != null) {
            handleResponse(data.split(RES_SPILT_CHAR), 0);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void handleResponse(String[] splitData, int mode) {
        //Timer
        if (splitData[0].equals("{*1") && splitData[1].equals("08")) {
            accessoryTimer = splitData;
            mBinding.timerNameTxt.setText(splitData[4]);
            mBinding.txtOutputNameValueAct.setText(mBinding.txtOutputNameValueAct.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
            flowSensorVisibility(Integer.parseInt(splitData[6]));
            mBinding.txtModeValueAct.setText(mBinding.txtModeValueAct.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
            mBinding.txtFlowSensorValueAct.setText(mBinding.txtFlowSensorValueAct.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());

            //accessoryTimer- set background
            setAccessoryTimerBackground(splitData[9], mBinding.AccessoryCheckbox1, 1);
            setAccessoryTimerBackground(splitData[15], mBinding.AccessoryCheckbox2, 2);
            setAccessoryTimerBackground(splitData[21], mBinding.AccessoryCheckbox3, 3);
            setAccessoryTimerBackground(splitData[27], mBinding.AccessoryCheckbox4, 4);

            weeklyScheduleReadPacket(week1);
        }

        if (splitData[0].equals("{*0") && splitData[1].equals("08")) {
            if (splitData[2].equals("0*}")) {
                WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
                TimerConfigurationDao dao = db.timerConfigurationDao();
                int timerNum = Integer.parseInt(timerNo) + 1;
                dao.updateTimer(mBinding.timerNameTxt.getText().toString(),
                        mBinding.txtOutputNameValueAct.getText().toString(), mBinding.txtModeValueAct.getText().toString(), timerNum);
                writeWeeklySchedule(week1, timerOne, 1);
            }
            if (splitData[2].equals("1*}")) {
                mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
            }
        }
        //Weekly
        if (mode == 0) {
            if (splitData[0].equals("{*1") && splitData[1].equals("09")) {
                if (splitData[4].equals(week1)) {
                    timerOne = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
                        mBinding.switchBtnWeek.setChecked(false);
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_checked));
                        mBinding.switchBtnWeek.setChecked(true);
                    }
                    setWeekBackground(splitData);
                    weeklyScheduleReadPacket(week2);
                }
                if (splitData[4].equals(week2)) {
                    timerTwo = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));
                    }
                    setWeekBackground(splitData);
                    weeklyScheduleReadPacket(week3);
                }
                if (splitData[4].equals(week3)) {
                    timerThree = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));
                    }
                    setWeekBackground(splitData);
                    weeklyScheduleReadPacket(week4);
                }
                if (splitData[4].equals(week4)) {
                    timerFour = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                    }
                    setWeekBackground(splitData);
                    handleResponse(timerOne, 1);
                }
            }
        }

        if (mode == 1) {
            if (splitData[0].equals("{*1") && splitData[1].equals("09")) {
                if (splitData[4].equals(week1)) {
                    timerOne = splitData;
                }
                if (splitData[4].equals(week2)) {
                    timerTwo = splitData;
                }
                if (splitData[4].equals(week3)) {
                    timerThree = splitData;
                }
                if (splitData[4].equals(week4)) {
                    timerFour = splitData;
                }
                setWeekBackground(splitData);
            }
        }

        if (splitData[0].equals("{*0") && splitData[1].equals("09")) {
            if (splitData[2].equals("0*}")) {
                mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                switch (loopWeeklyPacket){
                    case 1:
                        writeWeeklySchedule(week2, timerTwo,2);
                        break;
                    case 2:
                        writeWeeklySchedule(week3, timerThree,3);
                        break;
                    case 3:
                        writeWeeklySchedule(week4, timerFour, 0);
                        break;
                }
            }
            if (splitData[2].equals("1*}")) {
                mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
            }
        }
        initAdapter();

    }

    private void setWeekBackground(String[] splitData) {
        setWeekDayBackground(splitData[6], mBinding.checkBoxMonday);
        setWeekDayBackground(splitData[9], mBinding.checkBoxTuesday);
        setWeekDayBackground(splitData[12], mBinding.checkBoxWednesday);
        setWeekDayBackground(splitData[15], mBinding.checkBoxThursday);
        setWeekDayBackground(splitData[18], mBinding.checkBoxFriday);
        setWeekDayBackground(splitData[21], mBinding.checkBoxSaturday);
        setWeekDayBackground(splitData[24], mBinding.checkBoxSunday);
    }

    private void setWeekDayBackground(String enableWeekDay, View weekdaycheckBox) {
        if (enableWeekDay.equals("0")) {
            weekdaycheckBox.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
        } else if (enableWeekDay.equals("1")) {
            weekdaycheckBox.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
        }
    }

    private void setAccessoryTimerBackground(String enableAccessoryTimer, View accessoryCheckbox, int i) {
        switch (i) {
            case 1:
                if (enableAccessoryTimer.equals("0")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
                } else if (enableAccessoryTimer.equals("1")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.one_checked));
                }
                break;
            case 2:
                if (enableAccessoryTimer.equals("0")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                } else if (enableAccessoryTimer.equals("1")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.two_checked));
                }
                break;
            case 3:
                if (enableAccessoryTimer.equals("0")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                } else if (enableAccessoryTimer.equals("1")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.three_checked));
                }
                break;
            case 4:
                if (enableAccessoryTimer.equals("0")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
                } else if (enableAccessoryTimer.equals("1")) {
                    accessoryCheckbox.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                }
                break;
        }

    }

    private void flowSensorVisibility(int mode) {
        if (mode == 1) {
            mBinding.viewFlowSensor.setVisibility(View.VISIBLE);
            mBinding.txtHeaderFlowSensor.setVisibility(View.VISIBLE);
        } else {
            mBinding.viewFlowSensor.setVisibility(View.GONE);
            mBinding.txtHeaderFlowSensor.setVisibility(View.GONE);
        }
        mBinding.txtFlowSensorValueAct.setAdapter(getAdapter(timerFlowSensor));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Accessory_checkbox_1:
                accessoryTimer(1);
                break;
            case R.id.Accessory_checkbox_2:
                accessoryTimer(2);
                break;
            case R.id.Accessory_checkbox_3:
                accessoryTimer(3);
                break;
            case R.id.Accessory_checkbox_4:
                accessoryTimer(4);
                break;
            case R.id.week_checkbox_1:
                mBinding.switchBtnWeek.setText(getString(R.string.week1_enable));
                week = "Week-1";
                if(timerOne != null) {
                    enabledWeeklySchedule(mBinding.switchBtnWeek, timerOne);
                    handleResponse(timerOne, 1);
                }
                break;

            case R.id.week_checkbox_2:
                mBinding.switchBtnWeek.setText(getString(R.string.week2_enable));
                week = "Week-2";
                if(timerTwo != null) {
                    enabledWeeklySchedule(mBinding.switchBtnWeek, timerTwo);
                    handleResponse(timerTwo, 1);
                }
                break;

            case R.id.week_checkbox_3:
                mBinding.switchBtnWeek.setText(getString(R.string.week3_enable));
                week = "Week-3";
                if(timerThree != null) {
                    enabledWeeklySchedule(mBinding.switchBtnWeek, timerThree);
                    handleResponse(timerThree, 1);
                }
                break;

            case R.id.week_checkbox_4:
                mBinding.switchBtnWeek.setText(getString(R.string.week4_enable));
                week = "Week-4";
                if(timerFour != null) {
                    enabledWeeklySchedule(mBinding.switchBtnWeek, timerFour);
                    handleResponse(timerFour, 1);
                }
                break;

            case R.id.saveLayout_condIS:
            case R.id.saveFab_condIS:
                writeTimerConfiguration();
                break;
            case R.id.check_box_monday:
                dayDialog(1, week, "MONDAY");
                break;
            case R.id.check_box_tuesday:
                dayDialog(2, week, "TUESDAY");
                break;
            case R.id.check_box_wednesday:
                dayDialog(3, week, "WEDNESDAY");
                break;
            case R.id.check_box_thursday:
                dayDialog(4, week, "THURSDAY");
                break;
            case R.id.check_box_friday:
                dayDialog(5, week, "FRIDAY");
                break;
            case R.id.check_box_saturday:
                dayDialog(6, week, "SATURDAY");
                break;
            case R.id.check_box_sunday:
                dayDialog(7, week, "SUNDAY");
                break;

        }
    }

    void weeklyScheduleReadPacket(String week) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_WEEKLY_CONFIG + SPILT_CHAR + timerNo + SPILT_CHAR + week);
    }

    void writeTimerConfiguration() {
        mActivity.showProgress();
        try {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                    SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                    PCK_TIMER_CONFIG + SPILT_CHAR + timerNo + SPILT_CHAR + mBinding.timerNameTxt.getText().toString()
                    + SPILT_CHAR + getPosition(2, toStringValue(mBinding.txtOutputNameValueAct), outputNames)
                    + SPILT_CHAR + getPosition(2, toStringValue(mBinding.txtModeValueAct), timerOutputMode)
                    + SPILT_CHAR + getPosition(1, toStringValue(mBinding.txtFlowSensorValueAct), timerFlowSensor)
                    + SPILT_CHAR + "1"
                    + SPILT_CHAR + accessoryTimer[9]
                    + SPILT_CHAR + accessoryTimer[10]
                    + SPILT_CHAR + accessoryTimer[11]
                    + SPILT_CHAR + accessoryTimer[12]
                    + SPILT_CHAR + accessoryTimer[13]
                    + SPILT_CHAR + "2"
                    + SPILT_CHAR + accessoryTimer[15]
                    + SPILT_CHAR + accessoryTimer[16]
                    + SPILT_CHAR + accessoryTimer[17]
                    + SPILT_CHAR + accessoryTimer[18]
                    + SPILT_CHAR + accessoryTimer[19]
                    + SPILT_CHAR + "3"
                    + SPILT_CHAR + accessoryTimer[21]
                    + SPILT_CHAR + accessoryTimer[22]
                    + SPILT_CHAR + accessoryTimer[23]
                    + SPILT_CHAR + accessoryTimer[24]
                    + SPILT_CHAR + accessoryTimer[25]
                    + SPILT_CHAR + "4"
                    + SPILT_CHAR + accessoryTimer[27]
                    + SPILT_CHAR + accessoryTimer[28]
                    + SPILT_CHAR + accessoryTimer[29]
                    + SPILT_CHAR + accessoryTimer[30]
                    + SPILT_CHAR + accessoryTimer[31]
                    + SPILT_CHAR + "1" + timerOne[5] + "2" + timerTwo[5] + "3" + timerThree[5] + "4" + timerFour[5]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void writeWeeklySchedule(String week, String[] weekType, int loop) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE +
                SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_WEEKLY_CONFIG + SPILT_CHAR + timerNo
                + SPILT_CHAR + week
                + SPILT_CHAR + weekType[5]
                + SPILT_CHAR + weekType[6]
                + SPILT_CHAR + weekType[7]
                + SPILT_CHAR + weekType[8]
                + SPILT_CHAR + weekType[9]
                + SPILT_CHAR + weekType[10]
                + SPILT_CHAR + weekType[11]
                + SPILT_CHAR + weekType[12]
                + SPILT_CHAR + weekType[13]
                + SPILT_CHAR + weekType[14]
                + SPILT_CHAR + weekType[15]
                + SPILT_CHAR + weekType[16]
                + SPILT_CHAR + weekType[17]
                + SPILT_CHAR + weekType[18]
                + SPILT_CHAR + weekType[19]
                + SPILT_CHAR + weekType[20]
                + SPILT_CHAR + weekType[21]
                + SPILT_CHAR + weekType[22]
                + SPILT_CHAR + weekType[23]
                + SPILT_CHAR + weekType[24]
                + SPILT_CHAR + weekType[25]
                + SPILT_CHAR + weekType[26]
        );
        loopWeeklyPacket = loop;
    }

    boolean accessoryTimeValidation() {
        if (startHour.getText().toString().isEmpty()) {
            startHour.setError("");
            mAppClass.showSnackBar(getContext(), "Hour cannot be empty");
            return false;
        } else if (startMin.getText().toString().isEmpty()) {
            startMin.setError("");
            mAppClass.showSnackBar(getContext(), "Min cannot be empty");
            return false;
        } else if (startSec.getText().toString().isEmpty()) {
            startSec.setError("");
            mAppClass.showSnackBar(getContext(), "Sec cannot be empty");
            return false;
        } else if (Integer.parseInt(startHour.getText().toString()) > 24) {
            startHour.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Hour");
            return false;
        } else if (Integer.parseInt(startMin.getText().toString()) > 60) {
            startMin.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Min");
            return false;
        } else if (Integer.parseInt(startSec.getText().toString()) > 60) {
            startSec.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Sec");
            return false;
        } else if ((getPosition(2, toStringValue(mBinding.txtModeValueAct), timerOutputMode).equalsIgnoreCase("00") ||
                getPosition(2, toStringValue(mBinding.txtModeValueAct), timerOutputMode).equalsIgnoreCase("3")) &&
                getPosition(1, toStringValue(mode), accessoryTimerMode).equalsIgnoreCase("1")) {
            mAppClass.showSnackBar(getContext(), "You can't able to choose Timer Safety Flow when the Main Timer Mode is Timer|Disabled");
            return false;
        }

        return true;
    }


    boolean weeklyScheduledValidation() {
        if (startHourDay.getText().toString().isEmpty()) {
            startHourDay.setError("");
            mAppClass.showSnackBar(getContext(), "Hour cannot be empty");
            return false;
        } else if (startMinDay.getText().toString().isEmpty()) {
            startMinDay.setError("");
            mAppClass.showSnackBar(getContext(), "Min cannot be empty");
            return false;
        } else if (startSecDay.getText().toString().isEmpty()) {
            startSecDay.setError("");
            mAppClass.showSnackBar(getContext(), "Sec cannot be empty");
            return false;
        }else if (endHourDay.getText().toString().isEmpty()) {
            endHourDay.setError("");
            mAppClass.showSnackBar(getContext(), "Hour cannot be empty");
            return false;
        } else if (endMinDay.getText().toString().isEmpty()) {
            endMinDay.setError("");
            mAppClass.showSnackBar(getContext(), "Min cannot be empty");
            return false;
        } else if (endSecDay.getText().toString().isEmpty()) {
            endSecDay.setError("");
            mAppClass.showSnackBar(getContext(), "Sec cannot be empty");
            return false;
        } else if (Integer.parseInt(startHourDay.getText().toString()) > 24) {
            startHourDay.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Hour");
            return false;
        } else if (Integer.parseInt(startMinDay.getText().toString()) > 60) {
            startMinDay.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Min");
            return false;
        } else if (Integer.parseInt(startSecDay.getText().toString()) > 60) {
            startSecDay.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Sec");
            return false;
        } else if (Integer.parseInt(endHourDay.getText().toString()) > 24) {
            endHourDay.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Hour");
            return false;
        } else if (Integer.parseInt(endMinDay.getText().toString()) > 60) {
            endMinDay.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Min");
            return false;
        } else if (Integer.parseInt(endSecDay.getText().toString()) > 60) {
            endSecDay.setError("");
            mAppClass.showSnackBar(getContext(), "Invalid Sec");
            return false;
        }

        return true;
    }

    void enabledWeeklySchedule(SwitchMaterial checkBox, String[] week) {
        try {
            if (week[5].equals("1")) {
                checkBox.setChecked(true);
            } else if (week[5].equals("0")) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            checkBox.setChecked(false);
        }
    }

    void applyToAllTime(String[] stringArr, String[] applyStringArr, int day) {
        int enable = 0, startTime = 0, durationTime = 0;
        switch (day) {
            case 1:
                enable = 6;
                startTime = 7;
                durationTime = 8;
                break;
            case 2:
                enable = 9;
                startTime = 10;
                durationTime = 11;
                break;
            case 3:
                enable = 12;
                startTime = 13;
                durationTime = 14;
                break;
            case 4:
                enable = 15;
                startTime = 16;
                durationTime = 17;
                break;
            case 5:
                enable = 18;
                startTime = 19;
                durationTime = 20;
                break;
            case 6:
                enable = 21;
                startTime = 22;
                durationTime = 23;
                break;
            case 7:
                enable = 24;
                startTime = 25;
                durationTime = 26;
                break;
        }
        stringArr[6] = applyStringArr[enable];
        stringArr[7] = applyStringArr[startTime];
        stringArr[8] = applyStringArr[durationTime];
        stringArr[9] = applyStringArr[enable];
        stringArr[10] = applyStringArr[startTime];
        stringArr[11] = applyStringArr[durationTime];
        stringArr[12] = applyStringArr[enable];
        stringArr[13] = applyStringArr[startTime];
        stringArr[14] = applyStringArr[durationTime];
        stringArr[15] = applyStringArr[enable];
        stringArr[16] = applyStringArr[startTime];
        stringArr[17] = applyStringArr[durationTime];
        stringArr[18] = applyStringArr[enable];
        stringArr[19] = applyStringArr[startTime];
        stringArr[20] = applyStringArr[durationTime];
        stringArr[21] = applyStringArr[enable];
        stringArr[22] = applyStringArr[startTime];
        stringArr[23] = applyStringArr[durationTime];
        stringArr[24] = applyStringArr[enable];
        stringArr[25] = applyStringArr[startTime];
        stringArr[26] = applyStringArr[durationTime];
    }
}