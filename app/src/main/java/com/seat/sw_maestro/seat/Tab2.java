package com.seat.sw_maestro.seat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Tab2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        System.out.println("onActivityCreated 호출2");
        LineChart lineChart;
        BarChart barChart;
        lineChart = (LineChart) getView().findViewById(R.id.linechart1);
        barChart = (BarChart) getView().findViewById(R.id.barchart1);

        // 라인차트
        ArrayList<Entry> lineChart_entries = new ArrayList<>();
        lineChart_entries.add(new Entry(4f, 0));
        lineChart_entries.add(new Entry(8f, 1));
        lineChart_entries.add(new Entry(6f, 2));
        lineChart_entries.add(new Entry(2f, 3));
        lineChart_entries.add(new Entry(18f, 4));
        lineChart_entries.add(new Entry(9f, 5));

        LineDataSet lineChart_dataset = new LineDataSet(lineChart_entries, "# of Calls");

        ArrayList<String> lineChart_labels = new ArrayList<String>();
        lineChart_labels.add("January");
        lineChart_labels.add("February");
        lineChart_labels.add("March");
        lineChart_labels.add("April");
        lineChart_labels.add("May");
        lineChart_labels.add("June");

        LineData lineChart_data = new LineData(lineChart_labels, lineChart_dataset);
        lineChart_dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        lineChart_dataset.setDrawFilled(true);

        lineChart.setData(lineChart_data);
        lineChart.animateY(5000);

        // 바 차트
        ArrayList<BarEntry> barChart_entries = new ArrayList<>();
        barChart_entries.add(new BarEntry(4f, 0));
        barChart_entries.add(new BarEntry(8f, 1));
        barChart_entries.add(new BarEntry(6f, 2));
        barChart_entries.add(new BarEntry(12f, 3));
        barChart_entries.add(new BarEntry(18f, 4));
        barChart_entries.add(new BarEntry(9f, 5));

        BarDataSet barChart_dataset = new BarDataSet(barChart_entries, "# of Calls");

        ArrayList<String> barChart_labels = new ArrayList<String>();
        barChart_labels.add("January");
        barChart_labels.add("February");
        barChart_labels.add("March");
        barChart_labels.add("April");
        barChart_labels.add("May");
        barChart_labels.add("June");

        BarData barChart_data = new BarData(barChart_labels, barChart_dataset);
        barChart_dataset.setColors(ColorTemplate.COLORFUL_COLORS); //

        barChart.setData(barChart_data);
        // 끝

        super.onActivityCreated(savedInstanceState);

        // 뷰에 데이터를 넣는 작업 등을 할 추가할 수 있음
    }
}