package com.ionexchange.Fragments.Trend;

import static com.ionexchange.Others.ApplicationClass.formDigits;
import static com.ionexchange.Others.ApplicationClass.getAdapter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ionexchange.Activity.BaseActivity;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.TrendDao;
import com.ionexchange.Database.Entity.TrendEntity;
import com.ionexchange.Interface.DataReceiveCallback;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentTrendBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentRoot_Trend extends Fragment implements DataReceiveCallback, OnChartValueSelectedListener {
    FragmentTrendBinding mBinding;
    BaseActivity mActivity;
    ApplicationClass mAppclass;

    TrendDao trendDao;
    InputConfigurationDao inputDao;

    String[] chartTypeArr = {"Line Chart", "Histogram Chart", "XY Chart"};
    int selectedChart = 0;
    String selectedSensor;
    HashMap<Integer, LineDataSet> lineDataSet;
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

        mBinding.trendChartTypeTie.setAdapter(getAdapter(chartTypeArr, getContext()));
        mBinding.trendSensorOneTie.setAdapter(getAdapter(inputDao.getEnabledSensor(), getContext()));
        mBinding.trendSensorTwoTie.setAdapter(getAdapter(inputDao.getEnabledSensor(), getContext()));

        mBinding.trendChartTypeTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChart = i;
                inValidateChartData();
            }
        });

        mBinding.trendSensorOneTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSensor = mBinding.trendSensorOneTie.getAdapter().getItem(i).toString().split("-")[0].trim();
                lineDataSet.put(1, getLineData(trendDao.getTrendList(selectedSensor), 1));
                inValidateChartData();
            }
        });

        mBinding.trendSensorTwoTie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSensor = mBinding.trendSensorOneTie.getAdapter().getItem(i).toString().split("-")[0].trim();
                lineDataSet.put(2, getLineData(trendDao.getTrendList(selectedSensor), 2));
                inValidateChartData();
            }
        });
    }

    private void inValidateChartData() {
        switch (selectedChart) {
            case 0:
                setLineChartData();
                break;

            case 1:

                break;

            case 2:

                break;
        }
    }

    private void indexChart() {
        lineDataSet = new HashMap<>();
        initLineChart();
        mBinding.trendSensorOneTie.setAdapter(getAdapter(inputDao.getEnabledSensor(), getContext()));
        mBinding.trendChartTypeTie.setText(chartTypeArr[0]);
       // mBinding.trendSensorOneTie.setText(mBinding.trendSensorOneTie.getAdapter().getItem(0).toString());
        //selectedSensor = mBinding.trendSensorOneTie.getAdapter().getItem(0).toString().split("-")[0].trim();
        lineDataSet.put(1, getLineData(trendDao.getTrendList(selectedSensor), 1));
        setLineChartData();
    }

    private void setLineChartData() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        if (lineDataSet.get(1) != null) {
            dataSets.add(lineDataSet.get(1));
        }
        if (lineDataSet.get(2) != null) {
           /* LimitLine ll1 = new LimitLine(Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(
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
            ll2.setTextSize(10f);
*/
            dataSets.add(lineDataSet.get(2));
           // yAxisRight.addLimitLine(ll1);
           // yAxisRight.addLimitLine(ll2);
        }
        LineData data = new LineData(dataSets);
        mBinding.trendLineChart.setData(data);
        mBinding.trendLineChart.notifyDataSetChanged();
        mBinding.trendLineChart.invalidate();
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

      /*  LimitLine ll1 = new LimitLine(Float.parseFloat(inputDao.getHighAlarm(Integer.parseInt(selectedSensor))), "Low Alarm");
        ll1.setLineWidth(2f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(Float.parseFloat(inputDao.getLowAlarm(Integer.parseInt(selectedSensor))), "High Alarm");
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
        set1 = new LineDataSet(values, "tempInput");
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
