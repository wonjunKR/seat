package com.seat.sw_maestro.seat;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class GraphManager {
    final static String TAG = "GraphManager";
    public GraphManager() { // 생성자

    }

    /*
    String형의 labels 배열을 넣으면 공용 어레이 리스트를 만들어서 리턴해주는 함수
     */
    public ArrayList<String> makeChartLabel(String[] labels){
        ArrayList<String> entries = new ArrayList<>();
        for(int i = 0; i < labels.length; i++){
            entries.add(labels[i]);
        }
        return entries;
    }

    /*
    float형의 데이터와 int형의 인덱스 배열을 넣으면 *라인차트용* 어레이 리스트를 만들어서 리턴해주는 함수
     */
    public ArrayList<Entry> makeLineChartEntry(float[] datas, int[] index){
        ArrayList<Entry> lineChart_entries = new ArrayList<>();
        for(int i = 0; i < index.length; i++){
            lineChart_entries.add(new Entry(datas[i], index[i]));
        }
        return lineChart_entries;
    }

    /*
    위의 두 과정(데이터와 라벨을 만드는 것)을 바탕으로 이후에 최종 데이터 셋을 만드는 함수(라인차트용)/ 인자는 데이터, 인덱스, 라벨, 표 아래에 보이는 글씨..
     */
    public LineData makeLineData(float[] datas, int[] index, String[] labels, String description){
        ArrayList<Entry> lineChart_entries = makeLineChartEntry(datas,index);
        ArrayList<String> lineChart_labels = makeChartLabel(labels);
        LineDataSet lineChart_dataSet = new LineDataSet(lineChart_entries, description);    // description -> 표 아래에 보이는 글씨
        LineData lineChart_data = new LineData(lineChart_labels, lineChart_dataSet);
        lineChart_dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineChart_dataSet.setDrawFilled(true);

        return lineChart_data;
    }

    /*
   float형의 데이터와 int형의 인덱스 배열을 넣으면 *바차트용* 어레이 리스트를 만들어서 리턴해주는 함수
    */
    public ArrayList<BarEntry> makeBarChartEntry(float[] datas, int[] index){
        ArrayList<BarEntry> barChart_entries = new ArrayList<>();
        for(int i = 0; i < index.length; i++){
            barChart_entries.add(new BarEntry(datas[i], index[i]));
        }
        return barChart_entries;
    }

    /*
    위의 두 과정(데이터와 라벨을 만드는 것)을 바탕으로 이후에 최종 데이터 셋을 만드는 함수(바차트용)/ 인자는 데이터, 인덱스, 라벨, 표 아래에 보이는 글씨..
     */
    public BarData makeBarData(float[] datas, int[] index, String[] labels, String description){
        ArrayList<BarEntry> barChart_entries = makeBarChartEntry(datas,index);
        ArrayList<String> barChart_labels = makeChartLabel(labels);
        BarDataSet barChart_dataSet = new BarDataSet(barChart_entries, description);    // description -> 표 아래에 보이는 글씨
        BarData barChart_data = new BarData(barChart_labels, barChart_dataSet);
        barChart_dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        return barChart_data;
    }
}