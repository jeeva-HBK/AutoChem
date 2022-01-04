package com.ionexchange.Fragments.MainScreen;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.chip.ChipGroup;
import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Dao.TrendDao;
import com.ionexchange.Database.Dao.VirtualConfigurationDao;
import com.ionexchange.Database.Entity.TrendEntity;
import com.ionexchange.Others.ApplicationClass;
import com.ionexchange.R;
import com.ionexchange.databinding.FragmentSensorStatisticsBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class FragmentSensorStatistics extends Fragment implements OnChartValueSelectedListener {

    FragmentSensorStatisticsBinding mBinding;
    LineChart chart;
    String inputNumber, inputType;
    TrendDao trendDao;
    InputConfigurationDao inputDao;
    VirtualConfigurationDao virtualDao;
    ArrayList<Entry> values;

    public FragmentSensorStatistics(String inputNumber, String inputType) {
        this.inputNumber = ApplicationClass.formDigits(2, inputNumber);
        this.inputType = inputType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_sensor_statistics, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.sensorName.setText(inputType + " Sensor");
        trendDao = ApplicationClass.DB.trendDao();
        inputDao = ApplicationClass.DB.inputConfigurationDao();
        virtualDao = ApplicationClass.DB.virtualConfigurationDao();
        chart = mBinding.lineChart;

        mBinding.lessThanWeek.setChecked(true);

        mBinding.chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.lessThanWeek:
                        initChart(trendDao.getLessThenOneWeek(ApplicationClass.getCurrentDate(),
                                lessThanAWeek(), inputNumber));
                        break;

                    case R.id.lessThanTwoWeek:
                        initChart(trendDao.getLessThenTwoWeek(ApplicationClass.getCurrentDate(),
                                lessThanTwoWeek(), inputNumber));
                        break;

                    case R.id.greaterThanTwoWeek:
                        initChart(trendDao.getMoreThanTwoWeek(inputNumber));
                        break;
                }
            }
        });

        trendDao.getTrendLiveList(inputNumber).observe(getViewLifecycleOwner(), new Observer<List<TrendEntity>>() {
            @Override
            public void onChanged(List<TrendEntity> list) {
                if (mBinding.lessThanWeek.isChecked()){
                    setData(list);
                }
            }
        });
    }

    String lessThanAWeek() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date sevenDaysAgo = cal.getTime();
        return ApplicationClass.formatDate(sevenDaysAgo);
    }

    String lessThanTwoWeek() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -14);
        Date sevenDaysAgo = cal.getTime();
        return ApplicationClass.formatDate(sevenDaysAgo);
    }

    private void initChart(List<TrendEntity> dataSet) {
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
        YAxis yAxis;
        yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(true);

        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridLineWidth(0.5f);

        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setGridLineWidth(0.5f);

        LimitLine ll1 = new LimitLine(getAlarm(1), "High Alarm");
        ll1.setLineWidth(2f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(getAlarm(0), "Low Alarm");
        ll2.setLineWidth(2f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);

        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);
        setData(dataSet);
        chart.animateX(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private float getAlarm(int type) {
        if (Integer.parseInt(inputNumber) > 49) {
            return Float.parseFloat(
                    type == 0 ?
                            virtualDao.getLowAlarm(Integer.parseInt(inputNumber)) :
                            virtualDao.getHighAlarm(Integer.parseInt(inputNumber)));
        }
        return
                Float.parseFloat(
                        type == 0 ?
                                inputDao.getLowAlarm(Integer.parseInt(inputNumber)) :
                                inputDao.getHighAlarm(Integer.parseInt(inputNumber)));
    }

    private void setData(List<TrendEntity> list) {
        values = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            float val = Float.parseFloat(list.get(i).keepValue);
            values.add(new Entry(Float.parseFloat(list.get(i).time.split("\\.")[0] + "." + list.get(i).time.split("\\.")[1]), val, getResources().getDrawable(R.drawable.circle_bg)));
        }

        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, inputType);
            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setValueTextSize(9f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });
            set1.setFillColor(getResources().getColor(R.color.primary));
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
        mBinding.lineChart.notifyDataSetChanged();
        mBinding.lineChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) { }

    @Override
    public void onNothingSelected() { }
}




