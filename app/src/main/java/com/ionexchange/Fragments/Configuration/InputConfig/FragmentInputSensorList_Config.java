package com.ionexchange.Fragments.Configuration.InputConfig;

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
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.textfield.TextInputLayout;
import com.ionexchange.Adapters.InputsIndexRvAdapter;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Interface.InputRvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ionexchange.Others.ApplicationClass.analogInputArr;
import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.sensorsViArr;
import static com.ionexchange.Others.ApplicationClass.userType;
import static com.ionexchange.Others.PacketControl.CONN_TYPE;
import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_INPUT_SENSOR_CONFIG;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;

public class FragmentInputSensorList_Config extends Fragment implements View.OnClickListener, InputRvOnClick {
    FragmentInputsettingsBinding mBinding;
    List<InputConfigurationEntity> inputConfigurationEntityList;
    AutoCompleteTextView inputNumber;
    AutoCompleteTextView sensorName;
    TextInputLayout sensorTypeLayout;
    ApplicationClass mAppClass;
    WaterTreatmentDb dB;
    InputConfigurationDao dao;
    private static final String TAG = "FragmentInputSettings";
    HashMap<Integer, String> mArr;

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
        dao = dB.inputConfigurationDao();
        if (dao.getInputConfigurationEntityList().isEmpty()) {
            for (int i = 1; i < 46; i++) {
                InputConfigurationEntity entityUpdate = new InputConfigurationEntity
                        (i, "N/A", 0, "N/A",
                                "N/A", "N/A", 0);
                List<InputConfigurationEntity> entryListUpdate = new ArrayList<>();
                entryListUpdate.add(entityUpdate);
                updateToDb(entryListUpdate);
            }
        }
        inputConfigurationEntityList = dao.getInputConfigurationEntityFlagKeyList(1);
        mBinding.inputsRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.inputsRv.setAdapter(new InputsIndexRvAdapter(this, inputConfigurationEntityList));
        mBinding.addsensorIsBtn.setOnClickListener(this);
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
                mArr = new HashMap<>();
                if (userType == 3) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.add_input_sensor_dailog, null);
                    dialogBuilder.setView(dialogView);
                    AlertDialog alertDialog = dialogBuilder.create();

                    inputNumber = dialogView.findViewById(R.id.add_input_number_dialog_act);
                    sensorName = dialogView.findViewById(R.id.add_sensor_name_dialog_act);
                    sensorTypeLayout = dialogView.findViewById(R.id.add_sensor_name_dialog_til);
                    Button btn = dialogView.findViewById(R.id.add_sensor_dialog_btn);
                    inputNumber.setAdapter(getAdapter(sensorsViArr));
                    sensorName.setAdapter(getAdapter(inputTypeArr));

                    inputNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int inputNo = Integer.parseInt(inputNumber.getText().toString());
                            if (inputNo >= 1 && inputNo <= 13) {
                                sensorName.setAdapter(getAdapter(inputTypeArr));

                                getSensorName(inputNo);

                                sensorTypeLayout.setEnabled(false);
                                sensorName.setAdapter(getAdapter(inputTypeArr));

                            } else if (inputNo >= 14 && inputNo <= 21) {
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorName.setText(sensorName.getAdapter().getItem(6).toString());
                                sensorTypeLayout.setEnabled(false);
                                sensorName.setAdapter(getAdapter(analogInputArr));

                            } else if (inputNo >= 22 && inputNo <= 29) {
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorTypeLayout.setEnabled(false);
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorName.setText(sensorName.getAdapter().getItem(3).toString());

                            } else if (inputNo >= 30 && inputNo <= 37) {
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorTypeLayout.setEnabled(false);
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorName.setText(sensorName.getAdapter().getItem(8).toString());

                            } else if (inputNo >= 38 && inputNo <= 45) {
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorTypeLayout.setEnabled(false);
                                sensorName.setAdapter(getAdapter(inputTypeArr));
                                sensorName.setText(sensorName.getAdapter().getItem(7).toString());
                            }
                        }
                    });

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialogValidation()) {
                                frameLayout(inputNumber.getText().toString(), sensorName.getText().toString(),"");
                                alertDialog.dismiss();
                            }
                        }
                    });
                    alertDialog.show();
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
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
                if (sensorNum <= 13) {
                    String[] tempRes = data.split("\\*")[1].split("\\$");
                    if (tempRes[0].equals(READ_PACKET)) {
                        if (tempRes[1].equals(PCK_INPUT_SENSOR_CONFIG)) {
                            if (tempRes[2].equals(RES_SUCCESS)) {
                                sensorName.setText(inputTypeArr[Integer.parseInt(tempRes[4])]);
                            }
                        }
                    }
                }
            }
        }, DEVICE_PASSWORD + SPILT_CHAR + CONN_TYPE + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_INPUT_SENSOR_CONFIG + SPILT_CHAR + formDigits(2, String.valueOf(sensorNum)));
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }

    void frameLayout(String inputNumber, String sensorType) {
        mBinding.inputsRv.setVisibility(View.GONE);
        mBinding.view8.setVisibility(View.GONE);
        switch (sensorType) {
            case "pH":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorPh_Config(inputNumber, 1)).commit();
                break;
            case "ORP":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorORP_Config(inputNumber, 1)).commit();
                break;
            case "Contacting Conductivity":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config(inputNumber, 1)).commit();
                break;
            case "Toroidal Conductivity":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config(inputNumber, 1)).commit();
                break;
            case "Temperature":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config(inputNumber, 1)).commit();
                break;
            case "Flow/Water Meter":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config(inputNumber, 1)).commit();
                break;
            case "Digital Input":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorDigital_config(inputNumber, 1)).commit();
                break;
            case "Tank Level":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTankLevel_Config(inputNumber, 1)).commit();
                break;
            case "Modbus Sensor":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorModbus_Config(inputNumber, 1)).commit();
                break;
            case "Analog Input":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorAnalog_Config(inputNumber, 1)).commit();
                break;
        }
    }

    void frameLayout(String inputNumber, String sensorName, String dumString) {
        mBinding.inputsRv.setVisibility(View.GONE);
        mBinding.view8.setVisibility(View.GONE);
        switch (sensorName) {
            case "pH":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorPh_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "ORP":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorORP_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "Contacting Conductivity":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "Toroidal Conductivity":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Temperature":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Flow/Water Meter":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Digital Input":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorDigital_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Tank Level":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTankLevel_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "Modbus Sensor":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorModbus_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "Analog Input":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorAnalog_Config(inputNumber, sensorName, 0)).commit();
                break;
        }
    }

    boolean dialogValidation() {

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
