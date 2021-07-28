package com.ionexchange.Fragments.Configuration.InputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsensorCondBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.resetCalibrationArr;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.tempLinkedArr;
import static com.ionexchange.Others.ApplicationClass.unitArr;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentInputSensorConductivity_Config extends Fragment implements DataReceiveCallback {
    private static final String TAG = "FragmentInputSensorCond";
    FragmentInputsensorCondBinding mBinding;
    ApplicationClass mAppClass;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsensor_cond, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        initAdapters();

        mBinding.saveFabCondIS.setOnClickListener(this::save);
        mBinding.saveLayoutCondIS.setOnClickListener(this::save);
        mBinding.DeleteFabCondIS.setOnClickListener(this::delete);
        mBinding.DeleteLayoutCondIS.setOnClickListener(this::delete);

        mBinding.backArrow.setOnClickListener(v -> {
            mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentInputSensorList_Config());
        });
    }

    private void delete(View view) {

    }

    private void save(View view) {
        // getPosition(toString(mBinding.sensorTypeCondISATXT), inputTypeArr)
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "04" + SPILT_CHAR +
                toString(mBinding.inputNumberCondISEDT) + SPILT_CHAR + getPosition(toString(mBinding.sensorActivationCondISATXT), sensorActivationArr) + SPILT_CHAR +
                toString(mBinding.inputLabelCondISEdt) + SPILT_CHAR + getPosition(toString(mBinding.tempLinkedCondISEdt), tempLinkedArr) + SPILT_CHAR + toString(mBinding.tempValueCondISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.unitOfMeasureCondISEdt), unitArr) + SPILT_CHAR + toString(mBinding.tempCompCondISEdt) + SPILT_CHAR + toString(mBinding.tempCompFacCondISEdt) + SPILT_CHAR +
                toString(mBinding.smoothingFactorCondISEdt) + SPILT_CHAR + toString(mBinding.alarmLowCondISEdt) + SPILT_CHAR + toString(mBinding.alarmHighCondISEdt) + SPILT_CHAR + toString(mBinding.calibRequiredAlarmCondISEdt) + SPILT_CHAR +
                getPosition(toString(mBinding.resetCalibCondISEdt), resetCalibrationArr)
        );
    }

    private int getPosition(String string, String[] strArr) {
        int i;
        for ( i = 0; i < strArr.length; i++) {
            if (string.equals(strArr[i])){
                return i;
            }
        }
        return i;
    }

    private String toString(EditText editText) {
        return editText.getText().toString();
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapters() {
        mBinding.sensorTypeCondISATXT.setAdapter(getAdapter(inputTypeArr));
        mBinding.sensorActivationCondISATXT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.tempLinkedCondISEdt.setAdapter(getAdapter(tempLinkedArr));
        mBinding.unitOfMeasureCondISEdt.setAdapter(getAdapter(unitArr));
        mBinding.resetCalibCondISEdt.setAdapter(getAdapter(resetCalibrationArr));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + INPUT_SENSOR_CONFIG + SPILT_CHAR + "03");
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponse(String[] spiltData) {
        // read Res - {*1# 04# 0# | 03# 04# 0# CONCON# 1# 33# 1# 2345# 2371# 1500# 100# 120000# 220000# 300# 1*}
        // write Res -
        if (spiltData[1].equals(INPUT_SENSOR_CONFIG)) {

            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.inputNumberCondISEDT.setText(spiltData[3]);
                    mBinding.sensorTypeCondISATXT.setText(mBinding.sensorTypeCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.sensorActivationCondISATXT.setText(mBinding.sensorActivationCondISATXT.getAdapter().getItem(Integer.parseInt(spiltData[5])).toString());
                    mBinding.inputLabelCondISEdt.setText(spiltData[6]);
                    mBinding.tempLinkedCondISEdt.setText(mBinding.tempLinkedCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[7])).toString());
                    mBinding.tempValueCondISEdt.setText(spiltData[8]);
                    mBinding.unitOfMeasureCondISEdt.setText(mBinding.unitOfMeasureCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[9])).toString());
                    mBinding.cellConstantCondISEdt.setText(spiltData[10]);
                    mBinding.tempCompCondISEdt.setText(spiltData[11]);
                    mBinding.tempCompFacCondISEdt.setText(spiltData[12]);
                    mBinding.smoothingFactorCondISEdt.setText(spiltData[13]);
                    mBinding.alarmLowCondISEdt.setText(spiltData[14]);
                    mBinding.alarmHighCondISEdt.setText(spiltData[15]);
                    mBinding.calibRequiredAlarmCondISEdt.setText(spiltData[16]);
                    mBinding.resetCalibCondISEdt.setText(mBinding.resetCalibCondISEdt.getAdapter().getItem(Integer.parseInt(spiltData[17])).toString());

                    initAdapters();

                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "READ FAILED");
                }
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "WRITE SUCCESS");
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "WRITE FAILED");
                }
            }

        } else {
            Log.e(TAG, "handleResponse: Wrong Packet");
        }

    }
}
