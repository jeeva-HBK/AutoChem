package com.ionexchange.Fragments.Configuration.HomeScreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.DefaultLayoutConfigurationDao;
import com.ionexchange.Database.Dao.MainConfigurationDao;
import com.ionexchange.Database.Entity.MainConfigurationEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.DialogDismissListener;
import com.ionexchange.Others.DialogFrag;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSetlayoutBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//Created by Silambu
public class FragmentSetLayout extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    FragmentSetlayoutBinding mBinding;
    int screenNo, layoutNo = 1, windowNo = 1;
    String window;
    WaterTreatmentDb dB;
    DefaultLayoutConfigurationDao dao;
    MainConfigurationDao mainConfigurationDao;
    int pageNo = 1;
    BaseActivity mActivity;
    private static final String TAG = "SetLayout";
    String sensorNotAdded = "Sensor not Added";

    public FragmentSetLayout(int screenNo) {
        this.screenNo = screenNo;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setlayout, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dB = WaterTreatmentDb.getDatabase(getContext());
        mActivity = (BaseActivity) getActivity();
        dao = dB.defaultLayoutConfigurationDao();
        mainConfigurationDao = dB.mainConfigurationDao();
        enableDefaultLayout();
        mBinding.radioLyOne.setOnCheckedChangeListener(this);
        mBinding.radioLyTwo.setOnCheckedChangeListener(this);
        mBinding.radioLyThree.setOnCheckedChangeListener(this);
        mBinding.radioLyFour.setOnCheckedChangeListener(this);
        mBinding.radioLyFive.setOnCheckedChangeListener(this);
        mBinding.radioLySix.setOnCheckedChangeListener(this);

        mBinding.layout1View1.setOnClickListener(this);
        mBinding.layout2View1.setOnClickListener(this);
        mBinding.layout2View2.setOnClickListener(this);

        mBinding.layout3View1.setOnClickListener(this);
        mBinding.layout3View2.setOnClickListener(this);
        mBinding.layout3View3.setOnClickListener(this);

        mBinding.layout4View1.setOnClickListener(this);
        mBinding.layout4View2.setOnClickListener(this);
        mBinding.layout4Layout3.setOnClickListener(this);

        mBinding.layout5View1.setOnClickListener(this);
        mBinding.layout5View2.setOnClickListener(this);
        mBinding.layout5View3.setOnClickListener(this);
        mBinding.layout5View4.setOnClickListener(this);

        mBinding.layout6View1.setOnClickListener(this);
        mBinding.layout6View2.setOnClickListener(this);
        mBinding.layout6View3.setOnClickListener(this);
        mBinding.layout6View4.setOnClickListener(this);
        mBinding.layout6View5.setOnClickListener(this);
        mBinding.layout6View6.setOnClickListener(this);

        mBinding.rightArrowIsBtn.setOnClickListener(this);
        mBinding.leftArrowIsBtn.setOnClickListener(this);
        mBinding.layout1Delete1.setOnClickListener(this);
        mBinding.layout2Delete1.setOnClickListener(this);
        mBinding.layout2Delete2.setOnClickListener(this);
        mBinding.layout3Delete1.setOnClickListener(this);
        mBinding.layout3Delete2.setOnClickListener(this);
        mBinding.layout3Delete3.setOnClickListener(this);
        mBinding.layout4Delete1.setOnClickListener(this);
        mBinding.layout4Delete2.setOnClickListener(this);
        mBinding.layout4Delete3.setOnClickListener(this);
        mBinding.layout5Delete1.setOnClickListener(this);
        mBinding.layout5Delete2.setOnClickListener(this);
        mBinding.layout5Delete3.setOnClickListener(this);
        mBinding.layout5Delete4.setOnClickListener(this);
        mBinding.layout6Delete1.setOnClickListener(this);
        mBinding.layout6Delete2.setOnClickListener(this);
        mBinding.layout6Delete3.setOnClickListener(this);
        mBinding.layout6Delete4.setOnClickListener(this);
        mBinding.layout6Delete5.setOnClickListener(this);
        mBinding.layout6Delete6.setOnClickListener(this);
        mBinding.saveBtn.setOnClickListener(this);
        getSensorWindowNextPage(getTextViewNo(), getViewNo(), pageNo);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            switch (compoundButton.getId()) {
                case R.id.radio_ly_one:
                    mBinding.setSelected("layout1");
                    defaultWindowPage();
                    layoutNo = 1;
                    loopLayoutDetails();

                    break;
                case R.id.radio_ly_two:
                    mBinding.setSelected("layout2");
                    defaultWindowPage();
                    layoutNo = 2;
                    loopLayoutDetails();
                    break;
                case R.id.radio_ly_three:
                    mBinding.setSelected("layout3");
                    defaultWindowPage();
                    layoutNo = 3;
                    mActivity.showProgress();
                    loopLayoutDetails();

                    break;
                case R.id.radio_ly_four:
                    mBinding.setSelected("layout4");
                    defaultWindowPage();
                    layoutNo = 4;
                    loopLayoutDetails();
                    break;
                case R.id.radio_ly_five:
                    mBinding.setSelected("layout5");
                    defaultWindowPage();
                    layoutNo = 5;
                    loopLayoutDetails();
                    break;
                case R.id.radio_ly_six:
                    mBinding.setSelected("layout6");
                    defaultWindowPage();
                    layoutNo = 6;
                    loopLayoutDetails();
                    break;
            }
        }
    }

    void loopLayoutDetails() {
        mActivity.showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                while (i <= layoutNo) {
                    windowNo = i;
                    getSensorWindowNextPage(getTextViewNo(), getViewNo(), pageNo);
                    i++;
                }
                mActivity.dismissProgress();
            }
        }, 1000);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rightArrow_is_btn:
                if (emptyNextSensorValidation(getTextViewNo())) {
                    loopWindowNextPage();
                }

                break;
            case R.id.leftArrow_is_btn:
                if (emptyPrevSensorValidation(getTextViewNo())) {
                    pageNo--;
                    int j = 1;
                    while (j <= layoutNo) {
                        windowNo = j;
                        windowPrevPage();
                        j++;
                    }
                }
                break;

            case R.id.layout_1_delete_1:
                deleteSensorWindowPage(1, mBinding.layout1Delete1);
                break;
            case R.id.layout_2_delete_1:
                deleteSensorWindowPage(1, mBinding.layout2Delete1);
                break;
            case R.id.layout_2_delete_2:
                deleteSensorWindowPage(2, mBinding.layout2Delete2);
                break;
            case R.id.layout_3_delete_1:
                deleteSensorWindowPage(1, mBinding.layout3Delete1);
                break;
            case R.id.layout_3_delete_2:
                deleteSensorWindowPage(2, mBinding.layout3Delete2);
                break;
            case R.id.layout_3_delete_3:
                deleteSensorWindowPage(3, mBinding.layout3Delete3);
                break;
            case R.id.layout_4_delete_1:
                deleteSensorWindowPage(1, mBinding.layout4Delete1);
                break;
            case R.id.layout_4_delete_2:
                deleteSensorWindowPage(2, mBinding.layout4Delete2);
                break;
            case R.id.layout_4_delete_3:
                deleteSensorWindowPage(3, mBinding.layout4Delete3);
                break;
            case R.id.layout_5_delete_1:
                deleteSensorWindowPage(1, mBinding.layout5Delete1);
                break;
            case R.id.layout_5_delete_2:
                deleteSensorWindowPage(2, mBinding.layout5Delete2);
                break;
            case R.id.layout_5_delete_3:
                deleteSensorWindowPage(3, mBinding.layout5Delete3);
                break;
            case R.id.layout_5_delete_4:
                deleteSensorWindowPage(4, mBinding.layout5Delete4);
                break;

            case R.id.layout_6_delete_1:
                deleteSensorWindowPage(1, mBinding.layout6Delete1);
                break;
            case R.id.layout_6_delete_2:
                deleteSensorWindowPage(2, mBinding.layout6Delete2);
                break;
            case R.id.layout_6_delete_3:
                deleteSensorWindowPage(3, mBinding.layout6Delete3);
                break;
            case R.id.layout_6_delete_4:
                deleteSensorWindowPage(4, mBinding.layout6Delete4);
                break;
            case R.id.layout_6_delete_5:
                deleteSensorWindowPage(5, mBinding.layout6Delete5);
                break;
            case R.id.layout_6_delete_6:
                deleteSensorWindowPage(6, mBinding.layout6Delete6);
                break;

            case R.id.save_btn:
                dao.updateEnabledLayout(layoutNo, screenNo);
                getParentFragmentManager().beginTransaction().replace(R.id.setHomeScreenHost, new FragmentSetHomeScreen()).commit();
                break;
            case R.id.layout1_view1:
            case R.id.layout2_view1:
            case R.id.layout2_view2:
            case R.id.layout3_view1:
            case R.id.layout3_view2:
            case R.id.layout3_view3:
            case R.id.layout4_view1:
            case R.id.layout4_view2:
            case R.id.layout4_layout3:
            case R.id.layout5_view1:
            case R.id.layout5_view2:
            case R.id.layout5_view3:
            case R.id.layout5_view4:
            case R.id.layout6_view1:
            case R.id.layout6_view2:
            case R.id.layout6_view3:
            case R.id.layout6_view4:
            case R.id.layout6_view5:
            case R.id.layout6_view6:

                DialogFrag fragment = new DialogFrag();
                window = view.getTag().toString();
                window = window.substring(window.length() - 1);
                windowNo = Integer.parseInt(window);
                FragmentSelectSensors fragmentSetLayout = new FragmentSelectSensors(fragment, screenNo, layoutNo, windowNo, pageNo);
                fragment.init(fragmentSetLayout, "Layout", new DialogDismissListener() {
                    @Override
                    public void OnDismiss() {
                        windowNextPage();
                    }
                });

                fragment.show(getChildFragmentManager(), null);
                break;

        }


    }


    void enableDefaultLayout() {
        switch (dao.enableLayout(screenNo)) {
            case 1:
                mBinding.radioLyOne.setChecked(true);
                mBinding.setSelected("layout1");
                layoutNo = 1;
                loopLayoutDetails();
                break;
            case 2:
                mBinding.radioLyTwo.setChecked(true);
                mBinding.setSelected("layout2");
                layoutNo = 2;
                loopLayoutDetails();
                break;
            case 3:
                mBinding.radioLyThree.setChecked(true);
                mBinding.setSelected("layout3");
                layoutNo = 3;
                loopLayoutDetails();
                break;
            case 4:
                mBinding.radioLyFour.setChecked(true);
                mBinding.setSelected("layout4");
                layoutNo = 4;
                loopLayoutDetails();
                break;
            case 5:
                mBinding.radioLyFive.setChecked(true);
                mBinding.setSelected("layout5");
                layoutNo = 5;
                loopLayoutDetails();
                break;
            case 6:
                mBinding.radioLySix.setChecked(true);
                mBinding.setSelected("layout6");
                layoutNo = 6;
                loopLayoutDetails();
                break;
        }
    }

    private List<View> getViewNo() {
        List<View> viewList = new ArrayList<>();

        switch (mBinding.radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_ly_one:
                viewList.add(mBinding.layout1View1);
                break;
            case R.id.radio_ly_two:
                viewList.add(mBinding.layout2View1);
                viewList.add(mBinding.layout2View2);
                break;
            case R.id.radio_ly_three:
                viewList.add(mBinding.layout3View1);
                viewList.add(mBinding.layout3View2);
                viewList.add(mBinding.layout3View3);
                break;
            case R.id.radio_ly_four:
                viewList.add(mBinding.layout4View1);
                viewList.add(mBinding.layout4View2);
                viewList.add(mBinding.layout4Layout3);
                break;

            case R.id.radio_ly_five:
                viewList.add(mBinding.layout5View1);
                viewList.add(mBinding.layout5View2);
                viewList.add(mBinding.layout5View3);
                viewList.add(mBinding.layout5View4);

                break;

            case R.id.radio_ly_six:
                viewList.add(mBinding.layout6View1);
                viewList.add(mBinding.layout6View2);
                viewList.add(mBinding.layout6View3);
                viewList.add(mBinding.layout6View4);
                viewList.add(mBinding.layout6View5);
                viewList.add(mBinding.layout6View6);
                break;

        }

        return viewList;
    }

    private List<TextView> getTextViewNo() {
        List<TextView> textViewList = new ArrayList<>();
        switch (mBinding.radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_ly_one:
                textViewList.add(mBinding.layout1TextView1);
                break;
            case R.id.radio_ly_two:
                textViewList.add(mBinding.layout2TextView1);
                textViewList.add(mBinding.layout2TextView2);
                break;
            case R.id.radio_ly_three:
                textViewList.add(mBinding.layout3TextView1);
                textViewList.add(mBinding.layout3TextView2);
                textViewList.add(mBinding.layout3TextView3);
                break;
            case R.id.radio_ly_four:
                textViewList.add(mBinding.layout4TextView1);
                textViewList.add(mBinding.layout4TextView2);
                textViewList.add(mBinding.layout4TextView3);
                break;

            case R.id.radio_ly_five:
                textViewList.add(mBinding.layout5TextView1);
                textViewList.add(mBinding.layout5TextView2);
                textViewList.add(mBinding.layout5TextView3);
                textViewList.add(mBinding.layout5TextView4);

                break;

            case R.id.radio_ly_six:
                textViewList.add(mBinding.layout6TextView1);
                textViewList.add(mBinding.layout6TextView2);
                textViewList.add(mBinding.layout6TextView3);
                textViewList.add(mBinding.layout6TextView4);
                textViewList.add(mBinding.layout6TextView5);
                textViewList.add(mBinding.layout6TextView6);
                break;

        }
        return textViewList;
    }


    void loopWindowNextPage() {
        pageNo++;
        int i = 1;
        while (i <= layoutNo) {
            windowNo = i;
            windowNextPage();
            i++;
        }
    }

    void windowNextPage() {
        if (pageNo >= 2) {
            mBinding.leftArrowIsBtn.setVisibility(View.VISIBLE);
        }
        getSensorWindowNextPage(getTextViewNo(), getViewNo(), pageNo);
    }


    void windowPrevPage() {
        if (pageNo == 1) {
            mBinding.leftArrowIsBtn.setVisibility(View.GONE);
        }
        getSensorWindowNextPage(getTextViewNo(), getViewNo(), pageNo);
    }

    void defaultWindowPage() {
        pageNo = 1;
        mBinding.leftArrowIsBtn.setVisibility(View.GONE);
    }

    void getSensorWindowNextPage(List<TextView> textViewList, List<View> viewList, int pageNo) {
        //root of the code
        mBinding.pageNo.setText("PageNo - " + pageNo);
        if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
            switch (mBinding.radioGroup.getCheckedRadioButtonId()) {
                case R.id.radio_ly_one:
                    if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                            && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
                        textViewList.get(0).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                        viewList.get(0).setEnabled(textViewList.get(0).getText().toString().equals(sensorNotAdded));
                        if (textViewList.get(0).getText().toString().equals(sensorNotAdded)) {
                            mBinding.layout1Delete1.setVisibility(View.GONE);
                        } else {
                            mBinding.layout1Delete1.setVisibility(View.VISIBLE);
                        }


                    }
                    break;
                case R.id.radio_ly_two:
                    if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                            && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
                        if (windowNo == 1) {
                            textViewList.get(0).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(0).setEnabled(textViewList.get(0).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(0).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout2Delete1.setVisibility(View.GONE);
                            } else {
                                mBinding.layout2Delete1.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 2) {
                            textViewList.get(1).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(1).setEnabled(textViewList.get(1).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(1).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout2Delete2.setVisibility(View.GONE);
                            } else {
                                mBinding.layout2Delete2.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    break;
                case R.id.radio_ly_three:
                    if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                            && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
                        if (windowNo == 1) {
                            textViewList.get(0).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(0).setEnabled(textViewList.get(0).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(0).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout3Delete1.setVisibility(View.GONE);
                            } else {
                                mBinding.layout3Delete1.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 2) {
                            textViewList.get(1).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(1).setEnabled(textViewList.get(1).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(1).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout3Delete2.setVisibility(View.GONE);
                            } else {
                                mBinding.layout3Delete2.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 3) {
                            textViewList.get(2).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(2).setEnabled(textViewList.get(2).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(2).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout3Delete3.setVisibility(View.GONE);
                            } else {
                                mBinding.layout3Delete3.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                case R.id.radio_ly_four:
                    if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                            && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
                        if (windowNo == 1) {
                            textViewList.get(0).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(0).setEnabled(textViewList.get(0).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(0).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout4Delete1.setVisibility(View.GONE);
                            } else {
                                mBinding.layout4Delete1.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 2) {
                            textViewList.get(1).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(1).setEnabled(textViewList.get(1).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(1).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout4Delete2.setVisibility(View.GONE);
                            } else {
                                mBinding.layout4Delete2.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 3) {
                            textViewList.get(2).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(2).setEnabled(textViewList.get(2).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(2).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout4Delete3.setVisibility(View.GONE);
                            } else {
                                mBinding.layout4Delete3.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    break;
                case R.id.radio_ly_five:
                    if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                            && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
                        if (windowNo == 1) {
                            textViewList.get(0).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(0).setEnabled(textViewList.get(0).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(0).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout5Delete1.setVisibility(View.GONE);
                            } else {
                                mBinding.layout5Delete1.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 2) {
                            textViewList.get(1).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(1).setEnabled(textViewList.get(1).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(1).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout5Delete2.setVisibility(View.GONE);
                            } else {
                                mBinding.layout5Delete2.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 3) {
                            textViewList.get(2).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(2).setEnabled(textViewList.get(2).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(2).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout5Delete3.setVisibility(View.GONE);
                            } else {
                                mBinding.layout5Delete3.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 4) {
                            textViewList.get(3).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(3).setEnabled(textViewList.get(3).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(3).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout5Delete4.setVisibility(View.GONE);
                            } else {
                                mBinding.layout5Delete4.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    break;

                case R.id.radio_ly_six:
                    if (mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo) != null
                            && !mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo).isEmpty()) {
                        if (windowNo == 1) {
                            textViewList.get(0).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(0).setEnabled(textViewList.get(0).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(0).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout6Delete1.setVisibility(View.GONE);
                            } else {
                                mBinding.layout6Delete1.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 2) {
                            textViewList.get(1).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(1).setEnabled(textViewList.get(1).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(1).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout6Delete2.setVisibility(View.GONE);
                            } else {
                                mBinding.layout6Delete2.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 3) {
                            textViewList.get(2).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(2).setEnabled(textViewList.get(2).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(2).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout6Delete3.setVisibility(View.GONE);
                            } else {
                                mBinding.layout6Delete3.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 4) {
                            textViewList.get(3).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(3).setEnabled(textViewList.get(3).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(3).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout6Delete4.setVisibility(View.GONE);
                            } else {
                                mBinding.layout6Delete4.setVisibility(View.VISIBLE);
                            }

                        }
                        if (windowNo == 5) {
                            textViewList.get(4).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(4).setEnabled(textViewList.get(4).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(4).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout6Delete5.setVisibility(View.GONE);
                            } else {
                                mBinding.layout6Delete5.setVisibility(View.VISIBLE);
                            }
                        }
                        if (windowNo == 6) {
                            textViewList.get(5).setText(mainConfigurationDao.getSensorName(screenNo, layoutNo, windowNo, pageNo));
                            viewList.get(5).setEnabled(textViewList.get(4).getText().toString().equals(sensorNotAdded));
                            if (textViewList.get(5).getText().toString().equals(sensorNotAdded)) {
                                mBinding.layout6Delete6.setVisibility(View.GONE);
                            } else {
                                mBinding.layout6Delete6.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    break;
            }
        } else {
            switch (mBinding.radioGroup.getCheckedRadioButtonId()) {
                case R.id.radio_ly_one:
                    textViewList.get(0).setText("N/A");
                    viewList.get(0).setEnabled(true);
                    mBinding.layout1Delete1.setVisibility(View.GONE);
                    break;
                case R.id.radio_ly_two:

                    if (windowNo == 1) {
                        mBinding.layout2Delete1.setVisibility(View.GONE);
                        textViewList.get(0).setText("N/A");
                        viewList.get(0).setEnabled(true);
                    }
                    if (windowNo == 2) {
                        mBinding.layout2Delete2.setVisibility(View.GONE);
                        textViewList.get(1).setText("N/A");
                        viewList.get(1).setEnabled(true);
                    }
                    break;
                case R.id.radio_ly_three:
                    if (windowNo == 1) {
                        mBinding.layout3Delete1.setVisibility(View.GONE);
                        viewList.get(0).setEnabled(true);
                        textViewList.get(0).setText("N/A");
                    }
                    if (windowNo == 2) {
                        mBinding.layout3Delete2.setVisibility(View.GONE);
                        textViewList.get(1).setText("N/A");
                        viewList.get(1).setEnabled(true);
                    }
                    if (windowNo == 3) {
                        mBinding.layout3Delete3.setVisibility(View.GONE);
                        textViewList.get(2).setText("N/A");
                        viewList.get(2).setEnabled(true);
                    }
                    break;
                case R.id.radio_ly_four:

                    if (windowNo == 1) {
                        mBinding.layout4Delete1.setVisibility(View.GONE);
                        viewList.get(0).setEnabled(true);
                        textViewList.get(0).setText("N/A");
                    }
                    if (windowNo == 2) {
                        mBinding.layout4Delete2.setVisibility(View.GONE);
                        textViewList.get(1).setText("N/A");
                        viewList.get(1).setEnabled(true);
                    }
                    if (windowNo == 3) {
                        mBinding.layout4Delete3.setVisibility(View.GONE);
                        textViewList.get(2).setText("N/A");
                        viewList.get(2).setEnabled(true);
                    }
                    break;

                case R.id.radio_ly_five:
                    if (windowNo == 1) {
                        mBinding.layout5Delete1.setVisibility(View.GONE);
                        textViewList.get(0).setText("N/A");
                        viewList.get(0).setEnabled(true);
                    }
                    if (windowNo == 2) {
                        mBinding.layout5Delete2.setVisibility(View.GONE);
                        textViewList.get(1).setText("N/A");
                        viewList.get(1).setEnabled(true);
                    }
                    if (windowNo == 3) {
                        mBinding.layout5Delete3.setVisibility(View.GONE);
                        textViewList.get(2).setText("N/A");
                        viewList.get(2).setEnabled(true);
                    }
                    if (windowNo == 4) {
                        mBinding.layout5Delete4.setVisibility(View.GONE);
                        textViewList.get(3).setText("N/A");
                        viewList.get(3).setEnabled(true);
                    }
                    break;

                case R.id.radio_ly_six:
                    if (windowNo == 1) {
                        mBinding.layout6Delete1.setVisibility(View.GONE);
                        textViewList.get(0).setText("N/A");
                        viewList.get(0).setEnabled(true);
                    }
                    if (windowNo == 2) {
                        mBinding.layout6Delete2.setVisibility(View.GONE);
                        textViewList.get(1).setText("N/A");
                        viewList.get(1).setEnabled(true);
                    }
                    if (windowNo == 3) {
                        mBinding.layout6Delete3.setVisibility(View.GONE);
                        textViewList.get(2).setText("N/A");
                        viewList.get(2).setEnabled(true);
                    }
                    if (windowNo == 4) {
                        mBinding.layout6Delete4.setVisibility(View.GONE);
                        textViewList.get(3).setText("N/A");
                        viewList.get(3).setEnabled(true);
                    }
                    if (windowNo == 3) {
                        mBinding.layout6Delete5.setVisibility(View.GONE);
                        textViewList.get(4).setText("N/A");
                        viewList.get(4).setEnabled(true);
                    }
                    if (windowNo == 4) {
                        mBinding.layout6Delete6.setVisibility(View.GONE);
                        textViewList.get(5).setText("N/A");
                        viewList.get(5).setEnabled(true);
                    }
                    break;

            }

        }

    }

    void deleteSensorWindowPage(int window, View delete) {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Confirmation").
                setMessage("Are you confirm to Delete the sensor ?")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainConfigurationDao.deleteBySnoId(mainConfigurationDao.getSno(screenNo, layoutNo, window, pageNo));
                        delete.setVisibility(View.GONE);
                        int i = 1;
                        while (i <= layoutNo) {
                            windowNo = i;
                            windowNextPage();
                            i++;
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    boolean emptyNextSensorValidation(List<TextView> textViewList) {
        switch (mBinding.radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_ly_one:
                if (textViewList.get(0).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(0), 1);
                    return false;
                }
                break;
            case R.id.radio_ly_two:
                if (textViewList.get(0).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(0), 1);
                    return false;
                } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(1), 2);
                    return false;
                }

                break;
            case R.id.radio_ly_three:
            case R.id.radio_ly_four:
                if (textViewList.get(0).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(0), 1);
                    return false;
                } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(1), 2);
                    return false;
                } else if (textViewList.get(2).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(2), 3);
                    return false;
                }

                break;

            case R.id.radio_ly_five:
                if (textViewList.get(0).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(0), 1);
                    return false;
                } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(1), 2);
                    return false;
                } else if (textViewList.get(2).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(2), 3);
                    return false;
                } else if (textViewList.get(3).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(3), 4);
                    return false;
                }

                break;

            case R.id.radio_ly_six:
                if (textViewList.get(0).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(0), 1);
                    return false;
                } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(1), 2);
                    return false;
                } else if (textViewList.get(2).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(2), 3);
                    return false;
                } else if (textViewList.get(3).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(3), 4);
                    return false;
                } else if (textViewList.get(4).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(4), 5);
                    return false;
                } else if (textViewList.get(5).getText().toString().equals("N/A")) {
                    addEmptySensorToDb(textViewList.get(5), 6);
                    return false;
                }
                break;

        }
        return true;
    }

    boolean emptyPrevSensorValidation(List<TextView> textViewList) {
        switch (mBinding.radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_ly_two:
                if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) == null) {
                    return true;
                } else if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) < 3) {
                    if (textViewList.get(0).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(0), 1);
                        return false;
                    } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(1), 2);
                        return false;
                    }
                    return false;
                }
                break;
            case R.id.radio_ly_three:
            case R.id.radio_ly_four:
                if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) == null) {
                    return true;
                } else if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) < 6) {
                    if (textViewList.get(0).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(0), 1);
                        return false;
                    } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(1), 2);
                        return false;
                    } else if (textViewList.get(2).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(2), 3);
                        return false;
                    }
                    return false;
                }

                break;

            case R.id.radio_ly_five:
                if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) == null) {
                    return true;
                } else if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) < 10) {
                    if (textViewList.get(0).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(0), 1);
                        return false;
                    } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(1), 2);
                        return false;
                    } else if (textViewList.get(2).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(2), 3);
                        return false;
                    } else if (textViewList.get(3).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(3), 4);
                        return false;
                    }
                    return false;
                }

                break;

            case R.id.radio_ly_six:
                if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) == null) {
                    return true;
                } else if (mainConfigurationDao.sumWindowNo(screenNo, layoutNo, pageNo) < 21) {
                    if (textViewList.get(0).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(0), 1);
                        return false;
                    } else if (textViewList.get(1).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(1), 2);
                        return false;
                    } else if (textViewList.get(2).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(2), 3);
                        return false;
                    } else if (textViewList.get(3).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(3), 4);
                        return false;
                    } else if (textViewList.get(4).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(4), 5);
                        return false;
                    } else if (textViewList.get(5).getText().toString().equals("N/A")) {
                        addEmptySensorToDb(textViewList.get(5), 6);
                        return false;
                    }
                    return false;
                }
                break;

        }
        return true;
    }

    void addEmptySensorToDb(TextView textView, int window) {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Sensor Not Added")
                .setMessage("WINDOW - " + window + " is empty. Are you sure, you want to Continue ?").
                setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainConfigurationEntity mainConfigurationEntity = new MainConfigurationEntity
                                (mainConfigurationDao.getLastSno() + 1,
                                        screenNo, layoutNo, window, pageNo,
                                        0, "Sensor not Added", 0, "N/A");
                        List<MainConfigurationEntity> entryListUpdate = new ArrayList<>();
                        entryListUpdate.add(mainConfigurationEntity);
                        updateToDb(entryListUpdate);
                        dialog.dismiss();
                        int i = 1;
                        while (i <= layoutNo) {
                            windowNo = i;
                            windowNextPage();
                            i++;
                        }
                        //   textView.setText("Senor not Added");

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    public void updateToDb(List<MainConfigurationEntity> entryList) {
        WaterTreatmentDb db = WaterTreatmentDb.getDatabase(getContext());
        MainConfigurationDao dao = db.mainConfigurationDao();
        dao.insert(entryList.toArray(new MainConfigurationEntity[0]));
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}



