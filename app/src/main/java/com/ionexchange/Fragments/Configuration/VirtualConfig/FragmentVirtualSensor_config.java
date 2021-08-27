package com.ionexchange.Fragments.Configuration.VirtualConfig;

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

import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.VirtualConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentVirtualsensorConfigBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.calculationArr;
import static com.ionexchange.Others.ApplicationClass.findDecimal;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputSensors;
import static com.ionexchange.Others.ApplicationClass.sensorActivationArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.VIRTUAL_INPUT;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentVirtualSensor_config extends Fragment implements DataReceiveCallback {
    FragmentVirtualsensorConfigBinding mBinding;
    ApplicationClass mAppClass;
    int sensorInputNo;
    WaterTreatmentDb db;
    VirtualConfigurationDao dao;

    private static final String TAG = "FragmentVirtualSensor_c";

    public FragmentVirtualSensor_config(int sensorInputNo) {
        this.sensorInputNo = sensorInputNo;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_virtualsensor_config, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        db = WaterTreatmentDb.getDatabase(getContext());
        dao = db.virtualConfigurationDao();

        switch (userType) {
            case 1:
                mBinding.lowRangeVi.setEnabled(false);
                mBinding.highRangeVi.setEnabled(false);
                mBinding.lowAlarmVi.setEnabled(false);
                mBinding.highAlarmVi.setEnabled(false);

                mBinding.vsRow1Isc.setVisibility(View.GONE);
                mBinding.vsRow2Isc.setVisibility(View.GONE);
                mBinding.vsRow4Isc.setVisibility(View.GONE);
                break;

            case 2:
                mBinding.lowRangeVi.setEnabled(false);
                mBinding.highRangeVi.setEnabled(false);
                mBinding.smoothingFactorVi.setEnabled(false);

                mBinding.calculationVi.setVisibility(View.GONE);
                mBinding.sensorActivationVi.setVisibility(View.GONE);
                mBinding.vsRow2Isc.setVisibility(View.GONE);
                mBinding.DeleteLayoutInputSettings.setVisibility(View.GONE);
                break;

            case 3:

                break;
        }
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + VIRTUAL_INPUT + SPILT_CHAR + sensorInputNo);
        initAdapters();
        mBinding.saveFabInputSettings.setOnClickListener(this::save);
        mBinding.saveLayoutInputSettings.setOnClickListener(this::save);
        mBinding.DeleteFabInputSettings.setOnClickListener(this::delete);

        mBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppClass.castFrag(getParentFragmentManager(), R.id.configRootHost, new FragmentVirtualSensorList_Config());
            }
        });
    }

    private void delete(View view) {

    }

    private void save(View view) {
        if (validField()) {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + WRITE_PACKET +
                    SPILT_CHAR + VIRTUAL_INPUT + SPILT_CHAR +
                    sensorInputNo + SPILT_CHAR +
                    getPosition(0, toString(mBinding.sensorActivationViEDT), sensorActivationArr) + SPILT_CHAR +
                    toString(0, mBinding.labelViEDT) + SPILT_CHAR +
                    formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.sensor1ViATXT), inputSensors)) + 1) + "") + SPILT_CHAR +
                    toStringSplit(6, 2, mBinding.sensor1ConstantViEDT) + SPILT_CHAR +
                    formDigits(2, (Integer.parseInt(getPosition(2, toString(mBinding.sensor2ViATXT), inputSensors)) + 1) + "") + SPILT_CHAR +
                    toStringSplit(6, 2, mBinding.sensor2ConstantViEDT) + SPILT_CHAR +
                    toString(4, mBinding.lowRangeViEDT) + SPILT_CHAR +
                    toString(4, mBinding.highRangeViEDT) + SPILT_CHAR +
                    toString(3, mBinding.smoothingFactorViEDT) + SPILT_CHAR +
                    toStringSplit(4, 2, mBinding.lowAlarmViEDT) + SPILT_CHAR +
                    toStringSplit(4, 2, mBinding.highAlarmViEDT) + SPILT_CHAR +
                    getPosition(0, toString(mBinding.calculationViEDT), calculationArr) + SPILT_CHAR + "1"
            );
        }
    }

    private String toStringSplit(int digits, int digitPoint, EditText editText) {
        if (editText.getText().toString().split("\\.").length == 1) {
            return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, "00");
        }
        return mAppClass.formDigits(digits, editText.getText().toString().split("\\.")[0]) + mAppClass.formDigits(digitPoint, editText.getText().toString().split("\\.")[1]);
    }

    private void initAdapters() {
        mBinding.sensorActivationViEDT.setAdapter(getAdapter(sensorActivationArr));
        mBinding.calculationViEDT.setAdapter(getAdapter(calculationArr));
        mBinding.sensor1ViATXT.setAdapter(getAdapter(inputSensors));
        mBinding.sensor2ViATXT.setAdapter(getAdapter(inputSensors));
    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    private boolean validField() {
        if (isEmpty(mBinding.labelViEDT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Label cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.sensorActivationViEDT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor Activation mode");
            return false;
        } else if (isEmpty(mBinding.calculationViEDT)) {
            mAppClass.showSnackBar(getContext(), "Please select Calculation mode");
            return false;
        } else if (isEmpty(mBinding.smoothingFactorViEDT)) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor cannot be Empty");
            return false;
        } else if (Integer.parseInt(mBinding.smoothingFactorViEDT.getText().toString()) > 100) {
            mAppClass.showSnackBar(getContext(), "Smoothing factor value less than 101");
            return false;
        } else if (isEmpty(mBinding.sensor1ViATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor 1 Input Number");
            return false;
        } else if (isEmpty(mBinding.sensor1ConstantViEDT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Constant 1 cannot be Empty");
            return false;
        } else if ((!mBinding.sensor1ConstantViEDT.getText().toString().contains(".") && mBinding.sensor1ConstantViEDT.getText().toString().length() > 6)
                || (mBinding.sensor1ConstantViEDT.getText().toString().contains(".") && find_sixdigit_Decimal(mBinding.sensor1ConstantViEDT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Sensor Constant 1 decimal format like XXXXXX.XX");
            return false;
        } else if (isEmpty(mBinding.sensor2ViATXT)) {
            mAppClass.showSnackBar(getContext(), "Please select Sensor 2 Input Number");
            return false;
        } else if (isEmpty(mBinding.sensor2ConstantViEDT)) {
            mAppClass.showSnackBar(getContext(), "Sensor Constant 2 cannot be Empty");
            return false;
        } else if ((!mBinding.sensor2ConstantViEDT.getText().toString().contains(".") && mBinding.sensor2ConstantViEDT.getText().toString().length() > 6)
                || (mBinding.sensor2ConstantViEDT.getText().toString().contains(".") && find_sixdigit_Decimal(mBinding.sensor2ConstantViEDT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Sensor Constant 2 decimal format like XXXXXX.XX");
            return false;
        } else if (isEmpty(mBinding.lowRangeViEDT)) {
            mAppClass.showSnackBar(getContext(), "Low Range cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.highAlarmViEDT)) {
            mAppClass.showSnackBar(getContext(), "High Range cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.lowAlarmViEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm low cannot be Empty");
            return false;
        } else if (isEmpty(mBinding.highAlarmViEDT)) {
            mAppClass.showSnackBar(getContext(), "Alarm high cannot be Empty");
            return false;
        } else if ((!mBinding.lowAlarmViEDT.getText().toString().contains(".") && mBinding.lowAlarmViEDT.getText().toString().length() > 4)
                || (mBinding.lowAlarmViEDT.getText().toString().contains(".") && findDecimal(mBinding.lowAlarmViEDT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Alarm low decimal format like XXXX.XX");
            return false;
        } else if ((!mBinding.highAlarmViEDT.getText().toString().contains(".") && mBinding.highAlarmViEDT.getText().toString().length() > 4)
                || (mBinding.highAlarmViEDT.getText().toString().contains(".") && findDecimal(mBinding.highAlarmViEDT) == 1)) {
            mAppClass.showSnackBar(getContext(), "Alarm high decimal format like XXXX.XX");
            return false;
        }
        return true;
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

    private String toString(int digits, int value) {
        return mAppClass.formDigits(digits, String.valueOf(value));
    }

    private String toString(AutoCompleteTextView editText) {
        return editText.getText().toString();
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }
    public static Integer find_sixdigit_Decimal(EditText editText) {
        int throwError = 0;
        String[] findDecimalEdtTxt = editText.getText().toString().split("\\.");
        try {
            if (!editText.getText().toString().contains(".") && editText.getText().toString().length() > 6) {
                return 1;
            }else if (findDecimalEdtTxt[0].length() > 6) {
                return 1;
            } else if (findDecimalEdtTxt[1].isEmpty()) {
                return 1;
            } else if (findDecimalEdtTxt[1].length() > 2) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throwError = 1;
        }
        return throwError;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponse(data.split("\\*")[1].split("\\$"));
        }
    }

    private void handleResponse(String[] spiltData) {
        // READ Res - {*1# 05# 0# | 01# 0# VirtualInput3# 01# 2345# 02# 3214# 1100# 2200# 100# 120000# 240000# 1*}
        // WRITE Res -
        if (spiltData[1].equals(VIRTUAL_INPUT)) {
            if (spiltData[0].equals(READ_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mBinding.sensorActivationViEDT.setText(mBinding.sensorActivationViEDT.getAdapter().getItem(Integer.parseInt(spiltData[4])).toString());
                    mBinding.labelViEDT.setText(spiltData[5]);
                    mBinding.sensor1ViATXT.setText(mBinding.sensor1ViATXT.getAdapter().getItem(Integer.parseInt(spiltData[6]) - 1).toString());
                    mBinding.sensor1ConstantViEDT.setText(spiltData[7].substring(0, 6) + "." + spiltData[13].substring(6, 8));
                    mBinding.sensor2ViATXT.setText(mBinding.sensor2ViATXT.getAdapter().getItem(Integer.parseInt(spiltData[8]) - 1).toString());
                    mBinding.sensor2ConstantViEDT.setText(spiltData[9].substring(0, 6) + "." + spiltData[13].substring(6, 8));
                    mBinding.lowRangeViEDT.setText(spiltData[10]);
                    mBinding.highRangeViEDT.setText(spiltData[11]);
                    mBinding.smoothingFactorViEDT.setText(spiltData[12]);
                    mBinding.lowAlarmViEDT.setText(spiltData[13].substring(0, 4) + "." + spiltData[13].substring(4, 6));
                    mBinding.highAlarmViEDT.setText(spiltData[14].substring(0, 4) + "." + spiltData[14].substring(4, 6));
                    mBinding.calculationViEDT.setText(mBinding.calculationViEDT.getAdapter().getItem(Integer.parseInt(spiltData[15])).toString());

                    initAdapters();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.readFailed));
                }
            } else if (spiltData[0].equals(WRITE_PACKET)) {
                if (spiltData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_success));
                    virtualEntity();
                } else if (spiltData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), getString(R.string.update_failed));
                }
            }
        } else {
            Log.e(TAG, "handleResponse: Received Wrong Packet");
        }
    }

    void virtualEntity() {
        VirtualConfigurationEntity virtualConfigurationEntity = new VirtualConfigurationEntity(
                sensorInputNo, 0, toString(0, mBinding.labelViEDT),
                toStringSplit(4, 2, mBinding.lowAlarmViEDT),
                toStringSplit(4, 2, mBinding.highAlarmViEDT));
        List<VirtualConfigurationEntity> entryListUpdate = new ArrayList<>();
        entryListUpdate.add(virtualConfigurationEntity);
        updateToDb(entryListUpdate);
    }

    public void updateToDb(List<VirtualConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        VirtualConfigurationDao dao = db.virtualConfigurationDao();
        dao.insert(entryList.toArray(new VirtualConfigurationEntity[0]));
    }
}
