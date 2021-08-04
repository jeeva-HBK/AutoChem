package com.ionexchange.Fragments.Configuration.OutputConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.ionexchange.databinding.FragmentOutputConfigBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.functionMode;
import static com.ionexchange.Others.ApplicationClass.interlockChannel;
import static com.ionexchange.Others.ApplicationClass.linkBleedRelay;
import static com.ionexchange.Others.ApplicationClass.modeAnalog;
import static com.ionexchange.Others.ApplicationClass.modeInhibitor;
import static com.ionexchange.Others.ApplicationClass.modeSensor;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_OUTPUT_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentOutput_Config extends Fragment implements DataReceiveCallback {
    FragmentOutputConfigBinding mBinding;
    ApplicationClass mAppClass;
    String lInhibitorContinuous = "layoutInhibitorContinuous", lInhibitorBleed = "layoutInhibitorBleedDown", lInhibitorWaterFlow = "layoutInhibitorWaterFlow",
            lSensorOnOFF = "layoutSensorOnOff", lSensorPid = "layoutSensorPID", lAnalog = "layoutAnalog", currentFunctionMode = "";
    private static final String TAG = "FragmentOutput_Config";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_output_config, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        initAdapter();
        enableInhibitorLayout();
        mBinding.funtionModeOsATXT.setText(mBinding.funtionModeOsATXT.getAdapter().getItem(1).toString());
        initAdapter();
        mBinding.funtionModeOsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (pos) {
                    case 0:
                        mAppClass.showSnackBar(getContext(), "DISABLED");
                        break;
                    case 1:
                        enableInhibitorLayout();
                        break;
                    case 2:
                        enableSensorLayout();
                        break;
                    case 3:
                        enableAnalogLayout();
                        break;
                }
            }

        });

        mBinding.modeOsATXT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                switch (currentFunctionMode) {
                    case "Inhibitor":
                        switch (pos) {
                            case 0:
                                enableContinuous();
                                break;
                            case 1:
                                enableBleed();
                                break;
                            case 2:
                                enableWater();
                                break;
                        }
                        break;

                    case "Sensor":
                        switch (pos) {
                            case 0:
                                enableOnOff();
                                break;
                            case 1:
                                enablePID();
                                break;
                            case 2:
                                enableFuzzy();
                                break;
                        }
                        break;

                    case "Analog":
                        enableAnalog();
                        break;

                }
            }
        });
    }

    private void enableAnalog() {
        mBinding.setFunctionMode(lAnalog);
    }

    private void enableOnOff() {
        mBinding.setFunctionMode(lSensorOnOFF);
    }

    private void enablePID() {
        mBinding.setFunctionMode(lSensorPid);
    }

    private void enableFuzzy() {

    }

    private void enableWater() {
        mBinding.setFunctionMode("layoutInhibitorWaterFlow");
    }

    private void enableBleed() {
        mBinding.setFunctionMode(lInhibitorBleed);
        mBinding.LinkbleedBleedOsATXT.setAdapter(getAdapter(linkBleedRelay));
    }

    private void enableContinuous() {
        mBinding.setFunctionMode(lInhibitorContinuous);
    }

    private void enableInhibitorLayout() {
        currentFunctionMode = "Inhibitor";
        mBinding.setFunctionMode(lInhibitorContinuous);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeInhibitor));
    }

    private void enableSensorLayout() {
        currentFunctionMode = "Sensor";
        mBinding.setFunctionMode(lSensorOnOFF);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeSensor));
    }

    private void enableAnalogLayout() {
        currentFunctionMode = "Analog";
        mBinding.setFunctionMode(lAnalog);
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
        mBinding.modeOsATXT.setText(mBinding.modeOsATXT.getAdapter().getItem(0).toString());
        mBinding.modeOsATXT.setAdapter(getAdapter(modeAnalog));
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

    private String toString(int digits, EditText editText) {
        return mAppClass.formDigits(digits, editText.getText().toString());
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    private void initAdapter() {
        mBinding.interLockChannelOsATXT.setAdapter(getAdapter(interlockChannel));
        mBinding.activateChannelOsATXT.setAdapter(getAdapter(interlockChannel));
        mBinding.funtionModeOsATXT.setAdapter(getAdapter(functionMode));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_OUTPUT_CONFIG + SPILT_CHAR + "01");
    }

    @Override
    public void OnDataReceive(String data) {

        if (data != null) {
            handleResponse(data.split("\\*")[1].split("#"));
        }

    }

    private void handleResponse(String[] splitData) {
        // {*1# 06# 0# 01# Output1# 30# 30# 1# 0# 125# 322# 212*}
        if (splitData[1].equals(PCK_OUTPUT_CONFIG)) {
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mBinding.outputLabelOsEDT.setText(splitData[4]);
                    mBinding.interLockChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[5]) - 30).toString());
                    mBinding.activateChannelOsATXT.setText(mBinding.interLockChannelOsATXT.getAdapter().getItem(Integer.parseInt(splitData[6]) - 30).toString());
                    switch (splitData[7]) {
                        case "0": // Disable

                            break;
                        case "1": // Inhibitor
                            if (splitData[8].equals("0")) { // Continious
                                mBinding.flowRateContOsATXT.setText(splitData[9]);
                                mBinding.doseRateContOsATXT.setText(splitData[10]);
                                mBinding.dosePeriodOsATXT.setText(splitData[11]);
                            } else if (splitData[8].equals("1")) { // Bleed/Blow
                                mBinding.LinkbleedBleedOsATXT.setText(mBinding.LinkbleedBleedOsATXT.getAdapter().getItem(Integer.parseInt(splitData[8])).toString()); // FIXME: 04-08-2021 change to ATXT
                                mBinding.bleedFlowBleedOsATXT.setText(splitData[12]);
                                mBinding.flowRateBleedOsATXT.setText(splitData[13]);
                                mBinding.targetppmBleedOsATXT.setText(splitData[14]);
                                mBinding.concentrationBleedOsATXT.setText(splitData[15]);
                                mBinding.LinkbleedBleedOsATXT.setAdapter(getAdapter(linkBleedRelay));
                            } else if (splitData[8].equals("2")) { // Water/Meter

                            }
                            break;
                        case "2": // Sensor

                            break;
                        case "3": // Analog

                            break;

                    }


                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Read Failed !");
                }

            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {


                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed !");
                }
            }
        } else {
            Log.e(TAG, "handleResponse: ");
        }

    }
}
