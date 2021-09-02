package com.ionexchange.Fragments.Configuration.TimerConfig;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;
import static com.ionexchange.Others.ApplicationClass.OutputBleedFlowRate;
import static com.ionexchange.Others.ApplicationClass.accessoryTimerMode;
import static com.ionexchange.Others.ApplicationClass.accessoryType;
import static com.ionexchange.Others.ApplicationClass.timerFlowSensor;
import static com.ionexchange.Others.ApplicationClass.timerOutputMode;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_TIMER_CONFIG;
import static com.ionexchange.Others.PacketControl.PCK_WEEKLY_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTimerstatusConfigBinding;

import org.jetbrains.annotations.NotNull;

public class FragmentTimerStatus_Config extends Fragment implements DataReceiveCallback, View.OnClickListener {
    FragmentTimerstatusConfigBinding mBinding;
    String timerNo;
    BaseActivity mActivity;
    ApplicationClass mAppClass;

    String week1;
    String week2;
    String week3;
    String week4;

    String enabledWeek1;

    int loopWeeklyPacket;
    String week;
    String[] timerOne;
    String[] timerTwo;
    String[] timerThree;
    String[] timerFour;
    String[] accessoryTimer;

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

        mBinding.switchBtnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.switchBtnWeek.isChecked()) {
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-1")) {
                        mBinding.weekCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_checked));
                        timerOne[5] = "1";
                    }
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-2")) {
                        mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));
                        timerTwo[5] = "1";
                    }
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-3")) {
                        mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));
                        timerThree[5] = "1";
                    }
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-4")) {
                        mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                        timerFour[5] = "1";
                    }

                } else {
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-1")) {
                        mBinding.weekCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
                        timerOne[5] = "0";
                    }
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-2")) {
                        mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                        timerTwo[5] = "0";
                    }
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-3")) {
                        mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                        timerThree[5] = "0";
                    }
                    if (mBinding.switchBtnWeek.getText().toString().equals("Enabled Week-4")) {
                        mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
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
        mBinding.txtOutputNameValueAct.setAdapter(getAdapter(OutputBleedFlowRate));
        mBinding.txtModeValueAct.setAdapter(getAdapter(timerOutputMode));
        mBinding.txtFlowSensorValueAct.setAdapter(getAdapter(timerFlowSensor));

    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" +
                SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_TIMER_CONFIG + SPILT_CHAR + timerNo);
    }

    void dayDialog(int day, String week) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_timer_day, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        CheckBox enableSchedule = dialogView.findViewById(R.id.checkbox_enable_time);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnApplyToAll = dialogView.findViewById(R.id.btn_apply_to_all);
        startHourDay = dialogView.findViewById(R.id.number_picker_hour);
        startMinDay = dialogView.findViewById(R.id.number_min_hour);
        startSecDay = dialogView.findViewById(R.id.number_sec_hour);
        endHourDay = dialogView.findViewById(R.id.number_dur_picker_hour);
        endMinDay = dialogView.findViewById(R.id.number_dur_min_hour);
        endSecDay = dialogView.findViewById(R.id.number_dur_sec_hour);

        if (enableSchedule.isChecked()) {
            startHourDay.setEnabled(true);
            startMinDay.setEnabled(true);
            startSecDay.setEnabled(true);
            endHourDay.setEnabled(true);
            endMinDay.setEnabled(true);
            startHourDay.setEnabled(true);
        } else {
            startHourDay.setEnabled(false);
            startMinDay.setEnabled(false);
            startSecDay.setEnabled(false);
            endHourDay.setEnabled(false);
            endMinDay.setEnabled(false);
            endSecDay.setEnabled(false);
        }

        enableSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
        });

        alertDialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        alertDialog.getWindow().setLayout(width, height);


        //DataReceive status
        //Week-1
        if (week.equals("Week-1")) {
            if (day == 1) {
                if (timerOne[6].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[6].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[7].substring(0, 2));
                startMinDay.setText(timerOne[7].substring(2, 4));
                startSecDay.setText(timerOne[7].substring(4, 6));
                endHourDay.setText(timerOne[8].substring(0, 2));
                endMinDay.setText(timerOne[8].substring(2, 4));
                endSecDay.setText(timerOne[8].substring(4, 6));
            }
            if (day == 2) {
                if (timerOne[9].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[9].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[10].substring(0, 2));
                startMinDay.setText(timerOne[10].substring(2, 4));
                startSecDay.setText(timerOne[10].substring(4, 6));
                endHourDay.setText(timerOne[11].substring(0, 2));
                endMinDay.setText(timerOne[11].substring(2, 4));
                endSecDay.setText(timerOne[11].substring(4, 6));
            }
            if (day == 3) {
                if (timerOne[12].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[12].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[13].substring(0, 2));
                startMinDay.setText(timerOne[13].substring(2, 4));
                startSecDay.setText(timerOne[13].substring(4, 6));
                endHourDay.setText(timerOne[14].substring(0, 2));
                endMinDay.setText(timerOne[14].substring(2, 4));
                endSecDay.setText(timerOne[14].substring(4, 6));
            }
            if (day == 4) {
                if (timerOne[15].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[15].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[16].substring(0, 2));
                startMinDay.setText(timerOne[16].substring(2, 4));
                startSecDay.setText(timerOne[16].substring(4, 6));
                endHourDay.setText(timerOne[17].substring(0, 2));
                endMinDay.setText(timerOne[17].substring(2, 4));
                endSecDay.setText(timerOne[17].substring(4, 6));
            }
            if (day == 5) {
                if (timerOne[18].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[18].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[19].substring(0, 2));
                startMinDay.setText(timerOne[19].substring(2, 4));
                startSecDay.setText(timerOne[19].substring(4, 6));
                endHourDay.setText(timerOne[20].substring(0, 2));
                endMinDay.setText(timerOne[20].substring(2, 4));
                endSecDay.setText(timerOne[20].substring(4, 6));
            }
            if (day == 6) {
                if (timerOne[21].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[21].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[22].substring(0, 2));
                startMinDay.setText(timerOne[22].substring(2, 4));
                startSecDay.setText(timerOne[22].substring(4, 6));
                endHourDay.setText(timerOne[23].substring(0, 2));
                endMinDay.setText(timerOne[23].substring(2, 4));
                endSecDay.setText(timerOne[23].substring(4, 6));
            }
            if (day == 7) {
                if (timerOne[24].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerOne[24].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerOne[25].substring(0, 2));
                startMinDay.setText(timerOne[25].substring(2, 4));
                startSecDay.setText(timerOne[25].substring(4, 6));
                endHourDay.setText(timerOne[26].substring(0, 2));
                endMinDay.setText(timerOne[26].substring(2, 4));
                endSecDay.setText(timerOne[26].substring(4, 6));
            }
        }
        //Week-2
        if (week.equals("Week-2")) {
            if (day == 1) {
                if (timerTwo[6].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[6].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[7].substring(0, 2));
                startMinDay.setText(timerTwo[7].substring(2, 4));
                startSecDay.setText(timerTwo[7].substring(4, 6));
                endHourDay.setText(timerTwo[8].substring(0, 2));
                endMinDay.setText(timerTwo[8].substring(2, 4));
                endSecDay.setText(timerTwo[8].substring(4, 6));
            }
            if (day == 2) {
                if (timerTwo[9].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[9].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[10].substring(0, 2));
                startMinDay.setText(timerTwo[10].substring(2, 4));
                startSecDay.setText(timerTwo[10].substring(4, 6));
                endHourDay.setText(timerTwo[11].substring(0, 2));
                endMinDay.setText(timerTwo[11].substring(2, 4));
                endSecDay.setText(timerTwo[11].substring(4, 6));
            }
            if (day == 3) {
                if (timerTwo[12].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[12].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[13].substring(0, 2));
                startMinDay.setText(timerTwo[13].substring(2, 4));
                startSecDay.setText(timerTwo[13].substring(4, 6));
                endHourDay.setText(timerTwo[14].substring(0, 2));
                endMinDay.setText(timerTwo[14].substring(2, 4));
                endSecDay.setText(timerTwo[14].substring(4, 6));
            }
            if (day == 4) {
                if (timerTwo[15].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[15].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[16].substring(0, 2));
                startMinDay.setText(timerTwo[16].substring(2, 4));
                startSecDay.setText(timerTwo[16].substring(4, 6));
                endHourDay.setText(timerTwo[17].substring(0, 2));
                endMinDay.setText(timerTwo[17].substring(2, 4));
                endSecDay.setText(timerTwo[17].substring(4, 6));
            }
            if (day == 5) {
                if (timerTwo[18].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[18].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[19].substring(0, 2));
                startMinDay.setText(timerTwo[19].substring(2, 4));
                startSecDay.setText(timerTwo[19].substring(4, 6));
                endHourDay.setText(timerTwo[20].substring(0, 2));
                endMinDay.setText(timerTwo[20].substring(2, 4));
                endSecDay.setText(timerTwo[20].substring(4, 6));
            }
            if (day == 6) {
                if (timerTwo[21].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[21].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[22].substring(0, 2));
                startMinDay.setText(timerTwo[22].substring(2, 4));
                startSecDay.setText(timerTwo[22].substring(4, 6));
                endHourDay.setText(timerTwo[23].substring(0, 2));
                endMinDay.setText(timerTwo[23].substring(2, 4));
                endSecDay.setText(timerTwo[23].substring(4, 6));
            }
            if (day == 7) {
                if (timerTwo[24].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerTwo[24].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerTwo[25].substring(0, 2));
                startMinDay.setText(timerTwo[25].substring(2, 4));
                startSecDay.setText(timerTwo[25].substring(4, 6));
                endHourDay.setText(timerTwo[26].substring(0, 2));
                endMinDay.setText(timerTwo[26].substring(2, 4));
                endSecDay.setText(timerTwo[26].substring(4, 6));
            }
        }
        //Week-3
        if (week.equals("Week-3")) {
            if (day == 1) {
                if (timerThree[6].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[6].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[7].substring(0, 2));
                startMinDay.setText(timerThree[7].substring(2, 4));
                startSecDay.setText(timerThree[7].substring(4, 6));
                endHourDay.setText(timerThree[8].substring(0, 2));
                endMinDay.setText(timerThree[8].substring(2, 4));
                endSecDay.setText(timerThree[8].substring(4, 6));
            }
            if (day == 2) {
                if (timerThree[9].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[9].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[10].substring(0, 2));
                startMinDay.setText(timerThree[10].substring(2, 4));
                startSecDay.setText(timerThree[10].substring(4, 6));
                endHourDay.setText(timerThree[11].substring(0, 2));
                endMinDay.setText(timerThree[11].substring(2, 4));
                endSecDay.setText(timerThree[11].substring(4, 6));
            }
            if (day == 3) {
                if (timerThree[12].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[12].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[13].substring(0, 2));
                startMinDay.setText(timerThree[13].substring(2, 4));
                startSecDay.setText(timerThree[13].substring(4, 6));
                endHourDay.setText(timerThree[14].substring(0, 2));
                endMinDay.setText(timerThree[14].substring(2, 4));
                endSecDay.setText(timerThree[14].substring(4, 6));
            }
            if (day == 4) {
                if (timerThree[15].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[15].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[16].substring(0, 2));
                startMinDay.setText(timerThree[16].substring(2, 4));
                startSecDay.setText(timerThree[16].substring(4, 6));
                endHourDay.setText(timerThree[17].substring(0, 2));
                endMinDay.setText(timerThree[17].substring(2, 4));
                endSecDay.setText(timerThree[17].substring(4, 6));
            }
            if (day == 5) {
                if (timerThree[18].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[18].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[19].substring(0, 2));
                startMinDay.setText(timerThree[19].substring(2, 4));
                startSecDay.setText(timerThree[19].substring(4, 6));
                endHourDay.setText(timerThree[20].substring(0, 2));
                endMinDay.setText(timerThree[20].substring(2, 4));
                endSecDay.setText(timerThree[20].substring(4, 6));
            }
            if (day == 6) {
                if (timerThree[21].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[21].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[22].substring(0, 2));
                startMinDay.setText(timerThree[22].substring(2, 4));
                startSecDay.setText(timerThree[22].substring(4, 6));
                endHourDay.setText(timerThree[23].substring(0, 2));
                endMinDay.setText(timerThree[23].substring(2, 4));
                endSecDay.setText(timerThree[23].substring(4, 6));
            }
            if (day == 7) {
                if (timerThree[24].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerThree[24].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerThree[25].substring(0, 2));
                startMinDay.setText(timerThree[25].substring(2, 4));
                startSecDay.setText(timerThree[25].substring(4, 6));
                endHourDay.setText(timerThree[26].substring(0, 2));
                endMinDay.setText(timerThree[26].substring(2, 4));
                endSecDay.setText(timerThree[26].substring(4, 6));
            }
        }
        //Week-4
        if (week.equals("Week-4")) {
            if (day == 1) {
                if (timerFour[6].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[6].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[7].substring(0, 2));
                startMinDay.setText(timerFour[7].substring(2, 4));
                startSecDay.setText(timerFour[7].substring(4, 6));
                endHourDay.setText(timerFour[8].substring(0, 2));
                endMinDay.setText(timerFour[8].substring(2, 4));
                endSecDay.setText(timerFour[8].substring(4, 6));
            }
            if (day == 2) {
                if (timerFour[9].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[9].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[10].substring(0, 2));
                startMinDay.setText(timerFour[10].substring(2, 4));
                startSecDay.setText(timerFour[10].substring(4, 6));
                endHourDay.setText(timerFour[11].substring(0, 2));
                endMinDay.setText(timerFour[11].substring(2, 4));
                endSecDay.setText(timerFour[11].substring(4, 6));
            }
            if (day == 3) {
                if (timerFour[12].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[12].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[13].substring(0, 2));
                startMinDay.setText(timerFour[13].substring(2, 4));
                startSecDay.setText(timerFour[13].substring(4, 6));
                endHourDay.setText(timerFour[14].substring(0, 2));
                endMinDay.setText(timerFour[14].substring(2, 4));
                endSecDay.setText(timerFour[14].substring(4, 6));
            }
            if (day == 4) {
                if (timerFour[15].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[15].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[16].substring(0, 2));
                startMinDay.setText(timerFour[16].substring(2, 4));
                startSecDay.setText(timerFour[16].substring(4, 6));
                endHourDay.setText(timerFour[17].substring(0, 2));
                endMinDay.setText(timerFour[17].substring(2, 4));
                endSecDay.setText(timerFour[17].substring(4, 6));
            }
            if (day == 5) {
                if (timerFour[18].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[18].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[19].substring(0, 2));
                startMinDay.setText(timerFour[19].substring(2, 4));
                startSecDay.setText(timerFour[19].substring(4, 6));
                endHourDay.setText(timerFour[20].substring(0, 2));
                endMinDay.setText(timerFour[20].substring(2, 4));
                endSecDay.setText(timerFour[20].substring(4, 6));
            }
            if (day == 6) {
                if (timerFour[21].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[21].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[22].substring(0, 2));
                startMinDay.setText(timerFour[22].substring(2, 4));
                startSecDay.setText(timerFour[22].substring(4, 6));
                endHourDay.setText(timerFour[23].substring(0, 2));
                endMinDay.setText(timerFour[23].substring(2, 4));
                endSecDay.setText(timerFour[23].substring(4, 6));
            }
            if (day == 7) {
                if (timerFour[24].equals("0")) {
                    enableSchedule.setChecked(false);
                } else if (timerFour[24].equals("1")) {
                    enableSchedule.setChecked(true);
                }
                startHourDay.setText(timerFour[25].substring(0, 2));
                startMinDay.setText(timerFour[25].substring(2, 4));
                startSecDay.setText(timerFour[25].substring(4, 6));
                endHourDay.setText(timerFour[26].substring(0, 2));
                endMinDay.setText(timerFour[26].substring(2, 4));
                endSecDay.setText(timerFour[26].substring(4, 6));
            }
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SendData
                if (week.equals("Week-1")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerOne[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerOne[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerOne[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerOne[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerOne[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerOne[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }
                    alertDialog.dismiss();
                }
                if (week.equals("Week-2")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }

                            timerTwo[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }
                    alertDialog.dismiss();
                }
                if (week.equals("Week-3")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerThree[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerThree[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerThree[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerThree[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerThree[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerThree[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }
                    alertDialog.dismiss();
                }
                if (week.equals("Week-4")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerFour[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerFour[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerFour[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerFour[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerFour[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerFour[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            } else {
                                timerFour[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            }
                            timerFour[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
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
                if (week.equals("Week-1")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerOne[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerOne[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerOne[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerOne[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerOne[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerOne[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerOne[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerOne[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerOne[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }

                    applyToAllTime(timerOne, timerOne);
                    handleResponse(timerOne, 1);
                    alertDialog.dismiss();
                }
                if (week.equals("Week-2")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }

                            timerTwo[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerTwo[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerTwo[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerTwo[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerTwo[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }
                    applyToAllTime(timerTwo, timerTwo);
                    handleResponse(timerTwo, 1);
                    alertDialog.dismiss();
                }
                if (week.equals("Week-3")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerThree[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerThree[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerThree[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerThree[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerThree[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerThree[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerThree[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerThree[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerThree[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }
                    applyToAllTime(timerThree, timerThree);
                    handleResponse(timerThree, 1);
                    alertDialog.dismiss();
                }
                if (week.equals("Week-4")) {
                    if (weeklyScheduledValidation()) {
                        if (day == 1) {
                            if (enableSchedule.isChecked()) {
                                timerFour[6] = "1";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[6] = "0";
                                mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[7] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[8] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 2) {
                            if (enableSchedule.isChecked()) {
                                timerFour[9] = "1";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[9] = "0";
                                mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[10] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[11] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 3) {
                            if (enableSchedule.isChecked()) {
                                timerFour[12] = "1";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[12] = "0";
                                mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[13] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[14] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 4) {
                            if (enableSchedule.isChecked()) {
                                timerFour[15] = "1";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[15] = "0";
                                mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[16] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[17] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 5) {
                            if (enableSchedule.isChecked()) {
                                timerFour[18] = "1";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[18] = "0";
                                mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[19] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[20] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 6) {
                            if (enableSchedule.isChecked()) {
                                timerFour[21] = "1";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            } else {
                                timerFour[21] = "0";
                                mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            }
                            timerFour[22] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[23] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                        if (day == 7) {
                            if (enableSchedule.isChecked()) {
                                timerOne[24] = "1";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                            } else {
                                timerFour[24] = "0";
                                mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                            }
                            timerFour[25] = startHourDay.getText().toString() + startMinDay.getText().toString() + startSecDay.getText().toString();
                            timerFour[26] = endHourDay.getText().toString() + endMinDay.getText().toString() + endSecDay.getText().toString();
                        }
                    }
                    applyToAllTime(timerFour, timerFour);
                    handleResponse(timerFour, 1);
                    alertDialog.dismiss();
                }
            }
        });

    }

    void accessoryTimer(int timer) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_accessory_picker, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        //init
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
        outputName.setAdapter(getAdapter(OutputBleedFlowRate));


        if (accessory.isChecked()) {
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

        accessory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
        });
        //OnDataReceive status
        if (timer == 1) {
            if (accessoryTimer[9].equals("0")) {
                accessory.setChecked(false);
            } else if (accessoryTimer[9].equals("1")) {
                accessory.setChecked(true);
            }
            if (accessoryTimer[10] != null && accessoryTimer[11] != null && accessoryTimer[12] != null && accessoryTimer[13] != null) {
                mode.setText(mode.getAdapter().getItem(Integer.parseInt(accessoryTimer[10])).toString());
                startHour.setText(accessoryTimer[11].substring(0, 2));
                startMin.setText(accessoryTimer[11].substring(2, 4));
                startSec.setText(accessoryTimer[11].substring(4, 6));
                outputName.setText(outputName.getAdapter().getItem(Integer.parseInt(accessoryTimer[12])).toString());
                type.setText(type.getAdapter().getItem(Integer.parseInt(accessoryTimer[13])).toString());


            }

        }
        if (timer == 2) {
            if (accessoryTimer[15].equals("0")) {
                accessory.setChecked(false);
            } else if (accessoryTimer[15].equals("1")) {
                accessory.setChecked(true);

            }
            if (accessoryTimer[16] != null && accessoryTimer[17] != null && accessoryTimer[18] != null && accessoryTimer[19] != null) {
                mode.setText(mode.getAdapter().getItem(Integer.parseInt(accessoryTimer[16])).toString());
                startHour.setText(accessoryTimer[17].substring(0, 2));
                startMin.setText(accessoryTimer[17].substring(2, 4));
                startSec.setText(accessoryTimer[17].substring(4, 6));
                outputName.setText(outputName.getAdapter().getItem(Integer.parseInt(accessoryTimer[18])).toString());
                type.setText(type.getAdapter().getItem(Integer.parseInt(accessoryTimer[19])).toString());

            }

        }
        if (timer == 3) {
            if (accessoryTimer[21].equals("0")) {
                accessory.setChecked(false);
            } else if (accessoryTimer[21].equals("1")) {
                accessory.setChecked(true);
            }
            if (accessoryTimer[22] != null && accessoryTimer[23] != null && accessoryTimer[24] != null && accessoryTimer[25] != null) {
                mode.setText(mode.getAdapter().getItem(Integer.parseInt(accessoryTimer[22])).toString());
                startHour.setText(accessoryTimer[23].substring(0, 2));
                startMin.setText(accessoryTimer[23].substring(2, 4));
                startSec.setText(accessoryTimer[23].substring(4, 6));
                outputName.setText(outputName.getAdapter().getItem(Integer.parseInt(accessoryTimer[24])).toString());
                type.setText(type.getAdapter().getItem(Integer.parseInt(accessoryTimer[25])).toString());

            }
        }
        if (timer == 4) {
            if (accessoryTimer[27].equals("0")) {
                accessory.setChecked(false);
            } else if (accessoryTimer[27].equals("1")) {
                accessory.setChecked(true);
            }
            if (accessoryTimer[28] != null && accessoryTimer[29] != null && accessoryTimer[30] != null && accessoryTimer[31] != null) {
                mode.setText(mode.getAdapter().getItem(Integer.parseInt(accessoryTimer[28])).toString());
                startHour.setText(accessoryTimer[29].substring(0, 2));
                startMin.setText(accessoryTimer[29].substring(2, 4));
                startSec.setText(accessoryTimer[29].substring(4, 6));
                outputName.setText(outputName.getAdapter().getItem(Integer.parseInt(accessoryTimer[30])).toString());
                type.setText(type.getAdapter().getItem(Integer.parseInt(accessoryTimer[31])).toString());

            }

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
                    if (accessoryTimeValidation()) {
                        if (accessory.isChecked()) {
                            accessoryTimer[9] = "1";
                            mBinding.AccessoryCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_checked));
                        } else {
                            accessoryTimer[9] = "0";
                            mBinding.AccessoryCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
                        }
                        accessoryTimer[10] = getPosition(1, FragmentTimerStatus_Config.this.toString(mode), accessoryTimerMode);
                        accessoryTimer[11] = startHour.getText().toString() + startMin.getText().toString() + startSec.getText().toString();
                        accessoryTimer[12] = getPosition(2, FragmentTimerStatus_Config.this.toString(outputName), OutputBleedFlowRate);
                        accessoryTimer[13] = getPosition(1, FragmentTimerStatus_Config.this.toString(type), accessoryType);
                        alertDialog.dismiss();
                    }
                }
                if (timer == 2) {
                    if (accessory.isChecked()) {
                        accessoryTimer[15] = "1";
                        mBinding.AccessoryCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));
                    } else {
                        accessoryTimer[15] = "0";
                        mBinding.AccessoryCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                    }
                    accessoryTimer[16] = getPosition(1, FragmentTimerStatus_Config.this.toString(mode), accessoryTimerMode);
                    accessoryTimer[17] = startHour.getText().toString() + startMin.getText().toString() + startSec.getText().toString();
                    accessoryTimer[18] = getPosition(2, FragmentTimerStatus_Config.this.toString(outputName), OutputBleedFlowRate);
                    accessoryTimer[19] = getPosition(1, FragmentTimerStatus_Config.this.toString(type), accessoryType);
                    alertDialog.dismiss();

                }
                if (timer == 3) {
                    if (accessory.isChecked()) {
                        accessoryTimer[21] = "1";
                        mBinding.AccessoryCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));

                    } else {
                        accessoryTimer[21] = "0";
                        mBinding.AccessoryCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                    }
                    accessoryTimer[22] = getPosition(1, FragmentTimerStatus_Config.this.toString(mode), accessoryTimerMode);
                    accessoryTimer[24] = startHour.getText().toString() + startMin.getText().toString() + startSec.getText().toString();
                    accessoryTimer[25] = getPosition(2, FragmentTimerStatus_Config.this.toString(outputName), OutputBleedFlowRate);
                    accessoryTimer[26] = getPosition(1, FragmentTimerStatus_Config.this.toString(type), accessoryType);
                    alertDialog.dismiss();

                }
                if (timer == 4) {
                    if (accessory.isChecked()) {
                        accessoryTimer[27] = "1";
                        mBinding.AccessoryCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                    } else {
                        accessoryTimer[27] = "0";
                        mBinding.AccessoryCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
                    }
                    accessoryTimer[28] = getPosition(1, FragmentTimerStatus_Config.this.toString(mode), accessoryTimerMode);
                    accessoryTimer[29] = startHour.getText().toString() + startMin.getText().toString() + startSec.getText().toString();
                    accessoryTimer[30] = getPosition(2, FragmentTimerStatus_Config.this.toString(outputName), OutputBleedFlowRate);
                    accessoryTimer[31] = getPosition(1, FragmentTimerStatus_Config.this.toString(type), accessoryType);
                    alertDialog.dismiss();

                }

            }
        });


        mode.setAdapter(getAdapter(accessoryTimerMode));
        type.setAdapter(getAdapter(accessoryType));
        outputName.setAdapter(getAdapter(OutputBleedFlowRate));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public void OnDataReceive(String data) {
        mActivity.dismissProgress();
        if (data.equals("FailedToConnect")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("pckError")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("sendCatch")) {
            mAppClass.showSnackBar(getContext(), "Failed to connect");
        }
        if (data.equals("Timeout")) {
            mAppClass.showSnackBar(getContext(), "TimeOut");
        }
        if (data != null) {
            handleResponse(data.split("\\$"), 0);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void handleResponse(String[] splitData, int mode) {
        //Timer
        if (splitData[0].equals("{*1") && splitData[1].equals("08")) {
            accessoryTimer = splitData;
            mBinding.timerNameTxt.setText(splitData[4]);
            mBinding.txtOutputNameValueAct.setText(mBinding.txtOutputNameValueAct.getAdapter().getItem(Integer.parseInt(splitData[5])).toString());
            mBinding.txtModeValueAct.setText(mBinding.txtModeValueAct.getAdapter().getItem(Integer.parseInt(splitData[6])).toString());
            mBinding.txtFlowSensorValueAct.setText(mBinding.txtFlowSensorValueAct.getAdapter().getItem(Integer.parseInt(splitData[7])).toString());

            //accessoryTimer-1
            if (splitData[9].equals("0")) {
                mBinding.AccessoryCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_unchecked));
            } else if (splitData[9].equals("1")) {
                mBinding.AccessoryCheckbox1.setBackground(getResources().getDrawable(R.drawable.one_checked));
            }


            //accessoryTimer-2
            if (splitData[15].equals("0")) {
                mBinding.AccessoryCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));

            } else if (splitData[15].equals("1")) {
                mBinding.AccessoryCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));

            }

            //accessoryTimer-3
            if (splitData[21].equals("0")) {
                mBinding.AccessoryCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));

            } else if (splitData[21].equals("1")) {
                mBinding.AccessoryCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));

            }


            //accessoryTimer-4
            if (splitData[27].equals("0")) {
                mBinding.AccessoryCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
            } else if (splitData[27].equals("1")) {
                mBinding.AccessoryCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
            }

            weeklyScheduleReadPacket(week1);

        }

        if (splitData[0].equals("{*0") && splitData[1].equals("08")) {
            if (splitData[2].equals("0*}")) {
                writeWeeklySchedule(week1, timerOne,1);
            }
            if (splitData[2].equals("1*}")) {
                mAppClass.showSnackBar(getContext(), "WRITE FAILED");
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
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    weeklyScheduleReadPacket(week2);
                }
                if (splitData[4].equals(week2)) {
                    timerTwo = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_unchecked));
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox2.setBackground(getResources().getDrawable(R.drawable.two_checked));
                    }
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    weeklyScheduleReadPacket(week3);
                }
                if (splitData[4].equals(week3)) {
                    timerThree = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_unchecked));
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox3.setBackground(getResources().getDrawable(R.drawable.three_checked));
                    }
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    weeklyScheduleReadPacket(week4);
                }
                if (splitData[4].equals(week4)) {
                    timerFour = splitData;
                    if (splitData[5].equals("0")) {
                        mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_unchecked));
                    } else if (splitData[5].equals("1")) {
                        mBinding.weekCheckbox4.setBackground(getResources().getDrawable(R.drawable.four_cheched));
                    }
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    handleResponse(timerOne, 1);
                }
            }
        }

        if (mode == 1) {
            if (splitData[0].equals("{*1") && splitData[1].equals("09")) {
                if (splitData[4].equals(week1)) {
                    timerOne = splitData;
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }

                }
                if (splitData[4].equals(week2)) {
                    timerTwo = splitData;
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }

                }
                if (splitData[4].equals(week3)) {
                    timerThree = splitData;
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }

                }
                if (splitData[4].equals(week4)) {
                    timerFour = splitData;
                    if (splitData[6].equals("0")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[6].equals("1")) {
                        mBinding.checkBoxMonday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[9].equals("0")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[9].equals("1")) {
                        mBinding.checkBoxTuesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[12].equals("0")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[12].equals("1")) {
                        mBinding.checkBoxWednesday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[15].equals("0")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));

                    } else if (splitData[15].equals("1")) {
                        mBinding.checkBoxThursday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                    if (splitData[18].equals("0")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[18].equals("1")) {
                        mBinding.checkBoxFriday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }

                    if (splitData[21].equals("0")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[21].equals("1")) {
                        mBinding.checkBoxSaturday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));
                    }
                    if (splitData[24].equals("0")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_unchecked));
                    } else if (splitData[24].equals("1")) {
                        mBinding.checkBoxSunday.setBackground(getResources().getDrawable(R.drawable.ic_date_checked));

                    }
                }
            }
        }

        if (splitData[0].equals("{*0") && splitData[1].equals("09")) {
            if (splitData[2].equals("0*}")) {
                mAppClass.showSnackBar(getContext(), "WRITE SUCCESS ");
                switch (loopWeeklyPacket){
                    case 1:
                        writeWeeklySchedule(week2, timerTwo,2);
                        break;
                    case 2:
                        writeWeeklySchedule(week3, timerThree,3);
                        break;

                    case 3:
                        writeWeeklySchedule(week4, timerFour,0);
                        break;
                }
            }
            if (splitData[2].equals("1*}")) {
                mAppClass.showSnackBar(getContext(), "WRITE FAILED");
            }
        }
        initAdapter();


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
                mBinding.switchBtnWeek.setText("Enabled Week-1");
                enabledWeeklySchedule(mBinding.switchBtnWeek, timerOne);
                week = "Week-1";
                handleResponse(timerOne, 1);
                break;

            case R.id.week_checkbox_2:
                mBinding.switchBtnWeek.setText("Enabled Week-2");
                enabledWeeklySchedule(mBinding.switchBtnWeek, timerTwo);
                week = "Week-2";
                handleResponse(timerTwo, 1);
                break;

            case R.id.week_checkbox_3:
                mBinding.switchBtnWeek.setText("Enabled Week-3");
                enabledWeeklySchedule(mBinding.switchBtnWeek, timerThree);
                week = "Week-3";
                handleResponse(timerThree, 1);
                break;


            case R.id.week_checkbox_4:
                mBinding.switchBtnWeek.setText("Enabled Week-4");
                enabledWeeklySchedule(mBinding.switchBtnWeek, timerFour);
                week = "Week-4";
                handleResponse(timerFour, 1);

                break;

            case R.id.saveLayout_condIS:
            case R.id.saveFab_condIS:
                writeTimerConfiguration();
                break;

            case R.id.check_box_monday:
                dayDialog(1, week);
                break;
            case R.id.check_box_tuesday:
                dayDialog(2, week);
                break;
            case R.id.check_box_wednesday:
                dayDialog(3, week);
                break;
            case R.id.check_box_thursday:
                dayDialog(4, week);
                break;
            case R.id.check_box_friday:
                dayDialog(5, week);
                break;
            case R.id.check_box_saturday:
                dayDialog(6, week);
                break;
            case R.id.check_box_sunday:
                dayDialog(7, week);
                break;

        }
    }

    void weeklyScheduleReadPacket(String week) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" +
                SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_WEEKLY_CONFIG + SPILT_CHAR + timerNo + SPILT_CHAR + week);
    }

    void writeTimerConfiguration() {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" +
                SPILT_CHAR + WRITE_PACKET + SPILT_CHAR +
                PCK_TIMER_CONFIG + SPILT_CHAR + timerNo + SPILT_CHAR + mBinding.timerNameTxt.getText().toString()
                + SPILT_CHAR + getPosition(2, toString(mBinding.txtOutputNameValueAct), OutputBleedFlowRate)
                + SPILT_CHAR + getPosition(2, toString(mBinding.txtModeValueAct), timerOutputMode)
                + SPILT_CHAR + getPosition(1, toString(mBinding.txtFlowSensorValueAct), timerFlowSensor)
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


    }

    void writeWeeklySchedule(String week, String[] weekType, int loop) {
        mActivity.showProgress();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + "0" +
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

    public String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    public String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    public String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    private String getPosition(int digit, String string, String[] strArr) {
        String j = null;
        for (int i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])) {
                j = String.valueOf(i);
            }
        }
        return mAppClass.formDigits(digit, j);
    }


    boolean accessoryTimeValidation() {
        if (Integer.parseInt(startHour.getText().toString()) > 24) {
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
        }else if (startHour.getText().toString().isEmpty()) {
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
        }

        return true;
    }


    boolean weeklyScheduledValidation() {
        if (Integer.parseInt(startHourDay.getText().toString()) > 24) {
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
        }else if (startHourDay.getText().toString().isEmpty()) {
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
        }

        return true;
    }

    void enabledWeeklySchedule(SwitchMaterial checkBox, String[] week) {
        if (week[5].equals("1")) {
            checkBox.setChecked(true);
        } else if (week[5].equals("0")) {
            checkBox.setChecked(false);
        }
    }

    void applyToAllTime(String[] stringArr, String[] applyStringArr) {
        stringArr[6] = applyStringArr[6];
        stringArr[7] = applyStringArr[7];
        stringArr[8] = applyStringArr[8];
        stringArr[9] = applyStringArr[6];
        stringArr[10] = applyStringArr[7];
        stringArr[11] = applyStringArr[8];
        stringArr[12] = applyStringArr[6];
        stringArr[13] = applyStringArr[7];
        stringArr[14] = applyStringArr[8];
        stringArr[15] = applyStringArr[6];
        stringArr[16] = applyStringArr[7];
        stringArr[17] = applyStringArr[8];
        stringArr[18] = applyStringArr[6];
        stringArr[19] = applyStringArr[7];
        stringArr[20] = applyStringArr[8];
        stringArr[21] = applyStringArr[6];
        stringArr[22] = applyStringArr[7];
        stringArr[23] = applyStringArr[8];
        stringArr[24] = applyStringArr[6];
        stringArr[25] = applyStringArr[7];
        stringArr[26] = applyStringArr[8];


    }
}