package com.ionexchange.Fragments.Configuration.GeneralConfig;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentCommonsettingsBinding;

import org.jetbrains.annotations.NotNull;

import static com.ionexchange.Others.PacketControl.DEVICE_PASSWORD;
import static com.ionexchange.Others.PacketControl.PCK_GENERAL;
import static com.ionexchange.Others.PacketControl.READ_PACKET;
import static com.ionexchange.Others.PacketControl.RES_FAILED;
import static com.ionexchange.Others.PacketControl.RES_SUCCESS;
import static com.ionexchange.Others.PacketControl.SPILT_CHAR;
import static com.ionexchange.Others.PacketControl.WRITE_PACKET;

public class FragmentCommonSettings_Config extends Fragment implements DataReceiveCallback {
    FragmentCommonsettingsBinding mBinding;
    ApplicationClass mAppClass;
    BaseActivity mActivity;
    private static final String TAG = "FragmentCommonSettings";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_commonsettings, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppClass = (ApplicationClass) getActivity().getApplication();
        mActivity = (BaseActivity) getActivity();
        mBinding.saveLayoutCommonSettings.setOnClickListener(this::onCLick);
        mBinding.saveFabCommonSettings.setOnClickListener(this::onCLick);
        mBinding.myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });
    }

    private void getCurrentLocation() {
       /* FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    mAppClass.showSnackBar(getContext(), "Permission Denied !");
                }
            });

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                try {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    mBinding.siteLocationCommonSettingsEDT.setText(addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getLocality() + addresses.get(0).getAdminArea() + " " + addresses.get(0).getPostalCode());

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "" + e, Toast.LENGTH_SHORT).show();
                }

            }
        });*/
    }

    private void onCLick(View view) {
        if (validateFields()) {
            mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + WRITE_PACKET + SPILT_CHAR + PCK_GENERAL + SPILT_CHAR + toString(mBinding.siteIdCommonSettingsEDT) + SPILT_CHAR +
                    toString(mBinding.siteNameCommonSettingsEDT) + SPILT_CHAR + toString(mBinding.sitePasswordCommonSettingsEDT) + SPILT_CHAR + getRadio(mBinding.radioGroup, mBinding.enableCommonSettings) + SPILT_CHAR + toString(mBinding.siteLocationCommonSettingsEDT) + SPILT_CHAR +
                    toString(mBinding.alarmDelayCommonSettingsEDT) + SPILT_CHAR + getRadio(mBinding.radioGroup2, mBinding.fahrenheit) + SPILT_CHAR + toString(mBinding.Hours) + toString(mBinding.MM) + toString(mBinding.SS) + toString(mBinding.NN) + toString(mBinding.DD) +
                    toString(mBinding.month) + toString(mBinding.YYYY));
        }
    }

    private String getRadio(RadioGroup radioGroup, RadioButton zeroValue) {
        if (radioGroup.getCheckedRadioButtonId() == zeroValue.getId()) {
            return "1";
        }
        return "0";
    }

    public String toString(EditText editText) {
        return editText.getText().toString();
    }

    private boolean validateFields() {
        if (isEmpty(mBinding.siteIdCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.siteNameCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.siteLocationCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.sitePasswordCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.alarmDelayCommonSettingsEDT)) {
            return false;
        } else if (isEmpty(mBinding.Hours)) {
            return false;
        } else if (isEmpty(mBinding.MM)) {
            return false;
        } else if (isEmpty(mBinding.SS)) {
            return false;
        } else if (isEmpty(mBinding.NN)) {
            return false;
        } else if (isEmpty(mBinding.DD)) {
            return false;
        } else if (isEmpty(mBinding.month)) {
            return false;
        } else return !isEmpty(mBinding.YYYY);
    }

    private Boolean isEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equals("")) {
            editText.setError("Field shouldn't empty !");
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
        loca();
    }

    private void loca() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            status.startResolutionForResult(getActivity(), 199);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void readData() {
        mAppClass.sendPacket(this, DEVICE_PASSWORD + SPILT_CHAR + READ_PACKET + SPILT_CHAR + PCK_GENERAL);
    }

    @Override
    public void OnDataReceive(String data) {
        if (data != null) {
            handleResponce(data.split("\\*")[1].split("#"));
        }
    }

    private void handleResponce(String[] splitData) {
        // Read - Res - {*1# 03# 0# 007# Russia# 12341# 1# chennai# 30# 0# 120435022072020*}
        // Write - Res - {*0#03#0*}

        if (splitData[1].equals(PCK_GENERAL)) {
            // READ_Response
            if (splitData[0].equals(READ_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {

                    mBinding.siteIdCommonSettingsEDT.setText(splitData[3]);
                    mBinding.siteNameCommonSettingsEDT.setText(splitData[4]);
                    mBinding.sitePasswordCommonSettingsEDT.setText(splitData[5]);

                    if (splitData[6].equals("0")) {
                        mBinding.disableCommonSettings.setChecked(true);
                    } else if (splitData[6].equals("1")) {
                        mBinding.enableCommonSettings.setChecked(true);
                    }

                    mBinding.siteLocationCommonSettingsEDT.setText(splitData[7]);
                    mBinding.alarmDelayCommonSettingsEDT.setText(splitData[8]);

                    if (splitData[9].equals("0")) {
                        mBinding.celsius.setChecked(true);
                    } else if (splitData[9].equals("1")) {
                        mBinding.fahrenheit.setChecked(true);
                    }
                    // FIXME: 22-07-2021 RTC
                    mBinding.Hours.setText(splitData[10].substring(1,2));
                    mBinding.MM.setText(splitData[10].substring(3, 4));
                    mBinding.SS.setText(splitData[10].substring(5, 6));
                    mBinding.NN.setText(splitData[10].substring(7,8));
                    mBinding.month.setText(splitData[10].substring(9, 10));
                    mBinding.YYYY.setText(splitData[10].substring(10, 13));

                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), String.valueOf(R.string.readFailed));
                }
            } else if (splitData[0].equals(WRITE_PACKET)) {
                if (splitData[2].equals(RES_SUCCESS)) {
                    mAppClass.showSnackBar(getContext(), "Write Success");
                } else if (splitData[2].equals(RES_FAILED)) {
                    mAppClass.showSnackBar(getContext(), "Write Failed");
                }
            }
        } else {
            mAppClass.showSnackBar(getContext(), String.valueOf(R.string.wrongPack));
        }
    }
}
