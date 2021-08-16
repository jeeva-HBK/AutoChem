package com.ionexchange.Fragments.Configuration.InputConfig;

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
import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentInputsettingsBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.ApplicationClass.inputTypeArr;
import static com.ionexchange.Others.ApplicationClass.sensorsViArr;

public class FragmentInputSensorList_Config extends Fragment implements RvOnClick, View.OnClickListener {
    FragmentInputsettingsBinding mBinding;
    RvOnClick rvOnClick;
    private static final String TAG = "FragmentInputSettings";
    AutoCompleteTextView inputNumber;
    AutoCompleteTextView sensorName;
    ApplicationClass mAppClass;


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
        mBinding.inputsRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mBinding.inputsRv.setAdapter(new InputsIndexRvAdapter(rvOnClick = this));
        mBinding.addsensorIsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(String inputNumber) {
        frameLayout(inputNumber);
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
                break;
        }
    }

    public ArrayAdapter<String> getAdapter(String[] strArr) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, strArr);
    }


    void frameLayout(String inputNumber) {
        mBinding.inputsRv.setVisibility(View.GONE);
        mBinding.view8.setVisibility(View.GONE);
        switch (inputNumber) {
            case "0":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorPh_Config(inputNumber, 1)).commit();
                break;
            case "1":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorORP_Config(inputNumber, 1)).commit();
                break;
            case "2":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config(inputNumber, 1)).commit();
                break;
            case "3":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config(inputNumber, 1)).commit();
                break;
            case "4":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config(inputNumber, 1)).commit();
                break;
            case "5":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config(inputNumber, 1)).commit();
                break;
            case "6":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorDigital_config(inputNumber, 1)).commit();
                break;
            case "7":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTankLevel_Config(inputNumber, 1)).commit();
                break;
            case "8":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorModbus_Config(inputNumber, 1)).commit();
                break;
            case "9":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorAnalog_Config(inputNumber, 1)).commit();
                break;
        }
    }

    void frameLayout(String inputNumber, String sensorName, String getSenorIndex) {
        mBinding.inputsRv.setVisibility(View.GONE);
        mBinding.view8.setVisibility(View.GONE);
        switch (getSenorIndex) {
            case "0":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorPh_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "1":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorORP_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "2":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorConductivity_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "3":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorToroidalConductivity_config(inputNumber, sensorName, 0)).commit();
                break;
            case "4":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTemp_config(inputNumber, sensorName, 0)).commit();
                break;
            case "5":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorFlow_config(inputNumber, sensorName, 0)).commit();
                break;
            case "6":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorDigital_config(inputNumber, sensorName, 0)).commit();
                break;
            case "7":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorTankLevel_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "8":
                getParentFragmentManager().beginTransaction().replace(R.id.inputHostFrame, new FragmentInputSensorModbus_Config(inputNumber, sensorName, 0)).commit();
                break;
            case "9":
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
}
