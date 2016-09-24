package com.seat.sw_maestro.seat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private static final String TAG = "Tab2";
    String graphType;   // 그래프 표현 방식

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("SettingStatus", Context.MODE_PRIVATE);
        graphType = prefs.getString("prefGraphList","0");   // 그래프의 표현 방식 / 0 - 꺾은선 그래프 / 1 - 막대 그래프 / 디폴트는 꺾은선 그래프로 표시
        Log.d(TAG, "그래프 표현 방식 : " + graphType);

        View v;
        if(graphType.equals("0")) { // 꺾은선 그래프
            v = inflater.inflate(R.layout.tab_2_line, container, false);
        }
        else if(graphType.equals("1")) { // 막대 그래프
            v = inflater.inflate(R.layout.tab_2_bar, container, false);
        }
        else{ // 디폴트는 꺾은선 그래프
            v = inflater.inflate(R.layout.tab_2_line, container, false);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        switch(graphType){
            case "0" :  // 꺾은선 그래프
                LineChart lineChart_time = (LineChart) getView().findViewById(R.id.lineChart_time);
                LineChart lineChart_accuracy = (LineChart) getView().findViewById(R.id.lineChart_accuracy);

                // 시간
                ArrayList<Entry> lineChart_time_entries = new ArrayList<>();
                lineChart_time_entries.add(new Entry(4f, 0));
                lineChart_time_entries.add(new Entry(8f, 1));
                lineChart_time_entries.add(new Entry(6f, 2));
                lineChart_time_entries.add(new Entry(2f, 3));
                lineChart_time_entries.add(new Entry(18f, 4));
                lineChart_time_entries.add(new Entry(9f, 5));

                LineDataSet lineChart_time_dataset = new LineDataSet(lineChart_time_entries, "# of Calls");

                ArrayList<String> lineChart_time_labels = new ArrayList<String>();
                lineChart_time_labels.add("January");
                lineChart_time_labels.add("February");
                lineChart_time_labels.add("March");
                lineChart_time_labels.add("April");
                lineChart_time_labels.add("May");
                lineChart_time_labels.add("June");

                LineData lineChart_time_data = new LineData(lineChart_time_labels, lineChart_time_dataset);
                lineChart_time_dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
                lineChart_time_dataset.setDrawFilled(true);

                lineChart_time.setData(lineChart_time_data);
                lineChart_time.animateY(5000);

                // 정확도
                ArrayList<Entry> lineChart_accuracy_entries = new ArrayList<>();
                lineChart_accuracy_entries.add(new Entry(4f, 0));
                lineChart_accuracy_entries.add(new Entry(8f, 1));
                lineChart_accuracy_entries.add(new Entry(6f, 2));
                lineChart_accuracy_entries.add(new Entry(2f, 3));
                lineChart_accuracy_entries.add(new Entry(18f, 4));
                lineChart_accuracy_entries.add(new Entry(9f, 5));

                LineDataSet lineChart_accuracy_dataset = new LineDataSet(lineChart_accuracy_entries, "# of Calls");

                ArrayList<String> lineChart_accuracy_labels = new ArrayList<String>();
                lineChart_accuracy_labels.add("January");
                lineChart_accuracy_labels.add("February");
                lineChart_accuracy_labels.add("March");
                lineChart_accuracy_labels.add("April");
                lineChart_accuracy_labels.add("May");
                lineChart_accuracy_labels.add("June");

                LineData lineChart_accuracy_data = new LineData(lineChart_accuracy_labels, lineChart_accuracy_dataset);
                lineChart_accuracy_dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
                lineChart_accuracy_dataset.setDrawFilled(true);

                lineChart_accuracy.setData(lineChart_accuracy_data);
                lineChart_accuracy.animateY(5000);
                break;

            case "1" :
                BarChart barChart_time = (BarChart) getView().findViewById(R.id.barChart_time);
                BarChart barChart_accuracy = (BarChart) getView().findViewById(R.id.barChart_accuracy);

                // 시간
                ArrayList<BarEntry> barChart_time_entries = new ArrayList<>();
                barChart_time_entries.add(new BarEntry(4f, 0));
                barChart_time_entries.add(new BarEntry(8f, 1));
                barChart_time_entries.add(new BarEntry(6f, 2));
                barChart_time_entries.add(new BarEntry(2f, 3));
                barChart_time_entries.add(new BarEntry(18f, 4));
                barChart_time_entries.add(new BarEntry(9f, 5));

                BarDataSet barChart_time_dataset = new BarDataSet(barChart_time_entries, "# of Calls");

                ArrayList<String> barChart_time_labels = new ArrayList<String>();
                barChart_time_labels.add("January");
                barChart_time_labels.add("February");
                barChart_time_labels.add("March");
                barChart_time_labels.add("April");
                barChart_time_labels.add("May");
                barChart_time_labels.add("June");

                BarData barChart_time_data = new BarData(barChart_time_labels, barChart_time_dataset);
                barChart_time_dataset.setColors(ColorTemplate.COLORFUL_COLORS); //

                barChart_time.setData(barChart_time_data);
                barChart_time.animateY(5000);

                // 정확도
                ArrayList<BarEntry> barChart_accuracy_entries = new ArrayList<>();
                barChart_accuracy_entries.add(new BarEntry(44f, 0));
                barChart_accuracy_entries.add(new BarEntry(68f, 1));
                barChart_accuracy_entries.add(new BarEntry(-63f, 2));
                barChart_accuracy_entries.add(new BarEntry(25f, 3));
                barChart_accuracy_entries.add(new BarEntry(148f, 4));
                barChart_accuracy_entries.add(new BarEntry(92f, 5));

                BarDataSet barChart_accuracy_dataset = new BarDataSet(barChart_accuracy_entries, "# of Calls");

                ArrayList<String> barChart_accuracy_labels = new ArrayList<String>();
                barChart_accuracy_labels.add("January");
                barChart_accuracy_labels.add("February");
                barChart_accuracy_labels.add("March");
                barChart_accuracy_labels.add("April");
                barChart_accuracy_labels.add("May");
                barChart_accuracy_labels.add("June");

                BarData barChart_accuracy_data = new BarData(barChart_accuracy_labels, barChart_accuracy_dataset);
                barChart_accuracy_dataset.setColors(ColorTemplate.COLORFUL_COLORS); //

                barChart_accuracy.setData(barChart_accuracy_data);
                barChart_accuracy.animateY(5000);
                break;
        }
        super.onActivityCreated(savedInstanceState);
        // 뷰에 데이터를 넣는 작업 등을 할 추가할 수 있음
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        // 이때 세팅 이후 그래프의 모양과 관련된 것을 바꾸면 되겠지?
        super.onStart();
    }
}