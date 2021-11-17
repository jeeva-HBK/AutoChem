package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Others.ApplicationClass.analogArr;
import static com.ionexchange.Others.ApplicationClass.digitalSensorArr;
import static com.ionexchange.Others.ApplicationClass.flowmeterArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.modbusArr;
import static com.ionexchange.Others.ApplicationClass.sensorArr;
import static com.ionexchange.Others.ApplicationClass.sensorTypeArr;
import static com.ionexchange.Others.ApplicationClass.sensorsViArr;
import static com.ionexchange.Others.ApplicationClass.tankArr;
import static com.ionexchange.Others.ApplicationClass.temperatureArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.InputsIndexRvAdapter;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.KeepAliveCurrentValueDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.Entity.KeepAliveCurrentEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.InputRvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentInputSensorList_Config extends Fragment implements View.OnClickListener, InputRvOnClick {
    FragmentInputsettingsBinding mBinding;
    AutoCompleteTextView sensorType, inputNumber, sensorName;
    ApplicationClass mAppClass;
    WaterTreatmentDb dB;
    InputConfigurationDao dao;
    KeepAliveCurrentValueDao keepAliveCurrentValueDao;
    List<KeepAliveCurrentEntity>keepAliveCurrentEntityList;
    private static final String TAG = "FragmentInputSettings";
    int pageOffset = 0, currentPage = 0, sequenceNo = 0, sequenceType = 0, sequenceValueRead = 0, analogType = 0, flowmeterType = 0;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inputsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        dB = WaterTreatmentDb.getDatabase(getContext());
        dao = ApplicationClass.inputDAO;
        keepAliveCurrentValueDao = dB.keepAliveCurrentValueDao();
        keepAliveCurrentEntityList = keepAliveCurrentValueDao.getKeepAliveList();
        mBinding.rightArrowIsBtn.setVisibility((dao.getInputConfigurationEntityFlagKeyList(1).size() / 9) > 0 ? View.VISIBLE : View.GONE);
        mBinding.addsensorIsBtn.setOnClickListener(this);

        mBinding.inputsRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.inputsRv.setAdapter(new InputsIndexRvAdapter(this, dao.getInputConfigurationEntityFlagKeyList(1, 9, pageOffset)));

        mBinding.leftArrowIsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage--;
                mBinding.inputsRv.setAdapter(new InputsIndexRvAdapter(FragmentInputSensorList_Config.this, dao.getInputConfigurationEntityFlagKeyList(1, 9, pageOffset = pageOffset - 9)));
                mBinding.leftArrowIsBtn.setVisibility(currentPage <= 0 ? View.GONE : View.VISIBLE);
                mBinding.rightArrowIsBtn.setVisibility(View.VISIBLE);
            }
        });

        mBinding.rightArrowIsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage++;
                mBinding.leftArrowIsBtn.setVisibility(View.VISIBLE);
                mBinding.inputsRv.setAdapter(new InputsIndexRvAdapter(FragmentInputSensorList_Config.this, dao.getInputConfigurationEntityFlagKeyList(1, 9, pageOffset = pageOffset + 9)));
                mBinding.rightArrowIsBtn.setVisibility(dao.getInputConfigurationEntityFlagKeyList(1, 9, pageOffset + 9).isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        keepAliveCurrentValueDao.getLiveList().observe(getViewLifecycleOwner(), new Observer<List<KeepAliveCurrentEntity>>() {
            @Override
            public void onChanged(List<KeepAliveCurrentEntity> keepAliveCurrentEntities) {
                mBinding.inputsRv.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void updateToDb(List<InputConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        InputConfigurationDao dao = db.inputConfigurationDao();
        dao.insert(entryList.toArray(new InputConfigurationEntity[0]));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addsensor_is_btn:
                if (userType == 3) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.add_input_sensor_dailog, null);
                    dialogBuilder.setView(dialogView);
                    AlertDialog alertDialog = dialogBuilder.create();

                    sensorType = dialogView.findViewById(R.id.add_input_type_dialog_act);
                    inputNumber = dialogView.findViewById(R.id.add_input_number_dialog_act);
                    sensorName = dialogView.findViewById(R.id.add_sensor_name_dialog_act);
                    Button btn = dialogView.findViewById(R.id.add_sensor_dialog_btn);
                    sensorType.setAdapter(getAdapter(sensorTypeArr));
                    inputNumber.setAdapter(getAdapter(sensorsViArr));
                    sensorName.setAdapter(getAdapter(inputTypeArr));

                    sensorType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String sensor_type = sensorType.getText().toString();
                            switch (sensor_type){
                                case "Sensor":
                                    inputNumber.setAdapter(getAdapter(sensorArr));
                                    break;
                                case "Temperature":
                                    inputNumber.setAdapter(getAdapter(temperatureArr));
                                    break;
                                case "Modbus":
                                    inputNumber.setAdapter(getAdapter(modbusArr));
                                    break;
                                case "Analog Input":
                                    inputNumber.setAdapter(getAdapter(analogArr));
                                    break;
                                case "Flow/Water Meter":
                                    inputNumber.setAdapter(getAdapter(flowmeterArr));
                                    break;
                                case "Digital Input":
                                    inputNumber.setAdapter(getAdapter(digitalSensorArr));
                                    break;
                                case "Tank Level":
                                    inputNumber.setAdapter(getAdapter(tankArr));
                                    break;
                            }
                            inputNumber.setText("");
                            inputNumber.setHint(getString(R.string.input_number));
                        }
                    });
                    inputNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int inputNo = Integer.parseInt(inputNumber.getText().toString());
                            sensorName.setAdapter(getAdapter(inputTypeArr));
                            getSensorName(inputNo);
                            sensorName.setAdapter(getAdapter(inputTypeArr));
                        }
                    });

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogValidation()) {
                                frameLayout(inputNumber.getText().toString(), sensorName.getText().toString(),"",sequenceNo,sequenceType,sequenceValueRead,analogType);
                                alertDialog.dismiss();
                            }
                        }
                    });
                    alertDialog.show();
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
                    alertDialog.getWindow().setLayout(width, height);

                } else {
                    mAppClass.showSnackBar(getContext(), "Access Denied !");
                }

                break;
        }
    }

    void getSensorName(int sensorNum) {
        mAppClass.sendPacket(new DataReceiveCallback() {
            @Override
            public void OnDataReceive(String data) {
                if (sensorNum <= 17) {
                    String[] tempRes = data.split("\\*")[1].split(RES_SPILT_CHAR);
                    if (tempRes[0].equals(READ_PACKET)) {
                        if (tempRes[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
                            if (tempRes[2].equals(RES_SUCCESS)) {
                                sensorName.setText(inputTypeArr[Integer.parseInt(tempRes[4])]);
                                sequenceNo = Integer.parseInt(tempRes[5]);
                                if(sensorNum >= 5 && sensorNum <= 14){
                                    sequenceType = Integer.parseInt(tempRes[6]);
                                    sequenceValueRead = Integer.parseInt(tempRes[7]);
                                }
                            }
                        }
                    }
                } else if (sensorNum >= 18 && sensorNum <= 49) {
                    String[] tempRes = data.split("\\*")[1].split(RES_SPILT_CHAR);
                    if (tempRes[0].equals(READ_PACKET)) {
                        if (tempRes[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
                            if (tempRes[2].equals(RES_SUCCESS)) {
                                sensorName.setText(inputTypeArr[Integer.parseInt(tempRes[4])]);
                                sequenceNo = Integer.parseInt(tempRes[5]);
                                if(sensorNum <= 33){
                                    sequenceNo = Integer.parseInt(tempRes[6]);
                                    sequenceType = Integer.parseInt(tempRes[5]);
                                    if (sensorNum <= 25) {
                                        analogType = Integer.parseInt(tempRes[7]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR +
                PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, String.valueOf(sensorNum)));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    void frameLayout(String inputNumber, String sensorType) {
        switch (sensorType) {
            case "pH":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_phConfig, updateBundle(inputNumber, 1));
                break;
            case "ORP":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_orpConfig, updateBundle(inputNumber, 1));
                break;
            case "Contacting Conductivity":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_conductivityConfig, updateBundle(inputNumber, 1));
                break;
            case "Toroidal Conductivity":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_ToroidalConfig, updateBundle(inputNumber, 1));
                break;
            case "Temperature":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_tempConfig, updateBundle(inputNumber, 1));
                break;
            case "Flow/Water Meter":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_flowConfig, updateBundle(inputNumber, 1));
            case "Digital Input":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_digitalConfig, updateBundle(inputNumber, 1));
                break;
            case "Tank Level":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_tankLevel, updateBundle(inputNumber, 1));
                break;
            case "Modbus Sensor":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_modBusConfig, updateBundle(inputNumber, 1));
                break;
            case "Analog Input":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_AnalogConfig, updateBundle(inputNumber, 1));
                break;
        }
    }

    Bundle updateBundle(String inputNumber, int sensorStatus) {
        Bundle bundle = new Bundle();
        bundle.putString("inputNumber", inputNumber);
        bundle.putInt("sensorStatus", sensorStatus);
        return bundle;
    }

    Bundle AddBundle(String inputNumber, String sensorName, int sensorStatus) {
        Bundle bundle = new Bundle();
        bundle.putString("inputNumber", inputNumber);
        bundle.putString("sensorName", sensorName);
        bundle.putInt("sensorStatus", sensorStatus);
        return bundle;
    }

    Bundle AddBundle(String inputNumber, String sensorName, int sensorStatus, String sequenceNo) {
        Bundle bundle = new Bundle();
        bundle.putString("inputNumber", inputNumber);
        bundle.putString("sensorName", sensorName);
        bundle.putInt("sensorStatus", sensorStatus);
        bundle.putString("sequenceNo", sequenceNo);
        return bundle;
    }

    Bundle AddBundle(String inputNumber, String sensorName, int sensorStatus, int sequenceType, int sequenceValueRead) {
        Bundle bundle = new Bundle();
        bundle.putString("inputNumber", inputNumber);
        bundle.putString("sensorName", sensorName);
        bundle.putInt("sensorStatus", sensorStatus);
        bundle.putInt("sequenceType", sequenceType);
        bundle.putInt("sequenceValueRead", sequenceValueRead);
        return bundle;
    }

    Bundle AddBundle(String inputNumber, String sensorName, int sensorStatus, String sequenceNo, int sequenceType, int sequenceValueRead) {
        Bundle bundle = new Bundle();
        bundle.putString("inputNumber", inputNumber);
        bundle.putString("sensorName", sensorName);
        bundle.putInt("sensorStatus", sensorStatus);
        bundle.putString("sequenceNo", sequenceNo);
        bundle.putInt("sequenceType", sequenceType);
        bundle.putInt("sequenceValueRead", sequenceValueRead);
        return bundle;
    }

    void frameLayout(String inputNumber, String sensorName, String dumString, int sequenceNo, int sequenceType, int sequenceValueRead, int analogType) {
        switch (sensorName) {
            case "pH":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_phConfig, AddBundle(inputNumber, sensorName, 0));
                break;
            case "ORP":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_orpConfig, AddBundle(inputNumber, sensorName, 0));
                break;
            case "Contacting Conductivity":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_conductivityConfig, AddBundle(inputNumber, sensorName, 0));
                break;
            case "Toroidal Conductivity":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_ToroidalConfig, AddBundle(inputNumber, sensorName, 0));
                break;
            case "Temperature":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_tempConfig, AddBundle(inputNumber, sensorName, 0, Integer.toString(sequenceNo)));
                break;
            case "Flow/Water Meter":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_flowConfig, AddBundle(inputNumber, sensorName, 0, Integer.toString(sequenceNo), sequenceType, 0));
                break;
            case "Digital Input":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_digitalConfig, AddBundle(inputNumber, sensorName, 0, Integer.toString(sequenceNo)));
                break;
            case "Tank Level":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_tankLevel, AddBundle(inputNumber, sensorName, 0, Integer.toString(sequenceNo)));
                break;
            case "Modbus Sensor":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_modBusConfig, AddBundle(inputNumber, sensorName, 0, sequenceType, sequenceValueRead));
                break;
            case "Analog Input":
                mAppClass.navigateToBundle(getActivity(), R.id.action_inputSetting_to_AnalogConfig, AddBundle(inputNumber, sensorName, 0, Integer.toString(sequenceNo), sequenceType, analogType));
                break;
        }
    }

    boolean dialogValidation() {
        if (sensorType.getText().toString().isEmpty()) {
            sensorType.requestFocus();
            mAppClass.showSnackBar(getContext(), "Select Sensor Type");
            return false;
        }
        if (inputNumber.getText().toString().isEmpty()) {
            inputNumber.requestFocus();
            mAppClass.showSnackBar(getContext(), "Input Number Cannot be Empty");
            return false;
        }
        if (sensorName.getText().toString().isEmpty()) {
            inputNumber.requestFocus();
            mAppClass.showSnackBar(getContext(), "sensorName Cannot be Empty");
            return false;
        }
        if (sensorName.getText().toString().equals("N/A")) {
            inputNumber.requestFocus();
            mAppClass.showSnackBar(getContext(), "sensorName Cannot be Empty");
        }
        return true;
    }

    @Override
    public void onClick(String sensorInputNo, String sensorType) {
        frameLayout(sensorInputNo, sensorType);
    }

}
