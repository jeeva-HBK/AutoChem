package com.ionexchange.Fragments.Configuration.InputConfig;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.sensorsViArr;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ionexchange.Adapters.InputsIndexRvAdapter;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.InputConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.InputRvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsettingsBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentInputSensorList_Config extends Fragment implements View.OnClickListener, InputRvOnClick {
    FragmentInputsettingsBinding mBinding;
    List<InputConfigurationEntity> inputConfigurationEntityList;
    AutoCompleteTextView inputNumber;
    AutoCompleteTextView sensorName;
    ApplicationClass mAppClass;
    WaterTreatmentDb dB;
    InputConfigurationDao dao;
    String[] inputHardwareNo;
    private static final String TAG = "FragmentInputSettings";


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
        mBinding.inputsRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
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

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_input_sensor_dailog, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();
                inputNumber = dialogView.findViewById(R.id.add_input_number_dialog_act);
                sensorName = dialogView.findViewById(R.id.add_sensor_name_dialog_act);
                Button btn = dialogView.findViewById(R.id.add_sensor_dialog_btn);
                inputNumber.setAdapter(getAdapter(sensorsViArr));
                sensorName.setAdapter(getAdapter(inputTypeArr));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialogValidation()) {
                            String dialogInput = inputNumber.getText().toString();
                            String dialogSensorName = sensorName.getText().toString();
                            frameLayout(dialogInput, dialogSensorName, getPosition(1, dialogSensorName, inputTypeArr));
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
                alertDialog.getWindow().setLayout(width, height);
                break;
        }
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
            case "Conductivity":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config(inputNumber, 1)).commit();
                break;
            case "Toroidal":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config(inputNumber, 1)).commit();
                break;
            case "Temp":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config(inputNumber, 1)).commit();
                break;
            case "Flow/Water Meter":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config(inputNumber, 1)).commit();
                break;
            case "Digital Sensor":
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

    void frameLayout(String inputNumber, String sensorName, String getSenorIndex) {
        mBinding.inputsRv.setVisibility(View.GONE);
        mBinding.view8.setVisibility(View.GONE);
        switch (sensorName) {
            case "pH":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorPh_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "ORP":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorORP_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "Conductivity":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "Toroidal":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Temp":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Flow/Water Meter":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config(inputNumber, sensorName, 0)).commit();
                break;
            case "Digital Sensor":
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

    @Override
    public void onClick(String sensorInputNo, String sensorType) {
        frameLayout(sensorInputNo, sensorType);
    }
}
