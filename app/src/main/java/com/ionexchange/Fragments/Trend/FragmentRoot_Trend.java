package com.ionexchange.Fragments.Trend;

import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.getCurrentDate;
import static com.ionexchange.Others.ApplicationClass.lessThanAWeek;
import static com.ionexchange.Others.ApplicationClass.virtualDAO;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.TrendDao;
import com.ionexchange.Database.Entity.TrendEntity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTrendBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FragmentRoot_Trend extends Fragment implements DataReceiveCallback, OnChartValueSelectedListener {
    FragmentTrendBinding mBinding;
    BaseActivity mActivity;
    ApplicationClass mAppclass;

    TrendDao trendDao;
    InputConfigurationDao inputDao;

    String[] chartTypeArr = {"Line Chart", "Histogram Chart", "XY Chart"};
    int selectedChart = 0, differencebetweendays = 6;
    String selectedSensor, selectedSensorTwo;
    HashMap<Integer, LineDataSet> lineDataSet;
    HashMap<Integer, BarDataSet> barDataSet;
    HashMap<Integer, ScatterDataSet> plotDataSet;
    YAxis yAxis, yAxisRight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trend, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = (BaseActivity) getActivity();
        mAppclass = (ApplicationClass) getActivity().getApplication();
        mActivity.changeToolBarVisibility(View.VISIBLE);

        inputDao = ApplicationClass.DB.inputConfigurationDao();
        trendDao = ApplicationClass.DB.trendDao();

        indexChart();
        setAdapter();

        mBinding.trendChartTypeTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChart = i;
                barDataSet = new HashMap<>();
                plotDataSet = new HashMap<>();
                initBarChart();
                initPlotChart();
                filterChartResults();
                inValidateChartData();
            }
        });

        mBinding.trendSensorOneTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSensor = mBinding.trendSensorOneTie.getAdapter().getItem(i).toString().split("-")[0].trim();
                filterChartResults();
                inValidateChartData();
            }
        });

        mBinding.trendSensorTwoTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSensorTwo = mBinding.trendSensorTwoTie.getAdapter().getItem(i).toString().split("-")[0].trim();
                filterChartResults();
                inValidateChartData();
            }
        });
        mBinding.trendFromDateTie.setOnClickListener(View -> {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
            materialDateBuilder.setTitleText("SELECT A FROM DATE");
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    String startDate = DateFormat.format("dd/MM/yyyy", new Date(materialDatePicker.getHeaderText())).toString();
                    mBinding.trendFromDateTie.setText(startDate);
                }
            });
        });

        mBinding.trendToDateTie.setOnClickListener(View -> {
            MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
            materialDateBuilder.setTitleText("SELECT A TO DATE");
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    String endDate = DateFormat.format("dd/MM/yyyy", new Date(materialDatePicker.getHeaderText())).toString();
                    mBinding.trendToDateTie.setText(endDate);
                    if (!mBinding.trendFromDateTie.getText().toString().isEmpty()) {
                        String[] splitStartDate = mBinding.trendFromDateTie.getText().toString().split("/");
                        String[] splitEndDate = mBinding.trendToDateTie.getText().toString().split("/");
                        String startDate = splitStartDate[0] + splitStartDate[1] + splitStartDate[2];
                        String FinalDate = splitEndDate[0] + splitEndDate[1] + splitEndDate[2];
                        if (Integer.parseInt(FinalDate) < Integer.parseInt(startDate)) {
                            Toast.makeText(getContext(), "To Date must be greater than From Date", Toast.LENGTH_SHORT).show();
                            mBinding.trendToDateTie.setText("");
                        }
                    }
                }
            });
        });
        mBinding.filterbutton.setOnClickListener(v -> {
            if (!mBinding.trendFromDateTie.getText().toString().isEmpty()) {
                String[] splitStartDate = mBinding.trendFromDateTie.getText().toString().split("/");
                String[] splitEndDate = mBinding.trendToDateTie.getText().toString().split("/");
                String startDate = splitStartDate[0] + splitStartDate[1] + splitStartDate[2];
                String FinalDate = splitEndDate[0] + splitEndDate[1] + splitEndDate[2];
                if (Integer.parseInt(FinalDate) < Integer.parseInt(startDate)) {
                    Toast.makeText(getContext(), "To Date must be greater than From Date", Toast.LENGTH_SHORT).show();
                    mBinding.trendToDateTie.setText("");
                } else {
                    differencebetweendays = getDateDiffFromNow(mBinding.trendToDateTie.getText().toString(), mBinding.trendFromDateTie.getText().toString());

                    filterChartResults();

                }
            }
        });

    }

    private void setAdapter() {
        if (inputDao.getEnabledSensor() != null) {
            mBinding.trendChartTypeTie.setAdapter(getAdapter(chartTypeArr, getContext()));
            mBinding.trendSensorOneTie.setAdapter(getAdapter(inputDao.getEnabledSensor(), getContext()));
            mBinding.trendSensorTwoTie.setAdapter(getAdapter(inputDao.getEnabledSensor(), getContext()));

        }
    }

    private void filterChartResults() {
        String fromDate = mBinding.trendFromDateTie.getText().toString().isEmpty() ? lessThanAWeek()
                : mBinding.trendFromDateTie.getText().toString();
        String toDate = mBinding.trendToDateTie.getText().toString().isEmpty() ? getCurrentDate()
                : mBinding.trendToDateTie.getText().toString();
        if (differencebetweendays < 7) {
            lessthanOneWeekChart(fromDate, toDate);
        } else if (differencebetweendays >= 7 && differencebetweendays < 14) {
            lessthanTwoWeekChart(fromDate, toDate);
        } else if (differencebetweendays >= 14) {
            morethanTwoWeekChart();
        } else {
            lessthanOneWeekChart(fromDate, toDate);
        }
    }

    private void lessthanOneWeekChart(String fromDate, String toDate) {
        switch (selectedChart) {
            case 0:
                if (selectedSensor != null) {
                    lineDataSet.put(1, getLineData(trendDao.getLessThenOneWeek(fromDate, toDate, formDigits(2, selectedSensor)), 1));
                    setLineChartData();
                }
                if (selectedSensorTwo != null) {
                    lineDataSet.put(2, getLineData(trendDao.getLessThenOneWeek(fromDate, toDate, formDigits(2, selectedSensorTwo)), 2));
                    setLineChartData();
                }
                break;
            case 1:
                if (selectedSensor != null) {
                    barDataSet.put(1, getBarData(trendDao.getLessThenOneWeek(fromDate, toDate, formDigits(2, selectedSensor)), 1));
                    setBarChartData();
                }
               /* if (selectedSensorTwo != null) {
                    barDataSet.put(2, getBarData(trendDao.getLessThenOneWeek(fromDate, toDate, formDigits(2, selectedSensorTwo)), 2));
                    setBarChartData();
                }*/
                break;
            case 2:
                if (selectedSensor != null) {
                    plotDataSet.put(1, getPlotData(trendDao.getLessThenOneWeek(fromDate, toDate, formDigits(2, selectedSensor)), 1));
                    setScatterChartData();
                }
                if (selectedSensorTwo != null) {
                    plotDataSet.put(2, getPlotData(trendDao.getLessThenOneWeek(fromDate, toDate, formDigits(2, selectedSensorTwo)), 2));
                    setScatterChartData();
                }
                break;
        }
    }

    private void lessthanTwoWeekChart(String fromDate, String toDate) {
        switch (selectedChart) {
            case 0:
                if (selectedSensor != null) {
                    lineDataSet.put(1, getLineData(trendDao.getLessThenTwoWeek(fromDate, toDate, formDigits(2, selectedSensor)), 1));
                    setLineChartData();
                }
                if (selectedSensorTwo != null) {
                    lineDataSet.put(2, getLineData(trendDao.getLessThenTwoWeek(fromDate, toDate, formDigits(2, selectedSensorTwo)), 2));
                    setLineChartData();
                }
                break;
            case 1:
                if (selectedSensor != null) {
                    barDataSet.put(1, getBarData(trendDao.getLessThenTwoWeek(fromDate, toDate, formDigits(2, selectedSensor)), 1));
                    setBarChartData();
                }
               /* if (selectedSensorTwo != null) {
                    barDataSet.put(2, getBarData(trendDao.getLessThenTwoWeek(fromDate, toDate, formDigits(2, selectedSensorTwo)), 2));
                    setBarChartData();
                }*/
                break;
            case 2:
                if (selectedSensor != null) {
                    plotDataSet.put(1, getPlotData(trendDao.getLessThenTwoWeek(fromDate, toDate, formDigits(2, selectedSensor)), 1));
                    setScatterChartData();
                }
                if (selectedSensorTwo != null) {
                    plotDataSet.put(2, getPlotData(trendDao.getLessThenTwoWeek(fromDate, toDate, formDigits(2, selectedSensorTwo)), 2));
                    setScatterChartData();
                }
                break;
        }
    }

    private void morethanTwoWeekChart() {
        switch (selectedChart) {
            case 0:
                if (selectedSensor != null) {
                    lineDataSet.put(1, getLineData(trendDao.getMoreThanTwoWeek(formDigits(2, selectedSensor)), 1));
                    setLineChartData();
                }
                if (selectedSensorTwo != null) {
                    lineDataSet.put(2, getLineData(trendDao.getMoreThanTwoWeek(formDigits(2, selectedSensorTwo)), 2));
                    setLineChartData();
                }
                break;
            case 1:
                if (selectedSensor != null) {
                    barDataSet.put(1, getBarData(trendDao.getMoreThanTwoWeek(formDigits(2, selectedSensor)), 1));
                    setBarChartData();
                }
                /*if (selectedSensorTwo != null) {
                    barDataSet.put(2, getBarData(trendDao.getMoreThanTwoWeek(formDigits(2, selectedSensorTwo)), 2));
                    setBarChartData();
                }*/
                break;
            case 2:
                if (selectedSensor != null) {
                    plotDataSet.put(1, getPlotData(trendDao.getMoreThanTwoWeek(formDigits(2, selectedSensor)), 1));
                    setScatterChartData();
                }
                if (selectedSensorTwo != null) {
                    plotDataSet.put(2, getPlotData(trendDao.getMoreThanTwoWeek(formDigits(2, selectedSensorTwo)), 2));
                    setScatterChartData();
                }
                break;
        }
    }

    public int getDateDiffFromNow(String fromdate, String todate) {
        int days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long diff = sdf.parse(fromdate).getTime() - sdf.parse(todate).getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            days = ((int) (long) hours / 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }

    private void inValidateChartData() {
        switch (selectedChart) {
            case 0:
                mBinding.trendLineChart.setVisibility(View.VISIBLE);
                mBinding.trendBarChart.setVisibility(View.GONE);
                mBinding.trendPlotChart.setVisibility(View.GONE);
                mBinding.trendSensorTwoTil.setEnabled(true);
                setLineChartData();
                break;
            case 1:
                mBinding.trendLineChart.setVisibility(View.GONE);
                mBinding.trendBarChart.setVisibility(View.VISIBLE);
                mBinding.trendPlotChart.setVisibility(View.GONE);
                mBinding.trendSensorTwoTil.setEnabled(false);
                setBarChartData();
                break;

            case 2:
                mBinding.trendLineChart.setVisibility(View.GONE);
                mBinding.trendBarChart.setVisibility(View.GONE);
                mBinding.trendPlotChart.setVisibility(View.VISIBLE);
                mBinding.trendSensorTwoTil.setEnabled(true);
                setScatterChartData();
                break;
        }
    }


    private void indexChart() {
        mBinding.trendLineChart.setVisibility(View.VISIBLE);
        mBinding.trendBarChart.setVisibility(View.GONE);
        lineDataSet = new HashMap<>();
        if(inputDao.getEnabledSensor().length > 0) {
            mBinding.trendSensorOneTie.setAdapter(getAdapter(inputDao.getEnabledSensor(), getContext()));
            mBinding.trendChartTypeTie.setText(chartTypeArr[0]);
            mBinding.trendSensorOneTie.setText(mBinding.trendSensorOneTie.getAdapter().getItem(0).toString());
            selectedSensor = mBinding.trendSensorOneTie.getAdapter().getItem(0).toString().split("-")[0].trim();
            initLineChart();
            lineDataSet.put(1, getLineData(trendDao.getLessThenOneWeek(lessThanAWeek(), getCurrentDate(), formDigits(2, selectedSensor)), 1));
            setLineChartData();
            setAdapter();
        }
    }

    private void setLineChartData() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if (lineDataSet.get(1) != null) {
            dataSets.add(lineDataSet.get(1));
        }
        if (lineDataSet.get(2) != null) {
            LimitLine ll1 = new LimitLine(Integer.parseInt(selectedSensorTwo) < 34 ? Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(
                    formDigits(2, selectedSensorTwo)))) : (Float.parseFloat(virtualDAO.getVirtualLowAlarm(Integer.parseInt(
                    formDigits(2, selectedSensorTwo)))
            )), "Low Alarm");
            ll1.setLineWidth(2f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);
            ll1.setLineColor(Color.parseColor("#FF2D00"));

            LimitLine ll2 = new LimitLine(Integer.parseInt(selectedSensorTwo) < 34 ? (Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(
                    formDigits(2, selectedSensorTwo))))) : (Float.parseFloat(virtualDAO.getVirtualHighAlarm(Integer.parseInt(
                    formDigits(2, selectedSensorTwo)))
            )), "High Alarm");
            ll2.setLineWidth(2f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);
            ll2.setLineColor(Color.parseColor("#FF2D00"));

            dataSets.add(lineDataSet.get(2));
            yAxisRight.addLimitLine(ll1);
            yAxisRight.addLimitLine(ll2);
        }
        LineData data = new LineData(dataSets);
        mBinding.trendLineChart.setData(data);
        mBinding.trendLineChart.notifyDataSetChanged();
        mBinding.trendLineChart.invalidate();
    }

    private void setBarChartData() {
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        if (barDataSet.get(1) != null) {
            dataSets.add(barDataSet.get(1));
        }
        if (barDataSet.get(2) != null) {
          /*  LimitLine ll1 = new LimitLine(Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(
                    formDigits(2, mBinding.trendSensorTwoTie.getAdapter().getItem(0).toString().split("-")[0].trim()))
            )), "Low Alarm");
            ll1.setLineWidth(2f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            LimitLine ll2 = new LimitLine(Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(
                    formDigits(2, mBinding.trendSensorTwoTie.getAdapter().getItem(0).toString().split("-")[0].trim()))
            )), "High Alarm");
            ll2.setLineWidth(2f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);*/

            dataSets.add(barDataSet.get(2));
            /*yAxisRight.addLimitLine(ll1);
            yAxisRight.addLimitLine(ll2);*/
        }
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.01f);
        mBinding.trendBarChart.setData(data);
        mBinding.trendBarChart.notifyDataSetChanged();
        mBinding.trendBarChart.invalidate();
    }

    private void setScatterChartData() {
        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        if (plotDataSet.get(1) != null) {
            dataSets.add(plotDataSet.get(1));
        }
        if (plotDataSet.get(2) != null) {
          /*  LimitLine ll1 = new LimitLine(Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(
                    formDigits(2, mBinding.trendSensorTwoTie.getAdapter().getItem(0).toString().split("-")[0].trim()))
            )), "Low Alarm");
            ll1.setLineWidth(2f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            LimitLine ll2 = new LimitLine(Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(
                    formDigits(2, mBinding.trendSensorTwoTie.getAdapter().getItem(0).toString().split("-")[0].trim()))
            )), "High Alarm");
            ll2.setLineWidth(2f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);*/

            dataSets.add(plotDataSet.get(2));
            /*yAxisRight.addLimitLine(ll1);
            yAxisRight.addLimitLine(ll2);*/
        }
        ScatterData data = new ScatterData(dataSets);
        mBinding.trendPlotChart.setData(data);
        mBinding.trendPlotChart.notifyDataSetChanged();
        mBinding.trendPlotChart.invalidate();
    }

    private void initLineChart() {
        LineChart chart = mBinding.trendLineChart;
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setGridBackgroundColor(Color.WHITE);
        XAxis xAxis;
        xAxis = chart.getXAxis();

        yAxis = chart.getAxisLeft();
        yAxisRight = chart.getAxisRight();
        chart.getAxisRight().setEnabled(true);


        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridLineWidth(0.5f);

        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setGridLineWidth(0.5f);

        LimitLine ll1 = new LimitLine(Integer.parseInt(selectedSensor) < 34 ?
                (Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(formDigits(2,selectedSensor))))) :
                (Float.parseFloat(virtualDAO.getVirtualLowAlarm(Integer.parseInt(formDigits(2,selectedSensor))))), "Low Alarm");
        ll1.setLineWidth(2f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll1.setTextSize(10f);
        ll1.setLineColor(Color.RED);
        LimitLine ll2 = new LimitLine(Integer.parseInt(selectedSensor) < 34 ?
                (Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(formDigits(2,selectedSensor))))) :
                (Float.parseFloat(virtualDAO.getVirtualHighAlarm(Integer.parseInt(formDigits(2,selectedSensor))))), "High Alarm");
        ll2.setLineWidth(2f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(Color.RED);

        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);

        chart.animateX(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void initBarChart() {
        BarChart chart = mBinding.trendBarChart;
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setGridBackgroundColor(Color.WHITE);
        XAxis xAxis;
        xAxis = chart.getXAxis();

        yAxis = chart.getAxisLeft();
        yAxisRight = chart.getAxisRight();
        chart.getAxisRight().setEnabled(false);

        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridLineWidth(0.5f);

        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setGridLineWidth(0.5f);

        /*LimitLine ll1 = new LimitLine(Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(formDigits(2,selectedSensor)))), "Low Alarm");
        ll1.setLineWidth(2f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(formDigits(2,selectedSensor)))), "High Alarm");
        ll2.setLineWidth(2f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);*/

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);
        chart.getXAxis().setSpaceMax(0.5f);
        chart.animateX(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void initPlotChart() {
        ScatterChart chart = mBinding.trendPlotChart;
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setGridBackgroundColor(Color.WHITE);
        XAxis xAxis;
        xAxis = chart.getXAxis();

        yAxis = chart.getAxisLeft();
        yAxisRight = chart.getAxisRight();
        chart.getAxisRight().setEnabled(false);


        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridLineWidth(0.5f);

        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setGridLineWidth(0.5f);

        /*LimitLine ll1 = new LimitLine(Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(formDigits(2,selectedSensor)))), "Low Alarm");
        ll1.setLineWidth(2f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(formDigits(2,selectedSensor)))), "High Alarm");
        ll2.setLineWidth(2f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);*/

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);
        chart.animateX(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private LineDataSet getLineData(List<TrendEntity> list, int pos) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            float val = Float.parseFloat(list.get(i).keepValue);
            values.add(new Entry(Float.parseFloat(list.get(i).time.split("\\.")[0] + "." + list.get(i).time.split("\\.")[1]), val, getResources().getDrawable(R.drawable.circle_bg)));
        }
        LineDataSet set1;
        set1 = pos == 1 ? new LineDataSet(values, Integer.parseInt(selectedSensor) < 34 ?
                inputDao.getInputType(Integer.parseInt(formDigits(2, selectedSensor))) :
                "Virtual") :
                new LineDataSet(values, Integer.parseInt(selectedSensorTwo) < 34 ?
                        inputDao.getInputType(Integer.parseInt(formDigits(2, selectedSensorTwo))) :
                        "Virtual");

        set1.setDrawIcons(false);

        set1.setColor(pos == 1 ? Color.parseColor("#0097DB") : Color.parseColor("#ACE4C4"));
        set1.setCircleColor(pos == 1 ? Color.parseColor("#0097DB") : Color.parseColor("#ACE4C4"));
        set1.setLineWidth(3f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setAxisDependency(pos == 1 ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT);
        return set1;
    }

    private BarDataSet getBarData(List<TrendEntity> list, int pos) {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            float val = Float.parseFloat(list.get(i).keepValue);
            values.add(new BarEntry(Float.parseFloat(list.get(i).time.split("\\.")[0] + "." + list.get(i).time.split("\\.")[1]), val, getResources().getDrawable(R.drawable.circle_bg)));
        }
        BarDataSet set1;
        set1 = pos == 1 ? new BarDataSet(values, Integer.parseInt(selectedSensor) < 34 ?
                inputDao.getInputType(Integer.parseInt(formDigits(2, selectedSensor))) :
                "Virtual") :
                new BarDataSet(values, Integer.parseInt(selectedSensorTwo) < 34 ?
                        inputDao.getInputType(Integer.parseInt(formDigits(2, selectedSensorTwo))) :
                        "Virtual");
        set1.setDrawIcons(false);

        set1.setColor(pos == 1 ? Color.parseColor("#0097DB") : Color.parseColor("#ACE4C4"));
        //set1.setValueTextColor(pos == 1 ? Color.parseColor("#0097DB") : Color.parseColor("#ACE4C4"));
        set1.setBarBorderWidth(0.5f);
        //set1.setCircleRadius(3f);
        //set1.setDrawCircleHole(false);
        //set1.setValueTextSize(9f);
        set1.setAxisDependency(pos == 1 ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT);
        return set1;
    }

    private ScatterDataSet getPlotData(List<TrendEntity> list, int pos) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            float val = Float.parseFloat(list.get(i).keepValue);
            values.add(new Entry(Float.parseFloat(list.get(i).time.split("\\.")[0] + "." + list.get(i).time.split("\\.")[1]), val, getResources().getDrawable(R.drawable.circle_bg)));
        }
        ScatterDataSet set1;
        set1 = pos == 1 ? new ScatterDataSet(values, Integer.parseInt(selectedSensor) < 34 ?
                inputDao.getInputType(Integer.parseInt(formDigits(2, selectedSensor))) :
                "Virtual") :
                new ScatterDataSet(values, Integer.parseInt(selectedSensorTwo) < 34 ?
                        inputDao.getInputType(Integer.parseInt(formDigits(2, selectedSensorTwo))) :
                        "Virtual");
        set1.setDrawIcons(false);

        set1.setColor(pos == 1 ? Color.parseColor("#0097DB") : Color.parseColor("#ACE4C4"));
        //set1.setValueTextColor(pos == 1 ? Color.parseColor("#0097DB") : Color.parseColor("#ACE4C4"));
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set1.setScatterShapeSize(8f);
        //set1.setCircleRadius(3f);
        //set1.setDrawCircleHole(false);
        //set1.setValueTextSize(9f);
        set1.setAxisDependency(pos == 1 ? YAxis.AxisDependency.LEFT : YAxis.AxisDependency.RIGHT);
        return set1;
    }

    @Override
    public void OnDataReceive(String data) {
        Log.e("TAG", "OnDataReceive: " + data);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
    }
}
